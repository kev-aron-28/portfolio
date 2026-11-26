(() => {
    const base = '/api/observatory';
    const el = id => document.getElementById(id);
    const states = {RUNNING:'En curso', COMPLETE:'Completa', PARTIAL:'Parcial', FAILED:'Fallida'};
    const availability = {AVAILABLE:'Disponible', UNAVAILABLE:'No disponible al consultar', UNKNOWN:'Desconocida'};
    const modes = {EXPLORATORY:'Exploratoria',SYSTEMATIC_MANUAL:'Sistemática manual'};
    let segments = [], protocols = [], activeRun = null, runOffset = 0, postingOffset = 0;
    let startKey = crypto.randomUUID();
    const pageSize = 25;
    async function api(path, options) {
        const response = await fetch(base + path, options);
        const body = await response.json();
        if (!response.ok) throw new Error(body.error || 'No fue posible completar la operación.');
        return body;
    }
    const send = (path, method, body) => api(path, {method, headers:{'Content-Type':'application/json'}, body:JSON.stringify(body)});
    function message(text, error = false) { el('capture-notice').textContent = text; el('capture-notice').className = `alert alert-${error ? 'danger' : 'success'}`; }
    function fail(e) { message(e.message, true); }
    function choices(select, items, label, allowNew) {
        select.replaceChildren();
        if (allowNew) select.append(new Option('Crear nuevo', ''));
        for (const item of items) select.append(new Option(label(item), `${item.id}:${item.revision}`));
    }
    function history(container, versions) {
        container.replaceChildren();
        for (const version of [...versions].reverse()) {
            const details = document.createElement('details'); const summary = document.createElement('summary');
            summary.textContent = `${version.spec.name} · v${version.revision} · ${new Date(version.recordedAt).toLocaleString()} · ${version.reason}`;
            const p = document.createElement('p');
            const s = version.spec;
            p.textContent = s.source ? `Fuente: ${s.source}. Consulta: ${s.query}. Filtros: ${s.filters}. Orden: ${s.ordering}. Paginación: ${s.pagination}. Intervalo: ${s.intervalMinutes} min. Límite: ${s.resultLimit}. Muestra: ${modes[s.samplingMode]}. Segmento fijado en v${s.segmentRevision}.` : `Familia: ${s.roleFamily}. País: ${s.countryCode || 'Sin definir'}. Nivel: ${s.seniority || 'Cualquiera'}. Modalidad: ${s.workMode || 'Cualquiera'}. ${s.description || ''}`;
            details.append(summary,p); container.append(details);
        }
    }
    async function loadConfigs() {
        const priorSelections = ['segment-select','protocol-select','proto-segment','start-protocol'].map(id => ({id, value:el(id).value, text:el(id).selectedOptions[0]?.textContent}));
        [segments,protocols] = await Promise.all([api('/segments'),api('/protocols')]);
        choices(el('segment-select'),segments,s => `${s.spec.name} · v${s.revision}`,true);
        choices(el('protocol-select'),protocols,s => `${s.spec.name} · v${s.revision}`,true);
        choices(el('proto-segment'),segments,s => `${s.spec.name} · v${s.revision}`,false);
        choices(el('start-protocol'),protocols,s => `${s.spec.name} · v${s.revision}`,false);
        for (const prior of priorSelections) if (prior.value) {
            const select = el(prior.id);
            if (![...select.options].some(o => o.value === prior.value) && ['proto-segment','start-protocol'].includes(prior.id)) select.append(new Option(prior.text + ' (histórico)',prior.value));
            if ([...select.options].some(o => o.value === prior.value)) select.value = prior.value;
        }
    }
    const segmentFields = ['name','description','roleFamily','countryCode','seniority','workMode'];
    const protocolFields = ['name','source','query','filters','intervalMinutes','resultLimit','ordering','pagination','samplingMode'];
    function syncCollector() {
        const enabled=el('collector-enabled').checked;
        el('collector-fields').hidden=!enabled;
        for (const field of ['source','filters','ordering','pagination']) el(`proto-${field}`).readOnly=enabled;
        el('proto-samplingMode').disabled=enabled; el('proto-resultLimit').max=enabled ? '1000' : '10000';
        if (!enabled) return;
        const platform=el('collector-platform').value;
        const location=el('collector-location').value.trim();
        const mode=el('collector-workMode').value;
        const days=el('collector-days').value;
        const modeNames={REMOTE:'remota',HYBRID:'híbrida',ONSITE:'presencial'};
        el('proto-source').value=platform.toLowerCase();
        el('proto-filters').value=`Ubicación: ${location || 'sin filtro'}; modalidad: ${modeNames[mode] || 'sin filtro'}; antigüedad: ${days ? Number(days)+' días' : 'sin filtro'}`;
        el('proto-ordering').value='Orden del portal'; el('proto-pagination').value=Number(el('collector-maxPages').value || 1)===1 ? 'Primera página' : `Hasta ${Number(el('collector-maxPages').value)} páginas`; el('proto-samplingMode').value='EXPLORATORY';
    }
    for (const id of ['collector-enabled','collector-platform','collector-location','collector-workMode','collector-days','collector-maxPages']) el(id).addEventListener('input',syncCollector);
    function fill(prefix, keys, values) { for (const key of keys) el(`${prefix}-${key}`).value = values?.[key] ?? ''; }
    el('segment-select').addEventListener('change', async () => {
        const key = el('segment-select').value; const selected = segments.find(s => `${s.id}:${s.revision}` === key);
        fill('seg',segmentFields,selected?.spec); el('seg-reason').value = ''; el('segment-history').replaceChildren();
        if (selected) try { const versions = await api(`/segments/${selected.id}/history`); if (el('segment-select').value === key) history(el('segment-history'),versions); } catch (e) { fail(e); }
    });
    el('protocol-select').addEventListener('change', async () => {
        const key = el('protocol-select').value; const selected = protocols.find(s => `${s.id}:${s.revision}` === key);
        fill('proto',protocolFields,selected?.spec); if (!el('proto-samplingMode').value) el('proto-samplingMode').value='EXPLORATORY'; el('proto-reason').value = ''; el('protocol-history').replaceChildren();
        const plan=selected?.spec.collectionPlan;
        el('collector-enabled').checked=!!plan;
        el('collector-platform').value=plan?.platform || 'LINKEDIN'; el('collector-location').value=plan?.location || '';
        el('collector-workMode').value=plan?.workMode || ''; el('collector-days').value=plan?.postedWithinDays || ''; el('collector-maxPages').value=plan?.maxPages || 1;
        syncCollector();
        if (!selected) return;
        const segmentKey = `${selected.spec.segmentId}:${selected.spec.segmentRevision}`;
        if (![...el('proto-segment').options].some(o => o.value === segmentKey)) el('proto-segment').append(new Option(`Segmento histórico · v${selected.spec.segmentRevision}`,segmentKey));
        el('proto-segment').value = segmentKey;
        try { const versions = await api(`/protocols/${selected.id}/history`); if (el('protocol-select').value === key) history(el('protocol-history'),versions); } catch(e) { fail(e); }
    });
    function submit(formId, action) {
        el(formId).addEventListener('submit', async event => {
            event.preventDefault(); const button = event.target.querySelector('button'); button.disabled = true;
            try { await action(); } catch(e) { fail(e); } finally { button.disabled = false; }
        });
    }
    submit('segment-form',async () => {
        const [id, revision] = el('segment-select').value.split(':');
        const spec = Object.fromEntries(segmentFields.map(k => [k,el(`seg-${k}`).value || null]));
        await send(`/segments${id ? '/' + id : ''}`,id ? 'PUT' : 'POST',{expectedRevision:id ? Number(revision) : null, spec,reason:el('seg-reason').value});
        await loadConfigs(); el('segment-form').reset(); el('segment-history').replaceChildren(); message('Versión del segmento guardada.');
    });
    submit('protocol-form',async () => {
        const [id, revision] = el('protocol-select').value.split(':');
        const [segmentId, segmentRevision] = el('proto-segment').value.split(':');
        const spec = Object.fromEntries(protocolFields.map(k => [k,el(`proto-${k}`).value]));
        spec.segmentId = segmentId; spec.segmentRevision = Number(segmentRevision);
        spec.intervalMinutes = Number(spec.intervalMinutes); spec.resultLimit = Number(spec.resultLimit);
        if (el('collector-enabled').checked) spec.collectionPlan={maxPages:Number(el('collector-maxPages').value || 1),platform:el('collector-platform').value,location:el('collector-location').value.trim() || null,
            workMode:el('collector-workMode').value || null,postedWithinDays:el('collector-days').value ? Number(el('collector-days').value) : null};
        await send(`/protocols${id ? '/' + id : ''}`,id ? 'PUT' : 'POST',{expectedRevision:id ? Number(revision) : null,spec,reason:el('proto-reason').value});
        await loadConfigs(); el('protocol-form').reset(); syncCollector(); el('protocol-history').replaceChildren(); message('Versión del protocolo guardada.');
    });
    el('start-protocol').addEventListener('change', () => { startKey = crypto.randomUUID(); });
    submit('start-run',async () => {
        const [id, revision] = el('start-protocol').value.split(':');
        if (!id) throw new Error('Crea primero un protocolo.');
        const run = await send(`/protocols/${id}/runs`,'POST',{protocolRevision:Number(revision),requestKey:startKey});
        startKey = crypto.randomUUID(); runOffset = 0; await loadRuns(); await showRun(run.id); message('Bitácora de captura iniciada. No se han consultado portales automáticamente.');
    });
    async function loadRuns() {
        const runs = await api(`/runs?limit=${pageSize}&offset=${runOffset}`);
        const histories = await Promise.all([...new Set(runs.map(r => r.protocolId))].map(id => api(`/protocols/${id}/history`)));
        const pinnedProtocols = histories.flat();
        el('run-list').replaceChildren();
        if (!runs.length) el('run-list').textContent = 'Sin ejecuciones en esta página.';
        for (const run of runs) {
            const li = document.createElement('li'); li.className = 'list-group-item';
            const b = document.createElement('button'); b.type = 'button'; b.className = 'btn btn-link text-start';
            const proto = pinnedProtocols.find(p => p.id === run.protocolId && p.revision === run.protocolRevision);
            b.textContent = `${proto?.spec.name || 'Protocolo'} · v${run.protocolRevision} · ${states[run.state]} · ${new Date(run.startedAt).toLocaleString()}`;
            b.addEventListener('click', () => showRun(run.id).catch(fail)); li.append(b); el('run-list').append(li);
        }
        el('run-prev').disabled = runOffset === 0; el('run-next').disabled = runs.length < pageSize;
    }
    async function showRun(id) {
        const run = await api(`/runs/${id}`);
        const versions = await api(`/protocols/${run.protocolId}/history`); const protocol = versions.find(p => p.revision === run.protocolRevision);
        const segmentVersions = await api(`/segments/${protocol.spec.segmentId}/history`); const segment = segmentVersions.find(s => s.revision === protocol.spec.segmentRevision);
        const [observations,collection]=await Promise.all([api(`/runs/${id}/observations`),api(`/runs/${id}/collection`)]);
        activeRun = run; el('run-detail').hidden = false;
        el('run-summary').textContent = `${states[run.state]} · ${run.observationCount} observaciones · Páginas: ${run.pagesVisited ?? 'Sin registrar'} · ${run.truncated ? 'Truncada' : 'Sin truncamiento declarado'} · ${run.notes || ''}`;
        const s = protocol.spec;
        el('run-config').textContent = `${s.name} v${protocol.revision} · ${segment.spec.name} v${segment.revision}\nFamilia: ${segment.spec.roleFamily}. País: ${segment.spec.countryCode || 'Sin definir'}. Nivel: ${segment.spec.seniority || 'Cualquiera'}. Modalidad: ${segment.spec.workMode || 'Cualquiera'}.\nFuente: ${s.source}\nConsulta: ${s.query}\nFiltros: ${s.filters}\nOrden: ${s.ordering}\nPaginación: ${s.pagination}\nIntervalo previsto: ${s.intervalMinutes} minutos; los horarios se gestionan en Programación\nLímite: ${s.resultLimit}\nMuestra: ${modes[s.samplingMode]}`;
        const collecting=collection.attempt.status==='RUNNING';
        el('finish-run').hidden = run.state !== 'RUNNING' || collecting; el('observe-controls').hidden = run.state !== 'RUNNING' || !!collection.attempt.status;
        el('new-capture-posting').href=`/observatory?run=${run.id}`;
        el('collect-run-controls').hidden=!s.collectionPlan || run.state!=='RUNNING';
        el('collect-run').disabled=!!collection.attempt.status || run.observationCount>0;
        el('interrupt-collection').hidden=!collecting || run.state!=='RUNNING';
        el('collection-preview').textContent=collection.attempt.searchUrl || '';
        if (s.collectionPlan && !collection.attempt.searchUrl) {
            const preview=await api(`/protocols/${protocol.id}/collection-preview?revision=${protocol.revision}`);
            if (activeRun?.id===id) el('collection-preview').textContent=preview.searchUrl;
        }
        el('collection-items').replaceChildren();
        if (collection.attempt.notes) { const p=document.createElement('p'); p.textContent=collection.attempt.notes; el('collection-items').append(p); }
        if (collecting) { const p=document.createElement('p'); p.textContent='Recolección en curso. Puedes actualizar el detalle para consultar su progreso.'; el('collection-items').append(p); }
        const pageOutcomes={NEXT_LINK_OBSERVED:'Enlace siguiente observado',END_CONTROL_OBSERVED:'Control de siguiente desactivado',NAVIGATION_UNKNOWN:'Continuidad desconocida',UNSUPPORTED_NAVIGATION:'Navegación no compatible',SEARCH_SCOPE_CHANGED:'El enlace cambia la búsqueda',UNSAFE_NAVIGATION:'Enlace no válido',ACCESS_NOT_CONFIRMED:'Acceso sin confirmar',NO_RECOGNIZED_CARDS:'Sin tarjetas reconocidas',REPEATED_PAGE:'Página repetida',RESULT_LIMIT:'Límite de anuncios',PAGE_LIMIT:'Límite de páginas'};
        for (const page of collection.pages || []) {
            const d=document.createElement('details'), summary=document.createElement('summary');
            summary.textContent=`Página ${page.number} · ${page.cardCount} tarjetas · ${pageOutcomes[page.outcome] || page.outcome}`;
            const info=document.createElement('p'); info.className='text-break'; info.textContent=`${page.url} · ${new Date(page.observedAt).toLocaleString()}${page.nextUrl ? ' · Siguiente: '+page.nextUrl : ''}`;
            const html=document.createElement('pre'); html.className='text-wrap border rounded p-2'; html.textContent=page.html;
            d.append(summary,info,html); el('collection-items').append(d);
        }
        for (const item of collection.items) {
            const d=document.createElement('details'),summary=document.createElement('summary');
            const status={PENDING:'Pendiente de importación',IMPORTED:'Importado',ERROR:'Requiere revisión'};
            summary.textContent=`${status[item.status]} · ${item.detailVerified ? 'Detalle verificado' : 'Sólo tarjeta / detalle sin verificar'} · ${item.issue || ''}`;
            const text=document.createElement('pre'); text.className='text-wrap border rounded p-2'; text.textContent=item.originalText || 'Sin texto extraído';
            const raw=document.createElement('pre'); raw.className='text-wrap border rounded p-2'; raw.textContent=`Tarjeta HTML:\n${item.listingHtml || ''}\nDetalle HTML:\n${item.detailHtml || ''}`;
            const rawDetails=document.createElement('details'),rawSummary=document.createElement('summary'); rawSummary.textContent='Ver HTML de origen (tarjeta y detalle)'; rawDetails.append(rawSummary,raw);
            d.append(summary,text,rawDetails); if (item.postingId) { const a=document.createElement('a'); a.href=`/observatory?posting=${item.postingId}`; a.textContent='Revisar datos del anuncio'; d.append(a); }
            el('collection-items').append(d);
        }
        el('finish-run').reset();
        el('run-observations').replaceChildren();
        if (!observations.length) el('run-observations').textContent = 'Sin observaciones. Un fallo de captura no equivale a cero demanda.';
        for (const o of observations) {
            const d = document.createElement('details'); const summary = document.createElement('summary');
            summary.textContent = `${availability[o.availability]} · ${new Date(o.observedAt).toLocaleString()}`;
            const text = document.createElement('pre'); text.className = 'text-wrap border rounded p-2'; text.textContent = o.originalText || 'Sin contenido recuperado';
            const a = document.createElement('a'); a.href = `/observatory?posting=${o.postingId}`; a.textContent = 'Ver anuncio e historial'; d.append(summary,text,a); el('run-observations').append(d);
        }
        postingOffset = 0; if (run.state === 'RUNNING') await loadPostings();
    }
    async function loadPostings() {
        const postings = await api(`/postings?limit=${pageSize}&offset=${postingOffset}`);
        el('observe-posting').replaceChildren();
        for (const posting of postings) el('observe-posting').append(new Option(`${posting.revisions.at(-1).data.title} · ${posting.source}`,posting.id));
        el('posting-prev').disabled = postingOffset === 0; el('posting-next').disabled = postings.length < pageSize; updateLink();
    }
    function updateLink() {
        const id = el('observe-posting').value;
        el('observe-link').hidden = !id;
        if (id && activeRun) el('observe-link').href = `/observatory?run=${activeRun.id}&posting=${id}`;
    }
    submit('interrupt-collection',async () => {
        if (!activeRun) return;
        const id=activeRun.id;
        await send(`/runs/${id}/collection/interrupt`,'POST',{reason:el('interrupt-reason').value});
        await showRun(id); await loadRuns(); message('Captura interrumpida. Se conservaron las evidencias ya recibidas.');
    });
    el('collect-run').addEventListener('click',async () => {
        if (!activeRun) return;
        const id=activeRun.id; el('collect-run').disabled=true;
        let firstPoll=true;
        const poll=setInterval(async () => {
            if (activeRun?.id!==id) return;
            try {
                const current=await api(`/runs/${id}`);
                if (firstPoll || current.state!==activeRun.state || current.observationCount!==activeRun.observationCount) { firstPoll=false; await showRun(id); }
            } catch(e) { fail(e); }
        },5000);
        message('Recolectando las páginas configuradas. Se conservarán evidencia y errores; puede tardar según el portal.');
        try { await send(`/runs/${id}/collect`,'POST',{}); await showRun(id); await loadRuns(); message('Recolección terminada. Revisa el estado, la cobertura y la evidencia de cada elemento.'); }
        catch(e) { fail(e); await showRun(id).catch(fail); }
        finally { clearInterval(poll); }
    });
    el('observe-posting').addEventListener('change',updateLink);
    el('posting-prev').addEventListener('click',() => { postingOffset = Math.max(0, postingOffset-pageSize); loadPostings().catch(fail); });
    el('posting-next').addEventListener('click',() => { postingOffset += pageSize; loadPostings().catch(fail); });
    el('run-prev').addEventListener('click',() => { runOffset = Math.max(0,runOffset-pageSize); loadRuns().catch(fail); });
    el('run-next').addEventListener('click',() => { runOffset += pageSize; loadRuns().catch(fail); });
    submit('finish-run',async () => {
        if (!activeRun) return;
        await send(`/runs/${activeRun.id}/finish`,'PUT',{state:el('finish-state').value,pagesVisited:Number(el('finish-pages').value),truncated:el('finish-truncated').checked,notes:el('finish-notes').value || null});
        await showRun(activeRun.id); await loadRuns(); message('Captura finalizada. Su configuración y observaciones quedan conservadas.');
    });
    loadConfigs().then(loadRuns).then(() => {
        const run=new URLSearchParams(location.search).get('run');
        if (run) return showRun(run);
    }).catch(fail);
})();
