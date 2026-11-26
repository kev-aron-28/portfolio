const clock = document.querySelector('[data-focus-clock]');
if (clock) {
  const started = Date.parse(clock.dataset.startedAt);
  const update = () => {
    const seconds = Math.max(0, Math.floor((Date.now() - started) / 1000));
    const units = [Math.floor(seconds / 3600), Math.floor(seconds / 60) % 60, seconds % 60];
    clock.textContent = units.map(unit => String(unit).padStart(2, '0')).join(':');
  };
  update();
  setInterval(update, 1000);
}

const map = document.querySelector('[data-learning-map]');
if (map && map.querySelector('.learning-node')) {
  const canvas = map.querySelector('canvas');
  const context = canvas.getContext('2d');
  const nodes = new Map(Array.from(map.querySelectorAll('[data-node]'),
    node => [node.dataset.node, node]));
  const sections = Array.from(map.querySelectorAll('[data-section-point]'));
  const edges = Array.from(map.querySelectorAll('[data-learning-edge]'));
  const requiredTargets = new Set(edges.filter(edge => edge.dataset.required === 'true')
    .map(edge => edge.dataset.to));
  const selected = map.querySelector('[aria-current="true"]');
  const hub = map.querySelector('.learning-map-hub');
  const viewport = map.parentElement;
  const controls = viewport.closest('.learning-map-panel').querySelector('.learning-map-controls');
  const centerButton = controls.querySelector('[data-map-center]');
  const zoomButtons = controls.querySelectorAll('[data-map-zoom]');
  const scaleOutput = controls.querySelector('[data-map-scale]');
  const arrange = controls.querySelector('[data-map-arrange]');
  const arrangeHelp = document.querySelector('[data-arrange-help]');
  const positionStatus = document.querySelector('[data-position-status]');
  const positionLimit = Number(map.dataset.positionLimit);
  const positions = new Map();
  const pendingPositions = new Set();
  let arranging = false;
  let positionError = false;

  function setPosition(item, position) {
    positions.set(item, position);
    item.style.setProperty('--node-x', `calc(50% + ${position.x}px)`);
    item.style.setProperty('--node-y', `calc(50% + ${position.y}px)`);
  }
  const viewKey = `learning-map:${location.pathname}:${map.dataset.boardSection ?? 'root'}`;
  let zoom = 1;
  let savedView;
  try {
    savedView = JSON.parse(sessionStorage.getItem(viewKey));
  } catch {
    // The board works without browser storage.
  }
  const rings = [];
  const angles = new Map();
  let radius = 65;

  for (const level of map.querySelectorAll('.learning-level')) {
    const entries = Array.from(level.querySelectorAll('.learning-node'));
    const hasSections = entries.some(node => node.hasAttribute('data-section-point'));
    radius = Math.max(radius + (hasSections ? 135 : 115), entries.length * (hasSections ? 164 : 123) / (2 * Math.PI));
    rings.push(radius);
    const preferredAngle = node => {
      const parents = edges.filter(edge => edge.dataset.to === node.dataset.node)
        .map(edge => angles.get(edge.dataset.from)).filter(angle => angle !== undefined);
      if (!parents.length) return -Math.PI / 2;
      return Math.atan2(parents.reduce((total, angle) => total + Math.sin(angle), 0),
        parents.reduce((total, angle) => total + Math.cos(angle), 0));
    };
    entries.sort((first, second) => preferredAngle(first) - preferredAngle(second));
    const mastery = entries[0].querySelector('[data-kind]').dataset.kind === 'MASTERY';
    const start = mastery ? Math.PI / 2 : entries.length === 1 ? preferredAngle(entries[0])
      : -Math.PI / 2 + (rings.length % 2 ? Math.PI / 4 : Math.PI / 2);
    entries.forEach((node, index) => {
      const angle = start + index * Math.PI * 2 / entries.length;
      if (node.dataset.node) angles.set(node.dataset.node, angle);
      const x = Number(node.dataset.boardX ?? Math.cos(angle) * radius);
      const y = Number(node.dataset.boardY ?? Math.sin(angle) * radius);
      setPosition(node, {x, y});
    });
  }
  const defaultMapSize = 2 * (radius + 85);
  let mapSize = 0;
  map.classList.add('is-radial');
  updateMapSize();

  function updateMapSize() {
    let extent = defaultMapSize / 2;
    for (const {x, y} of positions.values()) {
      const reach = Math.max(Math.abs(x), Math.abs(y)) + 85;
      if (reach > defaultMapSize / 2 + 1) {
        extent = Math.max(extent, Math.ceil(reach / 48) * 48);
      }
    }
    if (mapSize === extent * 2) return;
    const centerBefore = hub.getBoundingClientRect();
    mapSize = extent * 2;
    map.style.setProperty('--map-size', `${mapSize}px`);
    const centerAfter = hub.getBoundingClientRect();
    viewport.scrollLeft += centerAfter.left - centerBefore.left;
    viewport.scrollTop += centerAfter.top - centerBefore.top;
  }
  controls.hidden = false;

  function fitZoom() {
    return Math.min(1, Math.max(0.1, Math.min(viewport.clientWidth - 30, viewport.clientHeight - 130) / mapSize));
  }

  function updateBoardSize() {
    map.style.setProperty('--board-width', `${viewport.clientWidth}px`);
    map.style.setProperty('--board-height', `${viewport.clientHeight}px`);
  }

  function updateControls() {
    scaleOutput.value = `${Math.round(zoom * 100)}%`;
    zoomButtons[0].disabled = zoom <= Math.min(.35, fitZoom());
    zoomButtons[1].disabled = zoom >= 2;
  }

  function drawEdges() {
    if (!context) return;
    const bounds = map.getBoundingClientRect();
    const scale = Math.min(window.devicePixelRatio || 1, 4096 / Math.max(bounds.width, bounds.height));
    const styles = getComputedStyle(map);
    const edgeColor = styles.getPropertyValue('--learning-edge').trim();
    const routeColor = styles.getPropertyValue('--learning-route').trim();
    const width = Math.max(1, Math.ceil(bounds.width * scale));
    const height = Math.max(1, Math.ceil(bounds.height * scale));
    if (canvas.width !== width) canvas.width = width;
    if (canvas.height !== height) canvas.height = height;
    context.setTransform(scale, 0, 0, scale, 0, 0);
    context.clearRect(0, 0, bounds.width, bounds.height);
    context.setLineDash([]);

    const center = {x: bounds.width / 2, y: bounds.height / 2};
    const point = element => {
      const rect = element.getBoundingClientRect();
      return {x: rect.left + rect.width / 2 - bounds.left, y: rect.top + rect.height / 2 - bounds.top};
    };
    for (const radius of rings) {
      context.strokeStyle = '#28364d';
      context.lineWidth = 1;
      context.beginPath();
      context.arc(center.x, center.y, radius * zoom, 0, Math.PI * 2);
      context.stroke();
    }

    function connect(from, to, active, dashed, highlighted) {
      context.strokeStyle = active ? routeColor : edgeColor;
      context.lineWidth = highlighted ? 3 : active ? 2 : 1.5;
      context.setLineDash(dashed ? [4, 4] : []);
      context.beginPath();
      context.moveTo(from.x, from.y);
      context.lineTo(to.x, to.y);
      context.stroke();
    }

    for (const section of sections) {
      connect(center, point(section.querySelector('.learning-skill-emblem')), true, false, false);
    }
    for (const [id, node] of nodes) {
      if (!requiredTargets.has(id) && node.querySelector('[data-kind]').dataset.kind !== 'MASTERY') {
        connect(center, point(node.querySelector('.learning-skill-emblem')),
          node.dataset.status !== 'LOCKED', false, node.hasAttribute('aria-current'));
      }
    }
    const mastery = Array.from(nodes.values()).find(node => node.querySelector('[data-kind]').dataset.kind === 'MASTERY');
    if (mastery) {
      const prerequisites = new Set(edges.filter(edge => edge.dataset.required === 'true')
        .map(edge => edge.dataset.from));
      const terminalNodes = Array.from(nodes.values())
        .filter(node => node !== mastery && !prerequisites.has(node.dataset.node));
      const populatedSections = sections.filter(section => Number(section.dataset.sectionTotal) > 0);
      for (const node of [...terminalNodes, ...populatedSections]) {
        connect(point(node.querySelector('.learning-skill-emblem')),
          point(mastery.querySelector('.learning-skill-emblem')),
          mastery.dataset.status !== 'LOCKED', false, mastery.hasAttribute('aria-current'));
      }
    }
    for (const edge of edges) {
      const source = nodes.get(edge.dataset.from);
      const target = nodes.get(edge.dataset.to);
      const required = edge.dataset.required === 'true';
      const highlighted = source.hasAttribute('aria-current') || target.hasAttribute('aria-current');
      const unlocked = required && ['COMPLETE', 'DUE'].includes(source.dataset.status)
        && target.dataset.status !== 'LOCKED';
      connect(point(source.querySelector('.learning-skill-emblem')),
        point(target.querySelector('.learning-skill-emblem')), highlighted || unlocked, !required, highlighted);
    }
  }

  function centerOn(element) {
    const bounds = viewport.getBoundingClientRect();
    const target = element.getBoundingClientRect();
    viewport.scrollLeft += target.left + target.width / 2 - bounds.left - viewport.clientWidth / 2;
    viewport.scrollTop += target.top + target.height / 2 - bounds.top - viewport.clientHeight / 2;
  }

  function setZoom(value, anchor) {
    const bounds = viewport.getBoundingClientRect();
    const x = anchor ? anchor.clientX - bounds.left : viewport.clientWidth / 2;
    const y = anchor ? anchor.clientY - bounds.top : viewport.clientHeight / 2;
    const before = hub.getBoundingClientRect();
    const dx = (bounds.left + x - before.left - before.width / 2) / zoom;
    const dy = (bounds.top + y - before.top - before.height / 2) / zoom;
    zoom = Math.min(2, Math.max(Math.min(.35, fitZoom()), value));
    map.style.setProperty('--map-zoom', zoom);
    const after = hub.getBoundingClientRect();
    viewport.scrollLeft += after.left + after.width / 2 + dx * zoom - bounds.left - x;
    viewport.scrollTop += after.top + after.height / 2 + dy * zoom - bounds.top - y;
    drawEdges();
    updateControls();
  }

  function saveView() {
    const bounds = viewport.getBoundingClientRect();
    const center = hub.getBoundingClientRect();
    try {
      sessionStorage.setItem(viewKey, JSON.stringify({
        zoom,
        x: (bounds.left + viewport.clientWidth / 2 - center.left - center.width / 2) / zoom,
        y: (bounds.top + viewport.clientHeight / 2 - center.top - center.height / 2) / zoom
      }));
    } catch {
      // Storage can be disabled by the browser.
    }
  }

  centerButton.addEventListener('click', () => centerOn(hub));
  controls.querySelector('[data-map-fit]').addEventListener('click', () => {
    setZoom(fitZoom());
    centerOn(hub);
    saveView();
  });
  for (const button of zoomButtons) {
    button.addEventListener('click', () => {
      setZoom(zoom * (Number(button.dataset.mapZoom) > 0 ? 1.2 : 1 / 1.2));
      saveView();
    });
  }

  viewport.addEventListener('wheel', event => {
    if (!event.ctrlKey && !event.metaKey) return;
    event.preventDefault();
    setZoom(zoom * (event.deltaY < 0 ? 1.12 : 1 / 1.12), event);
    saveView();
  }, {passive: false});

  let drawFrame;

  function placeItem(item, x, y) {
    const constrain = value => Math.max(-positionLimit, Math.min(positionLimit, Math.round(value)));
    setPosition(item, {x: constrain(x), y: constrain(y)});
    updateMapSize();
    cancelAnimationFrame(drawFrame);
    drawFrame = requestAnimationFrame(drawEdges);
  }

  async function persistPosition(item, previous) {
    const {x, y} = positions.get(item);
    if (x === previous.x && y === previous.y) return;
    if (!pendingPositions.size) positionError = false;
    pendingPositions.add(item);
    if (!positionError) positionStatus.textContent = 'Guardando posición…';

    const body = new URLSearchParams({
      type: item.dataset.sectionPoint ? 'SECTION' : 'NODE',
      item: item.dataset.sectionPoint ?? item.dataset.node,
      x: String(x),
      y: String(y)
    });
    if (map.dataset.boardSection) body.set('board', map.dataset.boardSection);
    try {
      const response = await fetch(map.dataset.positionUrl, {method: 'POST', body, keepalive: true});
      if (!response.ok) throw new Error('Error al guardar la posición.');
    } catch {
      setPosition(item, previous);
      updateMapSize();
      drawEdges();
      positionError = true;
      positionStatus.textContent = 'No se pudo guardar. El elemento volvió a su posición anterior.';
    } finally {
      pendingPositions.delete(item);
      if (!pendingPositions.size && !positionError) {
        positionStatus.textContent = arranging ? 'Posición guardada.' : '';
      }
    }
  }

  function setArranging(active) {
    arranging = active;
    arrange.setAttribute('aria-pressed', String(active));
    viewport.classList.toggle('is-arranging', active);
    arrangeHelp.hidden = !active;
    if (!pendingPositions.size) positionStatus.textContent = '';
  }

  arrange.addEventListener('click', () => setArranging(!arranging));

  const directions = {ArrowUp: [0, -1], ArrowDown: [0, 1], ArrowLeft: [-1, 0], ArrowRight: [1, 0]};
  viewport.addEventListener('keydown', event => {
    const item = event.target.closest('.learning-node');
    const direction = directions[event.key];
    if (arranging && event.key === 'Escape') {
      event.preventDefault();
      if (pointer?.item) {
        const id = pointer.id;
        endDrag({pointerId: id, type: 'pointercancel'});
        if (viewport.hasPointerCapture(id)) viewport.releasePointerCapture(id);
      }
      setArranging(false);
      arrange.focus();
    } else if (arranging && item && direction) {
      event.preventDefault();
      if (pendingPositions.has(item)) return;
      const previous = positions.get(item);
      const step = event.shiftKey ? 60 : 20;
      placeItem(item, previous.x + direction[0] * step, previous.y + direction[1] * step);
      persistPosition(item, previous);
    } else if (event.target === viewport && direction) {
      event.preventDefault();
      viewport.scrollBy(direction[0] * 80, direction[1] * 80);
    } else if (event.target === viewport && ['+', '=', '-'].includes(event.key)) {
      event.preventDefault();
      setZoom(zoom * (event.key === '-' ? 1 / 1.2 : 1.2));
      saveView();
    }
  });

  let pointer;
  let dragged = false;
  viewport.addEventListener('dragstart', event => event.preventDefault());
  viewport.addEventListener('pointerdown', event => {
    if (!event.isPrimary || event.button !== 0) return;
    dragged = false;
    const item = arranging ? event.target.closest('.learning-node') : null;
    if (item && pendingPositions.has(item)) return;
    const center = hub.getBoundingClientRect();
    const previous = item ? positions.get(item) : null;
    pointer = {id: event.pointerId, x: event.clientX, y: event.clientY,
      left: viewport.scrollLeft, top: viewport.scrollTop, item, previous,
      offsetX: previous ? previous.x - (event.clientX - center.left - center.width / 2) / zoom : 0,
      offsetY: previous ? previous.y - (event.clientY - center.top - center.height / 2) / zoom : 0};
  });

  viewport.addEventListener('pointermove', event => {
    if (pointer?.id !== event.pointerId) return;
    const dx = event.clientX - pointer.x;
    const dy = event.clientY - pointer.y;
    if (!dragged) {
      if (Math.hypot(dx, dy) < 5) return;
      dragged = true;
      viewport.setPointerCapture(event.pointerId);
      viewport.classList.add('is-dragging');
      pointer.item?.classList.add('is-moving');
    }

    if (pointer.item) {
      const center = hub.getBoundingClientRect();
      placeItem(pointer.item,
        (event.clientX - center.left - center.width / 2) / zoom + pointer.offsetX,
        (event.clientY - center.top - center.height / 2) / zoom + pointer.offsetY);
    } else {
      viewport.scrollLeft = pointer.left - dx;
      viewport.scrollTop = pointer.top - dy;
    }
  });

  function endDrag(event) {
    if (pointer?.id !== event.pointerId) return;
    const {item, previous} = pointer;
    pointer = undefined;
    viewport.classList.remove('is-dragging');
    item?.classList.remove('is-moving');
    if (!dragged) return;
    if (item && event.type === 'pointerup') {
      persistPosition(item, previous);
    } else if (item) {
      setPosition(item, previous);
      updateMapSize();
      drawEdges();
    }
    saveView();
  }

  window.addEventListener('pointerup', endDrag);
  window.addEventListener('pointercancel', endDrag);
  viewport.addEventListener('lostpointercapture', event => {
    if (event.target === viewport) endDrag(event);
  });
  viewport.addEventListener('click', event => {
    if ((arranging && event.target.closest('.learning-node')) || (dragged && event.detail !== 0)) {
      event.preventDefault();
      event.stopImmediatePropagation();
    }
  }, true);

  let saveFrame;
  viewport.addEventListener('scroll', () => {
    cancelAnimationFrame(saveFrame);
    saveFrame = requestAnimationFrame(saveView);
  }, {passive: true});
  window.addEventListener('pagehide', saveView);

  updateBoardSize();
  if (savedView && [savedView.zoom, savedView.x, savedView.y].every(Number.isFinite)) {
    setZoom(savedView.zoom);
    centerOn(hub);
    viewport.scrollLeft += savedView.x * zoom;
    viewport.scrollTop += savedView.y * zoom;
  } else {
    centerOn(hub);
  }

  if (selected) {
    const visible = viewport.getBoundingClientRect();
    const target = selected.getBoundingClientRect();
    if (target.left < visible.left || target.right > visible.right
        || target.top < visible.top || target.bottom > visible.bottom) {
      centerOn(selected);
    }
  }
  new ResizeObserver(() => {
    updateBoardSize();
    drawEdges();
    updateControls();
  }).observe(viewport);

  const closeNode = document.querySelector('[data-close-node]');
  closeNode?.addEventListener('click', event => {
    if (event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return;
    event.preventDefault();
    closeNode.closest('#node-detail').hidden = true;
    selected?.removeAttribute('aria-current');
    selected?.focus({preventScroll: true});
    history.replaceState(null, '', closeNode.href);
    drawEdges();
  });
}

// Move keyboard focus to the linked content after the full-page navigation.
if (location.hash === '#node-detail' || location.hash === '#review') {
  document.getElementById(location.hash.slice(1))?.focus({preventScroll: true});
}

// A connection can only have one role in a given editor.
for (const connection of document.querySelectorAll('.learning-connection')) {
  connection.addEventListener('change', event => {
    if (event.target.checked) {
      for (const checkbox of connection.querySelectorAll('input[type="checkbox"]')) {
        if (checkbox !== event.target) checkbox.checked = false;
      }
    }
  });
}

const editor = document.querySelector('[data-node-editor]');
if (editor && typeof HTMLDialogElement !== 'undefined' && HTMLDialogElement.prototype.showModal) {
  const content = editor.querySelector('[data-editor-content]');
  const summary = editor.querySelector('summary');
  const close = content.querySelector('[data-editor-close]');
  const dialog = document.createElement('dialog');
  dialog.className = 'learning-editor-dialog';
  dialog.setAttribute('aria-labelledby', 'learning-editor-title');
  dialog.append(content);

  const trigger = document.createElement('button');
  trigger.type = 'button';
  trigger.className = 'learning-button learning-secondary learning-editor-trigger';
  trigger.setAttribute('aria-haspopup', 'dialog');
  trigger.setAttribute('aria-label', 'Agregar nodo');
  trigger.title = 'Agregar nodo';
  trigger.append(...summary.childNodes);
  trigger.addEventListener('click', () => {
    dialog.showModal();
    dialog.querySelector('[name="title"]').focus();
  });
  close.hidden = false;
  close.addEventListener('click', () => dialog.close());
  dialog.addEventListener('close', () => trigger.focus());
  editor.replaceWith(trigger, dialog);
}

const activeNav = document.querySelector('.app-nav-link.active');
if (activeNav && matchMedia('(max-width: 767.98px)').matches) {
  const navigation = activeNav.parentElement;
  navigation.scrollLeft += activeNav.getBoundingClientRect().left - navigation.getBoundingClientRect().left
    - (navigation.clientWidth - activeNav.offsetWidth) / 2;
}
