package com.projects.job_tracker.domain.observatory;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.projects.job_tracker.domain.model.JobPlatform;

class CollectionPlanTest {
    @Test void rejectsImplicitOrIgnoredFiltersInsteadOfChangingTheSampleSilently() {
        assertThatThrownBy(() -> new CollectionPlan(JobPlatform.LINKEDIN,null,null,null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CollectionPlan(JobPlatform.INDEED,null,PostingContext.WorkMode.HYBRID,7)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CollectionPlan(JobPlatform.COMPUTRABAJO,"CDMX",PostingContext.WorkMode.REMOTE,7)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new CollectionPlan(JobPlatform.COMPUTRABAJO,null,null,6)).isInstanceOf(IllegalArgumentException.class);
        assertThat(new CollectionPlan(JobPlatform.COMPUTRABAJO,null,PostingContext.WorkMode.REMOTE,7).filterDescription()).contains("remota","7 días");
    }
    @Test void boundsPaginationAndRequiresTheProtocolToDescribeItsActualLimit() {
        assertThatThrownBy(()->new CollectionPlan(JobPlatform.INDEED,null,null,null,0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(()->new CollectionPlan(JobPlatform.INDEED,null,null,null,21)).isInstanceOf(IllegalArgumentException.class);
        var plan=new CollectionPlan(JobPlatform.INDEED,null,null,null,3);
        assertThatThrownBy(()->new CaptureModels.ProtocolSpec(java.util.UUID.randomUUID(),1,"Portal","indeed","Java",plan.filterDescription(),60,100,"Orden del portal","Primera página",CaptureModels.SamplingMode.EXPLORATORY,plan)).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void translatesExplicitThreeDayRemoteFiltersForEveryPortal() {
        for (var platform:JobPlatform.values()) {
            var plan=new CollectionPlan(platform,null,PostingContext.WorkMode.REMOTE,3);
            var spec=new CaptureModels.ProtocolSpec(java.util.UUID.randomUUID(),1,"Portal",platform.source(),"Java",plan.filterDescription(),60,10,"Orden del portal","Primera página",CaptureModels.SamplingMode.EXPLORATORY,plan);
            var version=new CaptureModels.ProtocolVersion(java.util.UUID.randomUUID(),1,spec,"Prueba",java.time.Instant.now());
            String url=new com.projects.job_tracker.infrastructure.observatory.collection.BrowserMarketCollector(null,null,null).searchUrl(version);
            switch(platform) {
                case LINKEDIN -> assertThat(url).contains("f_WT=2","f_TPR=r259200").doesNotContain("r604800");
                case INDEED -> assertThat(url).contains("remotejob=1","fromage=3");
                case OCC -> assertThat(url).contains("tipo-home-office/","tm=3");
                case COMPUTRABAJO -> assertThat(url).contains("en-remoto","pubdate=3");
            }
        }
    }
}
