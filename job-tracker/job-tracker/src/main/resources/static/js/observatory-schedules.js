(() => {
    const el=id=>document.getElementById(id),base='/api/observatory';
    const labels={PENDING:'Reintento en espera',CLAIMED:'Asignada',RUNNING:'En curso',SUCCEEDED:'Terminó con evidencia',FAILED:'Fallida',ABANDONED:'Recuperada tras abandono',CANCELLED:'Cancelada'};
    let schedules=[],protocols=[],selected=null,offset=0,createKey=crypto.randomUUID();
    async function api(path,options) { const response=await fetch(base+path,options),body=await response.json(); if (!response.ok) throw new Error(body.error || 'No se pudo completar la operación.'); return body; }
    function notice(text,error=false) { el('schedule-notice').textContent=text; el('schedule-notice').className=`alert alert-${error?'danger':'success'}`; }
    const fail=e=>notice(e.message,true);
    function local(value) { const date=new Date(value); return new Date(date.getTime()-date.getTimezoneOffset()*60000).toISOString().slice(0,-1); }
    const date=value=>value ? new Date(value).toLocaleString() : 'Sin registrar';
    function interval() {
        const key=el('schedule-protocol').value,protocol=protocols.find(p=>`${p.id}:${p.revision}`===key);
        el('schedule-interval').textContent=protocol ? `Cada ${protocol.spec.intervalMinutes} minutos. ${protocol.spec.pagination}; cobertura parcial. Calendario por intervalos transcurridos, sin ajuste por horario de verano.` : '';
    }
    function reset() {
        selected=null; el('schedule-form').reset(); el('schedule-start').value=local(new Date(Date.now()+60000));
        el('schedule-detail').hidden=true; el('schedule-reason').value=''; offset=0; interval();
    }
    async function load() {
        const [items,available,status]=await Promise.all([api('/schedules'),api('/protocols'),api('/automation')]); schedules=items;
        const old=el('schedule-protocol').value;
        // Keep pinned historical protocols in the editor instead of upgrading them silently.
        protocols=[...available.filter(p=>p.spec.collectionPlan),...protocols.filter(p=>p.spec.collectionPlan && !available.some(a=>a.id===p.id && a.revision===p.revision))];
        el('schedule-protocol').replaceChildren();
        for (const p of protocols) el('schedule-protocol').append(new Option(`${p.spec.name} · v${p.revision}`,`${p.id}:${p.revision}`));
        if ([...el('schedule-protocol').options].some(o=>o.value===old)) el('schedule-protocol').value=old;
        el('schedule-select').replaceChildren(new Option('Crear nueva',''));
        for (const s of schedules) el('schedule-select').append(new Option(`${s.version.config.name} · v${s.version.revision}`,s.version.id));
        if (selected) el('schedule-select').value=selected.version.id;
        el('automation-status').textContent=status.enabled && status.schedulingEnabled ? 'Programador del observatorio activo. Sólo se ejecutan horarios que hayas activado.' : 'Programador global desactivado. Los horarios se guardan, pero no se iniciarán automáticamente.';
        el('schedule-list').replaceChildren(); if (!schedules.length) el('schedule-list').textContent='Aún no hay horarios guardados.';
        for (const s of schedules) {
            const card=document.createElement('div'); card.className='border rounded p-3 mb-3';
            const p=document.createElement('p'); p.textContent=`${s.version.config.name} · ${s.version.config.enabled?'Activa':'Pausada'} · ${s.activeTaskId?'Tarea activa; sin solapamiento':s.version.config.enabled?'Próximo intervalo: '+date(s.nextRunAt):'Sin nuevas capturas'} · Fallos consecutivos: ${s.consecutiveFailures}`;
            const button=document.createElement('button'); button.type='button'; button.className='btn btn-outline-primary'; button.textContent='Editar y ver historial'; button.addEventListener('click',()=>select(s.version.id).catch(fail));
            card.append(p,button); el('schedule-list').append(card);
        }
        interval();
    }
    async function select(id) {
        selected=await api(`/schedules/${id}`); offset=0;
        const config=selected.version.config,key=`${config.protocolId}:${config.protocolRevision}`;
        if (!protocols.some(p=>`${p.id}:${p.revision}`===key)) {
            const versions=await api(`/protocols/${config.protocolId}/history`); const pinned=versions.find(p=>p.revision===config.protocolRevision);
            protocols.push(pinned); el('schedule-protocol').append(new Option(`${pinned.spec.name} · v${pinned.revision} (histórico)`,key));
        }
        el('schedule-select').value=id; el('schedule-name').value=config.name; el('schedule-protocol').value=key;
        el('schedule-start').value=local(config.startAt); el('schedule-retries').value=config.maxRetries; el('schedule-backoff').value=config.backoffMinutes;
        el('schedule-enabled').checked=config.enabled; el('schedule-reason').value=''; interval(); await details();
    }
    async function details() {
        if (!selected) return;
        const id=selected.version.id;
        const [current,history,tasks]=await Promise.all([api(`/schedules/${id}`),api(`/schedules/${id}/history`),api(`/schedules/${id}/tasks?limit=25&offset=${offset}`)]);
        el('schedule-detail').hidden=false;
        el('schedule-summary').textContent=`${current.version.config.name} · v${current.version.revision} · ${current.version.config.enabled?'Activa':'Pausada'} · Cada ${current.version.intervalMinutes} minutos`;
        el('schedule-history').replaceChildren();
        for (const version of [...history].reverse()) { const p=document.createElement('p'); p.textContent=`v${version.revision} · ${date(version.recordedAt)} · ${version.config.enabled?'Activa':'Pausada'} · Protocolo v${version.config.protocolRevision} · Cada ${version.intervalMinutes} min · ${version.reason}`; el('schedule-history').append(p); }
        el('schedule-tasks').replaceChildren(); if (!tasks.length) el('schedule-tasks').textContent='Sin tareas para esta programación.';
        for (const task of tasks) {
            const d=document.createElement('details'),summary=document.createElement('summary');
            summary.textContent=`${labels[task.state]} · ${task.retryNumber?'Reintento '+task.retryNumber:'Captura inicial'} · Intervalo previsto: ${date(task.scheduledFor)}`;
            const p=document.createElement('p'); p.textContent=`Programación v${task.scheduleRevision}. Asignada: ${date(task.claimedAt)}. Terminada: ${date(task.finishedAt)}. ${task.skippedSlots ? task.skippedSlots+' intervalos anteriores omitidos. ':''}${task.notes || ''}`;
            d.append(summary,p); if (task.runId) { const a=document.createElement('a'); a.href=`/observatory/captures?run=${task.runId}`; a.textContent='Abrir captura y evidencia'; d.append(a); } el('schedule-tasks').append(d);
        }
        el('tasks-prev').disabled=offset===0; el('tasks-next').disabled=tasks.length<25;
    }
    el('schedule-form').addEventListener('submit',async event=>{
        event.preventDefault(); const button=event.target.querySelector('button'); button.disabled=true;
        try {
            const [protocolId,revision]=el('schedule-protocol').value.split(':');
            const config={name:el('schedule-name').value,protocolId,protocolRevision:Number(revision),enabled:el('schedule-enabled').checked,startAt:new Date(el('schedule-start').value).toISOString(),maxRetries:Number(el('schedule-retries').value),backoffMinutes:Number(el('schedule-backoff').value)};
            const response=await api('/schedules'+(selected?'/'+selected.version.id:''),{method:selected?'PUT':'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({config,requestKey:createKey,expectedRevision:selected?.version.revision,reason:el('schedule-reason').value})});
            createKey=crypto.randomUUID(); await load(); await select(response.version.id); notice('Programación guardada. Su versión y las tareas anteriores se conservan.');
        } catch(e) { fail(e); } finally { button.disabled=false; }
    });
    el('schedule-select').addEventListener('change',()=>{ const id=el('schedule-select').value; if (id) select(id).catch(fail); else reset(); });
    el('schedule-protocol').addEventListener('change',interval);
    el('refresh-schedules').addEventListener('click',()=>load().then(()=>selected?details():null).catch(fail));
    el('tasks-prev').addEventListener('click',()=>{ offset=Math.max(0,offset-25); details().catch(fail); });
    el('tasks-next').addEventListener('click',()=>{ offset+=25; details().catch(fail); });
    load().then(reset).catch(fail);
})();
