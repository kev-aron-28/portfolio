(() => {
    const endpoint = '/api/observatory/postings';
    const form = document.getElementById('posting-form');
    const notice = document.getElementById('notice');
    const params = new URLSearchParams(window.location.search);
    let captureRun = null;
    let captureSource = null;
    let observationKey = crypto.randomUUID();
    const availabilityLabels = {AVAILABLE:'Disponible',UNAVAILABLE:'No disponible al consultar',UNKNOWN:'Desconocida'};
    const fields = ['title', 'company', 'location', 'requirements', 'salaryMin', 'salaryMax', 'currency', 'period', 'taxBasis'];
    const contextLabels = {publishedAt:'Publicación',countryCode:'País',region:'Región',city:'Ciudad',workMode:'Modalidad',weeklyHours:'Horas semanales',contractType:'Contratación',seniority:'Nivel',remoteScope:'Alcance remoto',remoteDetails:'Restricciones remotas',evidence:'Evidencia del contexto'};
    const contextValues = {ONSITE:'Presencial',HYBRID:'Híbrida',REMOTE:'Remota',PERMANENT:'Indefinida',FIXED_TERM:'Plazo fijo',INDEPENDENT:'Independiente',INTERNSHIP:'Prácticas',TRAINEE:'Trainee',JUNIOR:'Junior',MID:'Intermedio',SENIOR:'Senior',LEAD:'Lead',COUNTRY_ONLY:'Sólo país indicado',WORLDWIDE:'Mundial',RESTRICTED:'Restringido'};
    function localInstant(value) {
        const instant = new Date(value);
        return new Date(instant.getTime() - instant.getTimezoneOffset() * 60000).toISOString().slice(0, -1);
    }
    let catalog = [];
    const catalogSelect = document.getElementById('catalog-select');
    const requirementRows = document.getElementById('skill-requirements');
    const obligationLabels = {REQUIRED:'Obligatorio', DESIRED:'Deseable', UNKNOWN:'No especificado'};
    function skillLabel(id, revision) {
        const skill = catalog.find(s => s.id === id && s.revision === revision);
        return `${skill ? skill.name : id} · v${revision}`;
    }
    function addRequirement(requirement) {
        const row = document.createElement('div'); row.className = 'border rounded p-3 mb-3';
        const select = document.createElement('select'); select.className = 'form-select mb-2'; select.required = true; select.setAttribute('aria-label', 'Habilidad y versión');
        select.append(new Option('Selecciona habilidad', ''));
        for (const skill of catalog) select.append(new Option(skillLabel(skill.id, skill.revision), `${skill.id}:${skill.revision}`));
        if (requirement) {
            const key = `${requirement.skillId}:${requirement.skillRevision}`;
            if (![...select.options].some(o => o.value === key)) select.append(new Option(`${skillLabel(requirement.skillId, requirement.skillRevision)} (histórica)`, key));
            select.value = key;
        }
        const obligation = document.createElement('select'); obligation.className = 'form-select mb-2'; obligation.setAttribute('aria-label', 'Obligatoriedad');
        for (const [value, label] of Object.entries(obligationLabels)) obligation.append(new Option(label, value));
        obligation.value = requirement?.obligation || 'UNKNOWN';
        const evidence = document.createElement('textarea'); evidence.className = 'form-control mb-2'; evidence.required = true; evidence.setAttribute('aria-label', 'Fragmento exacto del original'); evidence.placeholder = 'Fragmento exacto del texto original'; evidence.value = requirement?.evidence || '';
        const remove = document.createElement('button'); remove.type = 'button'; remove.className = 'btn btn-sm btn-outline-danger'; remove.textContent = 'Quitar habilidad'; remove.addEventListener('click', () => row.remove());
        row.append(select, obligation, evidence, remove); requirementRows.append(row);
    }
    async function loadCatalog() {
        catalog = await api('/api/observatory/skills');
        catalogSelect.replaceChildren(new Option('Crear habilidad', ''));
        for (const skill of catalog) catalogSelect.append(new Option(skillLabel(skill.id, skill.revision), skill.id));
        // Preserve references and input while making new catalog versions available.
        for (const row of requirementRows.children) {
            const select = row.querySelector('select');
            for (const skill of catalog) {
                const key = `${skill.id}:${skill.revision}`;
                if (![...select.options].some(o => o.value === key)) select.append(new Option(skillLabel(skill.id, skill.revision), key));
            }
        }
    }
    catalogSelect.addEventListener('change', async () => {
        const skill = catalog.find(s => s.id === catalogSelect.value);
        document.getElementById('skill-name').value = skill?.name || '';
        document.getElementById('skill-aliases').value = skill?.aliases.join('\n') || '';
        document.getElementById('skill-reason').value = '';
        document.getElementById('catalog-history').textContent = '';
        if (skill) {
            try {
                const versions = await api(`/api/observatory/skills/${skill.id}/history`);
                if (catalogSelect.value === skill.id) document.getElementById('catalog-history').textContent = versions.map(v => `v${v.revision}: ${v.name} · Alias: ${v.aliases.join(', ')} · ${v.reason}`).join('\n');
            } catch (e) { fail(e); }
        }
    });
    document.getElementById('skill-form').addEventListener('submit', async event => {
        event.preventDefault(); const button = event.target.querySelector('button'); button.disabled = true;
        const skill = catalog.find(s => s.id === catalogSelect.value);
        try {
            const payload = {expectedRevision:skill?.revision || 0, name:document.getElementById('skill-name').value, aliases:document.getElementById('skill-aliases').value.split('\n').map(a => a.trim()).filter(Boolean), reason:document.getElementById('skill-reason').value};
            await api(`/api/observatory/skills${skill ? '/' + skill.id : ''}`, {method:skill ? 'PUT' : 'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(payload)});
            await loadCatalog(); event.target.reset(); document.getElementById('catalog-history').textContent = '';
            notice.textContent = 'Versión del catálogo guardada.'; notice.className = 'alert alert-success';
        } catch (e) { fail(e); } finally { button.disabled = false; }
    });
    document.getElementById('add-requirement').addEventListener('click', () => addRequirement());
    let selected = null;
    let offset = 0;
    let loading = false;
    const pageSize = 25;
    async function api(path, options) {
        const response = await fetch(path, options);
        const body = await response.json();
        if (!response.ok) {
            const error = new Error(body.error || 'No fue posible completar la operación. Revisa los datos e inténtalo otra vez.');
            error.matches = body.matchingPostingIds; throw error;
        }
        return body;
    }
    function fail(error) {
        notice.textContent = error.message; notice.className = 'alert alert-danger';
        if (error.matches) {
            document.getElementById('identity-conflict').hidden = false;
            for (const id of error.matches) {
                const link = document.createElement('a'); link.href = `/observatory?posting=${id}`; link.className = 'd-block'; link.textContent = 'Abrir registro coincidente'; notice.append(link);
            }
        }
    }
    function reset() {
        selected = null;
        form.reset();
        requirementRows.replaceChildren();
        document.getElementById('readiness').textContent = '';
        document.getElementById('posting-identity').textContent = '';
        document.getElementById('identity-conflict').hidden = true;
        document.getElementById('identityConflictReason').value = '';
        document.getElementById('evidence').disabled = false;
        document.getElementById('reason-container').hidden = true;
        form.elements.reason.required = false;
        document.getElementById('detail').hidden = true;
        document.getElementById('form-title').textContent = 'Registrar vacante';
        document.getElementById('save').textContent = captureRun ? 'Registrar en esta captura' : 'Guardar registro';
        document.getElementById('capture-observation').hidden = true;
        document.getElementById('capture-observedAt').required = false;
        document.getElementById('capture-text').required = false;
        document.getElementById('extraction-notice').textContent = '';
        form.elements.source.readOnly = !!captureRun;
        if (captureRun) { form.elements.source.value = captureSource; document.getElementById('form-title').textContent = 'Registrar anuncio nuevo en captura'; }

        const now = new Date();
        const local = new Date(now.getTime() - now.getTimezoneOffset() * 60000);
        form.elements.observedAt.value = local.toISOString().slice(0, -1);
    }
    function show(posting) {
        selected = posting;
        document.getElementById('identity-conflict').hidden = true;
        document.getElementById('posting-identity').textContent = '';
        api(`${endpoint}/${posting.id}/identity`).then(identity => {
            if (selected?.id !== posting.id) return;
            form.elements.externalId.value = identity.sourceIdentity.externalId || '';
            const panel = document.getElementById('posting-identity');
            panel.textContent = identity.memberIds.length > 1 ? `Este registro pertenece a un grupo de ${identity.memberIds.length} anuncios unidos para el análisis. Los historiales se conservan separados.` : 'Registro independiente para el análisis.';
            if (identity.sourceIdentity.registrationNote) panel.textContent += ' Motivo de alta independiente: ' + identity.sourceIdentity.registrationNote;
        }).catch(fail);
        const current = posting.revisions[posting.revisions.length - 1];
        document.getElementById('extraction-notice').textContent = current.method === 'AUTO_CAPTURE_V1' ? 'Datos extraídos automáticamente, pendientes de revisión. No se asumieron moneda, periodicidad ni requisitos revisados.' : '';
        for (const field of fields) form.elements[field].value = current.data[field] ?? '';
        for (const field of Object.keys(contextLabels)) {
            const value = current.data.context?.[field];
            document.getElementById(`ctx-${field}`).value = field === 'publishedAt' && value ? localInstant(value) : (value ?? '');
        }
        requirementRows.replaceChildren();
        for (const requirement of current.data.skillRequirements || []) addRequirement(requirement);
        document.getElementById('requirements-reviewed').checked = current.data.requirementsReviewed;
        const missingLabels = {salaryBounds:'importe', currency:'moneda', period:'periodicidad', taxBasis:'bruto/neto',countryCode:'país',region:'región',city:'ciudad',workMode:'modalidad',weeklyHours:'horas semanales',contractType:'contratación',seniority:'nivel',remoteScope:'alcance remoto'};
        const missing = current.data.quality?.salaryMissingFields || [];
        document.getElementById('readiness').textContent = `Habilidades: ${current.data.requirementsReviewed ? 'revisadas' : 'sin revisar'}. Salario: ${missing.length ? 'faltan ' + missing.map(f => missingLabels[f]).join(', ') : 'contexto completo para agrupar; comparar sólo con igual puesto, nivel, moneda, periodo, base, jornada, contratación y geografía'}.`;
        document.getElementById('evidence').disabled = true;
        for (const field of ['source', 'url', 'originalText']) form.elements[field].value = posting[field] ?? '';
        const basis = posting.observations?.find(o => o.id === current.observationId);
        if (basis) form.elements.originalText.value = basis.originalText || '';
        if (captureRun) {
            document.getElementById('capture-observation').hidden = false;
            document.getElementById('capture-context').textContent = 'Esta observación se añadirá a la ejecución elegida. La captura se finaliza desde Segmentos y capturas.';
            document.getElementById('capture-text').value = basis?.originalText || posting.originalText;
            document.getElementById('capture-availability').value = 'AVAILABLE';
            document.getElementById('capture-text').required = true;
            document.getElementById('capture-observedAt').value = localInstant(new Date().toISOString());
            document.getElementById('capture-observedAt').required = true;
        }
        const seen = new Date(posting.observedAt);
        form.elements.observedAt.value = new Date(seen.getTime() - seen.getTimezoneOffset() * 60000).toISOString().slice(0, 16);
        document.getElementById('reason-container').hidden = false;
        form.elements.reason.required = true;
        form.elements.reason.value = '';
        if (captureRun) { document.getElementById('reason-container').hidden = true; form.elements.reason.required = false; }
        document.getElementById('form-title').textContent = captureRun ? 'Registrar nueva observación' : 'Revisar o corregir vacante';
        document.getElementById('save').textContent = captureRun ? 'Guardar observación en captura' : 'Guardar nueva versión';
        document.getElementById('detail').hidden = false;
        document.getElementById('original').textContent = posting.originalText;
        const timeline = document.getElementById('posting-observations');
        timeline.replaceChildren();
        for (const observation of [...(posting.observations || [])].reverse()) {
            const details = document.createElement('details'); const summary = document.createElement('summary');
            summary.textContent = `${availabilityLabels[observation.availability]} · ${new Date(observation.observedAt).toLocaleString()} · ${observation.runId ? 'Captura registrada' : 'Registro manual inicial'}`;
            const text = document.createElement('pre'); text.className = 'text-wrap border rounded p-3'; text.textContent = observation.originalText || 'Sin contenido recuperado; no demuestra contratación ni cierre.';
            details.append(summary,text); timeline.append(details);
        }
        document.getElementById('provenance').textContent = `Fuente: ${posting.source} · Observado: ${seen.toLocaleString()} · Registrado: ${new Date(posting.recordedAt).toLocaleString()}`;
        const history = document.getElementById('history');
        history.replaceChildren();
        for (const revision of [...posting.revisions].reverse()) {
            const details = document.createElement('details');
            const summary = document.createElement('summary');
            summary.textContent = `Versión ${revision.number} · ${new Date(revision.recordedAt).toLocaleString()} · ${revision.reason}`;
            const text = document.createElement('pre');
            text.className = 'text-wrap border rounded p-3';
            const labels = {title:'Título', company:'Empresa', location:'Ubicación', requirements:'Requisitos', salaryMin:'Mínimo anunciado', salaryMax:'Máximo anunciado', currency:'Moneda', period:'Periodicidad', taxBasis:'Base'};
            const values = {HOUR:'Hora', DAY:'Día', WEEK:'Semana', MONTH:'Mes', YEAR:'Año', GROSS:'Bruto', NET:'Neto'};
            text.textContent = fields.map(f => `${labels[f]}: ${values[revision.data[f]] || revision.data[f] || 'Desconocido'}`).join('\n');
            for (const [key, label] of Object.entries(contextLabels)) {
                const value = revision.data.context?.[key];
                text.textContent += `\n${label}: ${key === 'publishedAt' && value ? new Date(value).toLocaleString() : (contextValues[value] || value || 'Desconocido')}`;
            }
            text.textContent += `\nHabilidades revisadas: ${revision.data.requirementsReviewed ? 'Sí' : 'No'}`;
            for (const req of revision.data.skillRequirements || []) text.textContent += `\n${skillLabel(req.skillId, req.skillRevision)} · ${obligationLabels[req.obligation]} · Evidencia: ${req.evidence}`;
            details.append(summary, text);
            history.append(details);
        }
    }
    async function load() {
        if (loading) return;
        loading = true;
        try {
            const postings = await api(`${endpoint}?limit=${pageSize}&offset=${offset}`);
            const list = document.getElementById('postings');
            list.replaceChildren();
            if (!postings.length) list.textContent = 'No hay registros en esta página.';
            for (const posting of postings) {
                const item = document.createElement('li'); item.className = 'list-group-item';
                const button = document.createElement('button'); button.type = 'button'; button.className = 'btn btn-link text-start';
                button.textContent = `${posting.revisions[posting.revisions.length - 1].data.title} · ${posting.source}`;
                button.addEventListener('click', async () => { try { show(await api(`${endpoint}/${posting.id}`)); } catch (e) { fail(e); } });
                item.append(button); list.append(item);
            }
            document.getElementById('previous').disabled = offset === 0;
            document.getElementById('next').disabled = postings.length < pageSize;
            document.getElementById('range').textContent = `Página ${offset / pageSize + 1}`;
        } finally { loading = false; }
    }
    document.getElementById('new-posting').addEventListener('click', reset);
    document.getElementById('previous').addEventListener('click', () => { if (!loading) { offset = Math.max(0, offset - pageSize); load().catch(fail); } });
    document.getElementById('next').addEventListener('click', () => { if (!loading) { offset += pageSize; load().catch(fail); } });
    form.addEventListener('submit', async event => {
        event.preventDefault();
        const button = document.getElementById('save'); button.disabled = true;
        try {

            const data = Object.fromEntries(fields.map(f => [f, form.elements[f].value || null]));
            data.context = Object.fromEntries(Object.keys(contextLabels).map(key => [key, document.getElementById(`ctx-${key}`).value || null]));
            if (data.context.publishedAt) data.context.publishedAt = new Date(data.context.publishedAt).toISOString();
            data.requirementsReviewed = document.getElementById('requirements-reviewed').checked;
            data.skillRequirements = [...requirementRows.children].map(row => {
                const selects = row.querySelectorAll('select');
                const [skillId, revision] = selects[0].value.split(':');
                return {skillId, skillRevision:Number(revision), obligation:selects[1].value, evidence:row.querySelector('textarea').value};
            });
            const payload = selected
                ? {expectedRevision: selected.revisions[selected.revisions.length - 1].number, reason: form.elements.reason.value, data}
                : {source: form.elements.source.value, url: form.elements.url.value, originalText: form.elements.originalText.value, observedAt: new Date(form.elements.observedAt.value).toISOString(), data, externalId:form.elements.externalId.value || null, identityConflictReason:document.getElementById('identityConflictReason').value || null};
            if (captureRun && !selected) {
                const observation = await api(`/api/observatory/runs/${captureRun.id}/items`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({
                    itemKey:observationKey,url:form.elements.url.value || null,externalId:form.elements.externalId.value || null,
                    originalText:form.elements.originalText.value,observedAt:new Date(form.elements.observedAt.value).toISOString(),data
                })});
                observationKey = crypto.randomUUID(); show(await api(`${endpoint}/${observation.postingId}`)); offset = 0; await load();
                notice.textContent = 'Anuncio registrado directamente en la captura. La identidad existente se reutilizó si coincidía exactamente.'; notice.className = 'alert alert-success'; return;
            }
            if (captureRun) {
                const availability = document.getElementById('capture-availability').value;
                await api(`/api/observatory/runs/${captureRun.id}/observations`, {method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({
                    itemKey:observationKey, postingId:selected.id, expectedRevision:selected.revisions.at(-1).number,
                    observedAt:new Date(document.getElementById('capture-observedAt').value).toISOString(), availability,
                    originalText:availability === 'AVAILABLE' ? document.getElementById('capture-text').value : null,
                    data:availability === 'AVAILABLE' ? data : null
                })});
                observationKey = crypto.randomUUID(); show(await api(`${endpoint}/${selected.id}`));
                notice.textContent = 'Observación guardada con su evidencia y fecha. Puedes finalizar la captura desde Segmentos y capturas.'; notice.className = 'alert alert-success';
                return;
            }
            const saved = await api(selected ? `${endpoint}/${selected.id}` : endpoint, {method: selected ? 'PUT' : 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(payload)});
            show(saved);
            notice.textContent = 'Registro guardado. El historial y la evidencia original se conservaron.'; notice.className = 'alert alert-success';
            offset = 0; await load();
        } catch (e) { fail(e); }
        finally { button.disabled = false; }
    });
    document.getElementById('capture-availability').addEventListener('change', () => {
        document.getElementById('capture-text').required = document.getElementById('capture-availability').value === 'AVAILABLE';
    });
    async function initialize() {
        reset(); await loadCatalog();
        if (params.has('run')) {
            captureRun = await api(`/api/observatory/runs/${encodeURIComponent(params.get('run'))}`);
            if (captureRun.state !== 'RUNNING') throw new Error('La captura ya terminó. Vuelve a Segmentos y capturas para consultar su historial.');
            const versions = await api(`/api/observatory/protocols/${captureRun.protocolId}/history`);
            captureSource = versions.find(p => p.revision === captureRun.protocolRevision).spec.source;
            document.getElementById('new-posting').textContent = 'Nuevo anuncio en esta captura';
            reset();
        }
        await load();
        if (params.has('posting')) show(await api(`${endpoint}/${encodeURIComponent(params.get('posting'))}`));
    }
    initialize().catch(fail);
})();
