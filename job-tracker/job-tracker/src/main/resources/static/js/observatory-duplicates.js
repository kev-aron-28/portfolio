(() => {
    const base = '/api/observatory';
    const el = id => document.getElementById(id);
    const labels = {SOURCE_URL:'Misma fuente y URL',SOURCE_EXTERNAL_ID:'Misma fuente e identificador',EXACT_CONTENT:'Texto completo idéntico (al menos 80 caracteres)',TITLE_COMPANY:'Mismo título y empresa'};
    const decisions = {MERGED:'Unidos para el análisis',DISTINCT:'Anuncios distintos',UNREVIEWED:'Decisión deshecha / pendiente'};
    let offset = 0, selected = null, loading = false;
    const pageSize = 25;
    async function api(path,options) {
        const response = await fetch(base+path,options); const body = await response.json();
        if (!response.ok) throw new Error(body.error || 'No fue posible completar la operación.');
        return body;
    }
    function notice(message,error=false) { el('duplicate-notice').textContent=message; el('duplicate-notice').className=`alert alert-${error ? 'danger' : 'success'}`; }
    function fail(e) { notice(e.message,true); }
    function postingLink(id,label) { const a=document.createElement('a'); a.href=`/observatory?posting=${id}`; a.textContent=label; return a; }
    function renderPosting(target,snapshot) {
        target.replaceChildren();
        const title=document.createElement('h3'); title.className='h6'; title.textContent=snapshot.title;
        const p=document.createElement('p'); p.textContent=`${snapshot.company || 'Empresa desconocida'} · ${snapshot.source} · Versión ${snapshot.revision}`;
        const url=document.createElement('p'); url.className='text-break'; url.textContent=snapshot.url || 'URL desconocida';
        const original=document.createElement('pre'); original.className='text-wrap border rounded p-2'; original.textContent=snapshot.originalText || 'Sin texto';
        target.append(title,p,url,original,postingLink(snapshot.id,'Abrir datos e historial'));
    }
    function snapshot(posting) {
        const current=posting.revisions.at(-1), basis=posting.observations.find(o => o.id===current.observationId);
        return {id:posting.id,revision:current.number,title:current.data.title,company:current.data.company,source:posting.source,url:posting.url,originalText:basis?.originalText};
    }
    async function selectPair(leftId,rightId,expectedRevision,reviewId) {
        const [left,right]=await Promise.all([api(`/postings/${leftId}`),api(`/postings/${rightId}`)]);
        selected={left:snapshot(left),right:snapshot(right),expectedRevision,reviewId};
        renderPosting(el('pair-left'),selected.left); renderPosting(el('pair-right'),selected.right);
        el('pair-detail').hidden=false; el('duplicate-reason').value=''; el('duplicate-decision').value='MERGED';
        el('pair-history').replaceChildren();
        // A saved decision can be reversed, while a new pair has nothing to undo yet.
        el('duplicate-decision').querySelector('[value="UNREVIEWED"]').disabled=expectedRevision===0;
        if (reviewId) {
            const history=await api(`/duplicate-reviews/${reviewId}/history`);
            if (selected?.reviewId!==reviewId) return;
            el('duplicate-decision').value=history.at(-1).decision;
            for (const version of [...history].reverse()) {
                const p=document.createElement('p');
                p.textContent=`Revisión ${version.revision} · ${decisions[version.decision]} · ${new Date(version.recordedAt).toLocaleString()} · ${version.reason} · Revisó versiones ${version.leftRevision} y ${version.rightRevision} de los anuncios.`;
                el('pair-history').append(p);
            }
        }
        el('pair-detail').scrollIntoView({block:'start',behavior:'smooth'});
    }
    async function load() {
        if (loading) return;
        loading=true;
        try {
            const [candidates,reviews,groups]=await Promise.all([api(`/duplicate-candidates?limit=${pageSize}&offset=${offset}`),api('/duplicate-reviews'),api('/duplicate-groups')]);
            el('duplicate-candidates').replaceChildren();
            if (!candidates.length) el('duplicate-candidates').textContent='Sin candidatos en esta página. Esto no garantiza que todos los duplicados estén identificados.';
            for (const pair of candidates) {
                const card=document.createElement('div'); card.className='border rounded p-3 mb-3';
                const p=document.createElement('p'); p.textContent=`${pair.left.title} (${pair.left.source}) / ${pair.right.title} (${pair.right.source})`;
                const signals=document.createElement('p'); signals.textContent=pair.signals.map(s => labels[s]).join(' · ');
                const b=document.createElement('button'); b.type='button'; b.className='btn btn-outline-primary'; b.textContent='Comparar y decidir';
                b.addEventListener('click',() => selectPair(pair.left.id,pair.right.id,pair.expectedRevision,null).catch(fail));
                card.append(p,signals,b); el('duplicate-candidates').append(card);
            }
            el('candidate-prev').disabled=offset===0; el('candidate-next').disabled=candidates.length<pageSize;
            el('duplicate-groups').replaceChildren();
            if (!groups.length) el('duplicate-groups').textContent='Aún no hay grupos unidos.';
            for (const group of groups) {
                const card=document.createElement('div'); card.className='border rounded p-3 mb-3';
                const p=document.createElement('p'); p.textContent=`${group.members.length} registros unidos; sus evidencias se conservan separadas.`; card.append(p);
                const ul=document.createElement('ul');
                for (const member of group.members) { const li=document.createElement('li'); li.append(postingLink(member.id,`${member.title} · ${member.source}`)); ul.append(li); }
                card.append(ul); el('duplicate-groups').append(card);
            }
            el('duplicate-reviews').replaceChildren();
            if (!reviews.length) el('duplicate-reviews').textContent='Sin decisiones guardadas.';
            for (const view of reviews) {
                const r=view.review; const card=document.createElement('div'); card.className='border rounded p-3 mb-3';
                const p=document.createElement('p'); p.textContent=`${decisions[r.decision]} · Revisión ${r.revision} · ${r.reason}${view.postingDataChanged ? ' · Los datos cambiaron después de esta revisión.' : ''}`;
                const b=document.createElement('button'); b.type='button'; b.className='btn btn-outline-secondary'; b.textContent='Ver historial o cambiar decisión';
                b.addEventListener('click',() => selectPair(r.leftId,r.rightId,r.revision,r.id).catch(fail));
                card.append(p,b); el('duplicate-reviews').append(card);
            }
        } finally { loading=false; }
    }
    el('duplicate-decision-form').addEventListener('submit',async event => {
        event.preventDefault(); if (!selected) return;
        const button=event.target.querySelector('button'); button.disabled=true;
        try {
            const command={leftId:selected.left.id,rightId:selected.right.id,leftRevision:selected.left.revision,rightRevision:selected.right.revision,
                expectedRevision:selected.expectedRevision,decision:el('duplicate-decision').value,reason:el('duplicate-reason').value};
            const result=await api('/duplicate-reviews',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(command)});
            selected.expectedRevision=result.revision; selected.reviewId=result.id;
            await load(); await selectPair(result.leftId,result.rightId,result.revision,result.id);
            notice('Decisión guardada. Las capturas, textos y correcciones originales se conservaron.');
        } catch(e) { fail(e); } finally { button.disabled=false; }
    });
    el('refresh-duplicates').addEventListener('click',() => { offset=0; load().catch(fail); });
    el('candidate-prev').addEventListener('click',() => { if (!loading) { offset=Math.max(0,offset-pageSize); load().catch(fail); } });
    el('candidate-next').addEventListener('click',() => { if (!loading) { offset+=pageSize; load().catch(fail); } });
    load().catch(fail);
})();
