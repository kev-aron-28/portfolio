package com.projects.job_tracker.presentation.api;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.jayway.jsonpath.JsonPath;
import com.projects.job_tracker.application.observatory.*;
import com.projects.job_tracker.domain.observatory.*;
import com.projects.job_tracker.domain.observatory.ScheduleModels.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:schedule-tests;MODE=PostgreSQL;DB_CLOSE_DELAY=-1","observatory.automation.lease-seconds=30","observatory.automation.max-runtime-seconds=120"})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(ObservatoryCollectionIntegrationTest.FixtureConfig.class)
public class ObservatoryScheduleIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObservatoryScheduleService schedules;
    @Autowired ObservatoryScheduleRepository repository;
    @Autowired ObservatoryScheduleWorker worker;
    @Autowired RunCollectionService collection;
    @Autowired CaptureService captures;
    @Autowired CollectionControlService control;
    @Autowired CaptureImportService importer;
    @Autowired ObservatoryMaintenanceService maintenance;
    @Autowired ActiveCollections active;
    @Autowired com.projects.job_tracker.infrastructure.observatory.automation.ObservatoryAutomationSettings settings;
    @Autowired ObservatoryCollectionIntegrationTest.FixtureCollector collector;
    private static final String BASE="/api/observatory";
    @BeforeEach void isolate() {
        for (var schedule:schedules.list()) if (schedule.version().config().enabled()) {
            var c=schedule.version().config();
            schedules.save(schedule.version().id(),new ObservatoryScheduleService.Edit(schedule.version().revision(),null,
                new Config(c.name(),c.protocolId(),c.protocolRevision(),false,c.startAt(),c.maxRetries(),c.backoffMinutes()),"Pause previous isolated fixture"));
        }
        for (UUID id:active.snapshot().keySet()) active.stop(id);
        collector.mode="SUCCESS"; collector.calls.set(0); collector.suffix=UUID.randomUUID().toString().replace("-",""); collector.entered=null; collector.release=null;
    }
    private Instant now() { return Instant.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS); }
    private String postJson(String path,String payload) throws Exception {
        return mvc.perform(post(BASE+path).contentType(MediaType.APPLICATION_JSON).content(payload)).andExpect(status().is2xxSuccessful()).andReturn().getResponse().getContentAsString();
    }
    private UUID protocol(int interval) throws Exception {
        String segment=JsonPath.read(postJson("/segments","{\"spec\":{\"name\":\"Backend\",\"roleFamily\":\"Backend\"},\"reason\":\"Fixture\"}"),"$.id");
        String result=postJson("/protocols","""
            {"spec":{"segmentId":"%s","segmentRevision":1,"name":"Indeed fixture","source":"indeed","query":"Java",
            "filters":"Ubicación: sin filtro; modalidad: sin filtro; antigüedad: sin filtro","intervalMinutes":%d,"resultLimit":10,
            "ordering":"Orden del portal","pagination":"Primera página","samplingMode":"EXPLORATORY","collectionPlan":{"platform":"INDEED"}},"reason":"Fixture"}
            """.formatted(segment,interval));
        return UUID.fromString(JsonPath.read(result,"$.id"));
    }
    private Schedule create(UUID protocol,Instant start,boolean enabled,int retries) {
        return schedules.save(null,new ObservatoryScheduleService.Edit(null,UUID.randomUUID(),new Config("Horario fixture",protocol,1,enabled,start,retries,1),"Fixture inicial"));
    }
    private Task claim(Instant at) { return schedules.claimDue(at).orElseThrow(); }
    private Task task(UUID id) { return repository.task(id,false).orElseThrow(); }
    private List<Task> pending(UUID id) { return schedules.tasks(id,100,0).stream().filter(t -> t.state()==TaskState.PENDING).toList(); }
    private void executeAt(Task task,Instant at) {
        var bound=schedules.bind(task.id(),task.leaseToken(),at).orElseThrow();
        collection.collect(bound.runId()); schedules.complete(task.id(),task.leaseToken(),at.plusMillis(1));
    }
    @Test void createIsIdempotentAndPausedConfigurationsDoNotDispatch() throws Exception {
        UUID protocol=protocol(60),key=UUID.randomUUID(); var config=new Config("Pausado",protocol,1,false,now(),2,1);
        var edit=new ObservatoryScheduleService.Edit(null,key,config,"Crear pausado");
        var first=schedules.save(null,edit); var second=schedules.save(null,edit);
        assertThat(first.version().id()).isEqualTo(second.version().id()); assertThat(schedules.claimDue(now())).isEmpty();
        mvc.perform(get(BASE+"/schedules/"+first.version().id())).andExpect(status().isOk()).andExpect(jsonPath("$.version.config.enabled").value(false));
        mvc.perform(get(BASE+"/automation")).andExpect(jsonPath("$.enabled").value(false));
        mvc.perform(get("/observatory/schedules")).andExpect(status().isOk()).andExpect(content().string(org.hamcrest.Matchers.containsString("schedule-form")));
        assertThatThrownBy(()->schedules.save(null,new ObservatoryScheduleService.Edit(null,key,new Config("Otro",protocol,1,false,config.startAt(),2,1),"Crear pausado"))).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void workerCreatesOneCaptureAndIdempotentBindingDoesNotRepeatEvidence() throws Exception {
        Instant time=now(); var schedule=create(protocol(60),time.minusSeconds(1),true,2); Task task=claim(time);
        worker.execute(task.id(),task.leaseToken());
        assertThat(task(task.id()).state()).isEqualTo(TaskState.SUCCEEDED);
        UUID run=task(task.id()).runId(); assertThat(captures.run(run).state()).isEqualTo(CaptureModels.RunState.PARTIAL);
        assertThat(captures.observations(run)).hasSize(1); assertThat(collector.calls.get()).isEqualTo(1);
        worker.execute(task.id(),task.leaseToken()); schedules.complete(task.id(),task.leaseToken(),now());
        assertThat(collector.calls.get()).isEqualTo(1); assertThat(schedules.get(schedule.version().id()).activeTaskId()).isNull();
        assertThat(schedules.get(schedule.version().id()).lastRunId()).isEqualTo(run);
    }
    @Test void retriesBackOffHaveAnUpperBoundAndKeepTheirOriginalWindow() throws Exception {
        collector.mode="EMPTY"; Instant time=now(); var schedule=create(protocol(60),time.minusSeconds(1),true,2);
        Task initial=claim(time); executeAt(initial,time);
        Task firstRetry=pending(schedule.version().id()).getFirst();
        assertThat(firstRetry.retryNumber()).isEqualTo(1); assertThat(firstRetry.scheduledFor()).isEqualTo(initial.scheduledFor());
        assertThat(firstRetry.dueAt()).isAfterOrEqualTo(time.plusSeconds(60)); assertThat(schedules.claimDue(firstRetry.dueAt().minusNanos(1000))).isEmpty();
        Task firstClaim=claim(firstRetry.dueAt()); executeAt(firstClaim,firstRetry.dueAt());
        Task secondRetry=pending(schedule.version().id()).getFirst();
        assertThat(secondRetry.retryNumber()).isEqualTo(2); assertThat(secondRetry.dueAt()).isAfterOrEqualTo(firstRetry.dueAt().plusSeconds(120));
        Task secondClaim=claim(secondRetry.dueAt()); executeAt(secondClaim,secondRetry.dueAt());
        assertThat(pending(schedule.version().id())).isEmpty(); assertThat(schedules.tasks(schedule.version().id(),100,0)).hasSize(3);
        assertThat(collector.calls.get()).isEqualTo(3); assertThat(schedules.get(schedule.version().id()).consecutiveFailures()).isEqualTo(3);
    }
    @Test void evidenceAndInvalidImportsDoNotTriggerAutomaticRetry() throws Exception {
        Instant time=now(); collector.mode="INVALID"; var invalid=create(protocol(60),time.minusSeconds(1),true,2);
        Task failed=claim(time); executeAt(failed,time);
        assertThat(task(failed.id()).state()).isEqualTo(TaskState.FAILED); assertThat(pending(invalid.version().id())).isEmpty();
        collector.mode="THROW"; var partial=create(protocol(60),time.minusSeconds(1),true,2);
        Task interrupted=claim(time); executeAt(interrupted,time);
        assertThat(task(interrupted.id()).state()).isEqualTo(TaskState.SUCCEEDED); assertThat(pending(partial.version().id())).isEmpty();
        assertThat(captures.run(task(interrupted.id()).runId()).state()).isEqualTo(CaptureModels.RunState.PARTIAL);
    }
    @Test void editingOrPausingDoesNotRewriteAnActiveTaskOrRetryTheOldVersion() throws Exception {
        Instant time=now(); UUID protocol=protocol(60); var schedule=create(protocol,time.minusSeconds(1),true,2);
        Task initial=claim(time); Task bound=schedules.bind(initial.id(),initial.leaseToken(),time).orElseThrow();
        var old=schedule.version().config();
        var paused=schedules.save(schedule.version().id(),new ObservatoryScheduleService.Edit(1,null,new Config("Pausado nuevo",protocol,1,false,time.plusSeconds(3600),2,1),"Pausar sin borrar tarea"));
        collector.mode="EMPTY"; collection.collect(bound.runId()); schedules.complete(initial.id(),initial.leaseToken(),now());
        assertThat(task(initial.id()).scheduleRevision()).isEqualTo(1); assertThat(pending(schedule.version().id())).isEmpty();
        assertThat(schedules.get(schedule.version().id()).version().revision()).isEqualTo(2);
        assertThat(schedules.get(schedule.version().id()).consecutiveFailures()).isZero();
        assertThat(paused.nextRunAt()).isEqualTo(time.plusSeconds(3600));
        assertThatThrownBy(()->schedules.save(schedule.version().id(),new ObservatoryScheduleService.Edit(1,null,old,"Edición obsoleta"))).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void missedIntervalsAreCoalescedAndNoHistoricalVacanciesAreFabricated() throws Exception {
        Instant time=now(); var schedule=create(protocol(60),time.minusSeconds(5*3600),true,0); Task task=claim(time);
        assertThat(task.skippedSlots()).isEqualTo(5); assertThat(task.scheduledFor()).isEqualTo(time);
        assertThat(schedules.get(schedule.version().id()).nextRunAt()).isEqualTo(time.plusSeconds(3600));
        assertThat(schedules.claimDue(time)).isEmpty(); assertThat(task.runId()).isNull(); assertThat(collector.calls.get()).isZero();
    }
    @Test void simultaneousDispatchClaimsOnlyOneTaskAndRejectsAnotherOwnerToken() throws Exception {
        Instant time=now(); var schedule=create(protocol(60),time.minusSeconds(1),true,1); var gate=new CountDownLatch(1);
        try (var executor=Executors.newFixedThreadPool(2)) {
            Callable<Optional<Task>> action=()->{ gate.await(); return schedules.claimDue(time); };
            var first=executor.submit(action); var second=executor.submit(action); gate.countDown();
            var results=List.of(first.get(10,TimeUnit.SECONDS),second.get(10,TimeUnit.SECONDS));
            assertThat(results.stream().filter(Optional::isPresent).count()).isEqualTo(1);
            Task chosen=results.stream().flatMap(Optional::stream).findFirst().orElseThrow();
            assertThat(schedules.bind(chosen.id(),UUID.randomUUID(),time)).isEmpty();
            assertThat(schedules.tasks(schedule.version().id(),100,0)).hasSize(1);
        }
    }
    @Test void expiredUnstartedTaskIsAbandonedAndAStaleWorkerCannotBindOrCreateAnotherRetry() throws Exception {
        Instant time=now(); var schedule=create(protocol(60),time.minusSeconds(1),true,1); Task initial=claim(time);
        schedules.recover(time.plusSeconds(31)); assertThat(task(initial.id()).state()).isEqualTo(TaskState.ABANDONED);
        assertThat(schedules.bind(initial.id(),initial.leaseToken(),time.plusSeconds(32))).isEmpty();
        schedules.complete(initial.id(),initial.leaseToken(),time.plusSeconds(33));
        assertThat(pending(schedule.version().id())).hasSize(1); assertThat(schedules.tasks(schedule.version().id(),100,0)).hasSize(2);
    }
    @Test void captureAndTerminationReasonCommitAtomically() throws Exception {
        Instant time=now(); create(protocol(60),time.minusSeconds(1),true,0); Task initial=claim(time);
        var bound=schedules.bind(initial.id(),initial.leaseToken(),time).orElseThrow();
        control.claim(bound.runId(),"https://mx.indeed.com/jobs?q=Java","FIXTURE_V1");
        assertThatThrownBy(()->control.complete(bound.runId(),new CaptureService.Finish(CaptureModels.RunState.FAILED,0,false,"Fixture failure"),"X".repeat(41))).isInstanceOf(RuntimeException.class);
        assertThat(captures.run(bound.runId()).state()).isEqualTo(CaptureModels.RunState.RUNNING);
        assertThat(control.attempt(bound.runId()).orElseThrow().status()).isEqualTo("RUNNING");
        control.interrupt(bound.runId(),"Clean up atomic rollback fixture");
        schedules.complete(initial.id(),initial.leaseToken(),now());
    }
    @Test void abandonedCollectorPreservesRawEvidenceAndBlocksLateWrites() throws Exception {
        Instant time=now(); var schedule=create(protocol(60),time.minusSeconds(1),true,1); Task initial=claim(time);
        var bound=schedules.bind(initial.id(),initial.leaseToken(),time).orElseThrow();
        control.claim(bound.runId(),"https://mx.indeed.com/jobs?q=Java","FIXTURE_V1");
        control.stage(bound.runId(),new MarketCollector.Item("pending",now(),"<div>Pending evidence</div>",null,false,"Pending evidence",null,null),"FIXTURE_V1");
        maintenance.maintain(time.plusSeconds(31));
        assertThat(captures.run(bound.runId()).state()).isEqualTo(CaptureModels.RunState.FAILED);
        assertThat(control.items(bound.runId()).getFirst().status()).isEqualTo("ERROR");
        assertThat(task(initial.id()).state()).isEqualTo(TaskState.ABANDONED); assertThat(pending(schedule.version().id())).hasSize(1);
        assertThatThrownBy(()->importer.automatic(bound.runId(),new CaptureImportService.Input("late",null,null,"Java",now(),new PostingData("Java",null,null,null,null,null,null,null,null)),UUID.randomUUID())).isInstanceOf(IllegalArgumentException.class);
        assertThat(captures.observations(bound.runId())).isEmpty();
    }
    @Test void liveHeartbeatKeepsACollectorOwnedUntilTheHardRuntimeDeadline() throws Exception {
        Instant time=now(); create(protocol(60),time.minusSeconds(1),true,1); Task initial=claim(time);
        var bound=schedules.bind(initial.id(),initial.leaseToken(),time).orElseThrow(); control.claim(bound.runId(),"https://mx.indeed.com/jobs?q=Java","FIXTURE_V1"); active.start(bound.runId());
        try {
            maintenance.maintain(time.plusSeconds(31));
            assertThat(captures.run(bound.runId()).state()).isEqualTo(CaptureModels.RunState.RUNNING);
            assertThat(task(initial.id()).leaseUntil()).isAfter(time.plusSeconds(31));
            maintenance.maintain(time.plusSeconds(121));
            assertThat(captures.run(bound.runId()).state()).isEqualTo(CaptureModels.RunState.FAILED);
            assertThat(control.attempt(bound.runId()).orElseThrow().terminationKind()).isEqualTo("TIME_LIMIT");
            assertThat(task(initial.id()).state()).isEqualTo(TaskState.ABANDONED);
        } finally { active.stop(bound.runId()); }
    }
    @Test void staleHeartbeatSelectionIsRecheckedBeforeClosingAFreshOwner() throws Exception {
        Instant time=now(); create(protocol(60),time.minusSeconds(1),true,0); Task initial=claim(time);
        var bound=schedules.bind(initial.id(),initial.leaseToken(),time).orElseThrow(); control.claim(bound.runId(),"https://mx.indeed.com/jobs?q=Java","FIXTURE_V1");
        control.heartbeat(bound.runId(),time.minusSeconds(60)); assertThat(control.stale(time.minusSeconds(30),time.minusSeconds(120))).contains(bound.runId());
        control.heartbeat(bound.runId(),time.plusSeconds(1)); control.recoverIfExpired(bound.runId(),time.plusSeconds(2),30,120);
        assertThat(captures.run(bound.runId()).state()).isEqualTo(CaptureModels.RunState.RUNNING);
    }
    @Test void aMetadataEditKeepsTheNextCadenceAndCancelsQueuedRetriesWithoutRecapturingTheSameWindow() throws Exception {
        Instant time=now(); collector.mode="EMPTY"; var schedule=create(protocol(60),time.minusSeconds(1),true,2); Task initial=claim(time); executeAt(initial,time);
        Instant next=schedules.get(schedule.version().id()).nextRunAt(); var c=schedule.version().config();
        var renamed=schedules.save(schedule.version().id(),new ObservatoryScheduleService.Edit(1,null,new Config("Nombre corregido",c.protocolId(),c.protocolRevision(),true,c.startAt(),2,1),"Corregir nombre"));
        assertThat(renamed.nextRunAt()).isEqualTo(next); assertThat(pending(schedule.version().id())).isEmpty();
        assertThat(schedules.claimDue(time.plusSeconds(2))).isEmpty();
    }
    @Test void regularCadenceSupersedesAQueuedRetryInsteadOfBuildingAQueueOfOldWindows() throws Exception {
        Instant time=now(); collector.mode="EMPTY"; var schedule=create(protocol(10),time.minusSeconds(1),true,2); Task initial=claim(time); executeAt(initial,time);
        Task retry=pending(schedule.version().id()).getFirst(); Instant next=schedules.get(schedule.version().id()).nextRunAt();
        Task regular=claim(next);
        assertThat(regular.retryNumber()).isZero(); assertThat(regular.scheduledFor()).isEqualTo(next);
        assertThat(task(retry.id()).state()).isEqualTo(TaskState.CANCELLED);
    }
    @Test void dispatchRunsBrowserWorkOutsideTheMaintenanceThreadAndDoesNotQueueAnotherCopy() throws Exception {
        Instant time=now(); var schedule=create(protocol(60),time.minusSeconds(1),true,1);
        collector.entered=new CountDownLatch(1); collector.release=new CountDownLatch(1); var done=new CountDownLatch(1);
        var monitored=new ObservatoryScheduleWorker(schedules,collection) {
            @Override public void execute(UUID id,UUID token) { try { super.execute(id,token); } finally { done.countDown(); } }
        };
        var automation=new com.projects.job_tracker.infrastructure.observatory.automation.ObservatoryAutomation(maintenance,schedules,monitored,settings);
        try {
            long began=System.nanoTime(); automation.poll();
            assertThat(Duration.ofNanos(System.nanoTime()-began)).isLessThan(Duration.ofSeconds(3));
            assertThat(collector.entered.await(5,TimeUnit.SECONDS)).isTrue();
            automation.poll(); assertThat(collector.calls.get()).isEqualTo(1);
            collector.release.countDown(); assertThat(done.await(10,TimeUnit.SECONDS)).isTrue();
            assertThat(schedules.tasks(schedule.version().id(),100,0)).hasSize(1);
            assertThat(schedules.tasks(schedule.version().id(),100,0).getFirst().state()).isEqualTo(TaskState.SUCCEEDED);
        } finally { collector.release.countDown(); automation.close(); }
    }
    @Test void pausingAssignedWorkPreventsTheBrowserFromStarting() throws Exception {
        Instant time=now(); var schedule=create(protocol(60),time.minusSeconds(1),true,2); Task assigned=claim(time); var c=schedule.version().config();
        schedules.save(schedule.version().id(),new ObservatoryScheduleService.Edit(1,null,new Config(c.name(),c.protocolId(),1,false,c.startAt(),2,1),"Pausar antes de iniciar"));
        assertThat(task(assigned.id()).state()).isEqualTo(TaskState.CANCELLED); assertThat(schedules.get(schedule.version().id()).activeTaskId()).isNull();
        worker.execute(assigned.id(),assigned.leaseToken()); assertThat(collector.calls.get()).isZero(); assertThat(task(assigned.id()).runId()).isNull();
    }
    @Test void protocolEditsDoNotSilentlyUpgradeAnExistingSchedule() throws Exception {
        Instant time=now(); UUID protocol=protocol(60); var schedule=create(protocol,time.minusSeconds(1),true,1);
        var old=captures.protocolHistory(protocol).getFirst().spec();
        captures.saveProtocol(protocol,new CaptureService.ProtocolEdit(1,new CaptureModels.ProtocolSpec(old.segmentId(),old.segmentRevision(),"Otra consulta",old.source(),"Python",old.filters(),120,old.resultLimit(),old.ordering(),old.pagination(),old.samplingMode(),old.collectionPlan()),"Cambiar búsqueda futura"));
        Task assigned=claim(time); worker.execute(assigned.id(),assigned.leaseToken());
        UUID run=task(assigned.id()).runId(); assertThat(captures.run(run).protocolRevision()).isEqualTo(1);
        assertThat(control.attempt(run).orElseThrow().searchUrl()).contains("q=Java").doesNotContain("Python");
        assertThat(schedules.get(schedule.version().id()).version().intervalMinutes()).isEqualTo(60);
    }
    @Test void userInterruptionIsNotRetriedAutomatically() throws Exception {
        Instant time=now(); var schedule=create(protocol(60),time.minusSeconds(1),true,2); Task assigned=claim(time);
        var bound=schedules.bind(assigned.id(),assigned.leaseToken(),time).orElseThrow(); control.claim(bound.runId(),"https://mx.indeed.com/jobs?q=Java","FIXTURE_V1");
        control.interrupt(bound.runId(),"Interrupción voluntaria del usuario"); schedules.complete(assigned.id(),assigned.leaseToken(),now());
        assertThat(task(assigned.id()).state()).isEqualTo(TaskState.CANCELLED); assertThat(pending(schedule.version().id())).isEmpty();
        assertThat(schedules.get(schedule.version().id()).consecutiveFailures()).isZero();
    }
}
