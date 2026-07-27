/**
 * Personal System Design whiteboard (Fabric.js).
 * Stores an editable scene as JSON — never generates diagrams.
 */
window.SystemDesignWhiteboard = (function () {
  const COLORS = ['#111827', '#2563eb', '#059669', '#d97706', '#dc2626', '#7c3aed', '#ffffff'];
  const GRID = 24;

  function mount(host) {
    if (!host || typeof fabric === 'undefined') {
      return null;
    }

    const readonly = host.dataset.readonly === 'true';
    const autosave = host.dataset.autosave !== 'false';
    const saveUrl = host.dataset.saveUrl || null;
    const initialScene = host.dataset.scene || '';

    host.innerHTML = '';
    const toolbar = document.createElement('div');
    toolbar.className = 'sd-whiteboard-toolbar';
    const wrap = document.createElement('div');
    wrap.className = 'sd-whiteboard-canvas-wrap';
    const canvasEl = document.createElement('canvas');
    wrap.appendChild(canvasEl);
    if (!readonly) {
      host.appendChild(toolbar);
    }
    host.appendChild(wrap);

    const width = Math.max(host.clientWidth || 800, 320);
    const height = wrap.clientHeight || 480;
    canvasEl.width = width;
    canvasEl.height = height;

    const canvas = new fabric.Canvas(canvasEl, {
      selection: !readonly,
      preserveObjectStacking: true,
      fireRightClick: true,
      stopContextMenu: true
    });
    canvas.setWidth(width);
    canvas.setHeight(height);

    let tool = 'select';
    let strokeColor = COLORS[0];
    let fillColor = 'rgba(37, 99, 235, 0.08)';
    let snap = false;
    let isPanning = false;
    let lastPos = null;
    let drawingShape = null;
    let history = [];
    let historyIndex = -1;
    let applyingHistory = false;
    let saveTimer = null;
    const statusEl = document.getElementById('whiteboardSaveStatus');

    function setStatus(text) {
      if (statusEl) {
        statusEl.textContent = text;
      }
    }

    function serialize() {
      return JSON.stringify(
        canvas.toJSON(['name', 'sdType', 'selectable', 'evented'])
      );
    }

    function pushHistory() {
      if (applyingHistory || readonly) {
        return;
      }
      const snapshot = serialize();
      history = history.slice(0, historyIndex + 1);
      history.push(snapshot);
      if (history.length > 50) {
        history.shift();
      } else {
        historyIndex++;
      }
      scheduleSave();
    }

    function loadScene(json, push) {
      if (!json) {
        canvas.clear();
        canvas.backgroundColor = '#f8fafc';
        canvas.renderAll();
        if (push) {
          pushHistory();
        }
        return;
      }
      applyingHistory = true;
      canvas.loadFromJSON(json, () => {
        canvas.getObjects().forEach((obj) => {
          obj.set({
            selectable: !readonly,
            evented: !readonly
          });
        });
        canvas.renderAll();
        applyingHistory = false;
        if (push) {
          pushHistory();
        }
      });
    }

    function scheduleSave() {
      if (!autosave || !saveUrl || readonly) {
        return;
      }
      setStatus('Saving…');
      clearTimeout(saveTimer);
      saveTimer = setTimeout(persist, 700);
    }

    function persist() {
      fetch(saveUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sceneJson: serialize() })
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error('Save failed');
          }
          setStatus('Saved');
        })
        .catch(() => setStatus('Save failed'));
    }

    function snapValue(value) {
      return snap ? Math.round(value / GRID) * GRID : value;
    }

    function addText() {
      const text = new fabric.IText('Text', {
        left: 120,
        top: 120,
        fontSize: 18,
        fill: strokeColor,
        fontFamily: 'IBM Plex Sans, sans-serif',
        sdType: 'text'
      });
      canvas.add(text);
      canvas.setActiveObject(text);
      pushHistory();
    }

    function addContainer() {
      const rect = new fabric.Rect({
        left: 80,
        top: 80,
        width: 260,
        height: 180,
        fill: 'rgba(15, 23, 42, 0.03)',
        stroke: strokeColor,
        strokeWidth: 2,
        strokeDashArray: [8, 6],
        rx: 8,
        ry: 8,
        sdType: 'container'
      });
      const label = new fabric.IText('Container', {
        left: 92,
        top: 88,
        fontSize: 14,
        fill: strokeColor,
        fontFamily: 'IBM Plex Sans, sans-serif',
        sdType: 'text'
      });
      canvas.add(rect, label);
      canvas.setActiveObject(rect);
      pushHistory();
    }

    function createDiamond(left, top, size) {
      const half = size / 2;
      return new fabric.Polygon(
        [
          { x: half, y: 0 },
          { x: size, y: half },
          { x: half, y: size },
          { x: 0, y: half }
        ],
        {
          left,
          top,
          fill: fillColor,
          stroke: strokeColor,
          strokeWidth: 2,
          sdType: 'diamond'
        }
      );
    }

    function setTool(next) {
      tool = next;
      canvas.isDrawingMode = tool === 'draw';
      canvas.selection = tool === 'select';
      canvas.defaultCursor = tool === 'pan' ? 'grab' : tool === 'select' ? 'default' : 'crosshair';
      wrap.classList.toggle('panning', tool === 'pan');
      wrap.classList.toggle('drawing', tool === 'draw' || tool === 'rect' || tool === 'ellipse'
        || tool === 'diamond' || tool === 'line' || tool === 'arrow');
      if (canvas.isDrawingMode) {
        canvas.freeDrawingBrush.color = strokeColor;
        canvas.freeDrawingBrush.width = 2.5;
      }
      toolbar.querySelectorAll('[data-tool]').forEach((btn) => {
        btn.classList.toggle('active', btn.dataset.tool === tool);
      });
    }

    function buildToolbar() {
      const tools = [
        ['select', 'Select'],
        ['pan', 'Pan'],
        ['rect', 'Rect'],
        ['ellipse', 'Ellipse'],
        ['diamond', 'Diamond'],
        ['line', 'Line'],
        ['arrow', 'Arrow'],
        ['draw', 'Draw'],
        ['text', 'Text'],
        ['container', 'Container']
      ];
      tools.forEach(([id, label]) => {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'btn btn-outline-secondary';
        btn.dataset.tool = id;
        btn.textContent = label;
        btn.addEventListener('click', () => {
          if (id === 'text') {
            addText();
            setTool('select');
            return;
          }
          if (id === 'container') {
            addContainer();
            setTool('select');
            return;
          }
          setTool(id);
        });
        toolbar.appendChild(btn);
      });

      const colorSelect = document.createElement('select');
      colorSelect.className = 'form-select form-select-sm';
      colorSelect.style.width = 'auto';
      COLORS.forEach((color) => {
        const option = document.createElement('option');
        option.value = color;
        option.textContent = color;
        option.style.color = color === '#ffffff' ? '#111' : color;
        colorSelect.appendChild(option);
      });
      colorSelect.addEventListener('change', () => {
        strokeColor = colorSelect.value;
        fillColor =
          strokeColor === '#ffffff'
            ? 'rgba(255,255,255,0.9)'
            : strokeColor + '14';
        if (canvas.isDrawingMode) {
          canvas.freeDrawingBrush.color = strokeColor;
        }
        const active = canvas.getActiveObject();
        if (active) {
          if (active.set) {
            if (active.type === 'i-text' || active.type === 'text') {
              active.set('fill', strokeColor);
            } else {
              active.set('stroke', strokeColor);
            }
            canvas.requestRenderAll();
            pushHistory();
          }
        }
      });
      toolbar.appendChild(colorSelect);

      const snapBtn = document.createElement('button');
      snapBtn.type = 'button';
      snapBtn.className = 'btn btn-outline-secondary';
      snapBtn.textContent = 'Snap';
      snapBtn.addEventListener('click', () => {
        snap = !snap;
        snapBtn.classList.toggle('active', snap);
      });
      toolbar.appendChild(snapBtn);

      const undoBtn = document.createElement('button');
      undoBtn.type = 'button';
      undoBtn.className = 'btn btn-outline-secondary';
      undoBtn.textContent = 'Undo';
      undoBtn.addEventListener('click', () => {
        if (historyIndex <= 0) {
          return;
        }
        historyIndex--;
        applyingHistory = true;
        loadScene(history[historyIndex], false);
      });
      toolbar.appendChild(undoBtn);

      const redoBtn = document.createElement('button');
      redoBtn.type = 'button';
      redoBtn.className = 'btn btn-outline-secondary';
      redoBtn.textContent = 'Redo';
      redoBtn.addEventListener('click', () => {
        if (historyIndex >= history.length - 1) {
          return;
        }
        historyIndex++;
        applyingHistory = true;
        loadScene(history[historyIndex], false);
      });
      toolbar.appendChild(redoBtn);

      const delBtn = document.createElement('button');
      delBtn.type = 'button';
      delBtn.className = 'btn btn-outline-secondary';
      delBtn.textContent = 'Delete';
      delBtn.addEventListener('click', () => {
        canvas.getActiveObjects().forEach((obj) => canvas.remove(obj));
        canvas.discardActiveObject();
        canvas.requestRenderAll();
        pushHistory();
      });
      toolbar.appendChild(delBtn);
    }

    if (!readonly) {
      buildToolbar();
      setTool('select');
    }

    canvas.on('mouse:wheel', (opt) => {
      const delta = opt.e.deltaY;
      let zoom = canvas.getZoom();
      zoom *= 0.999 ** delta;
      zoom = Math.min(4, Math.max(0.2, zoom));
      canvas.zoomToPoint({ x: opt.e.offsetX, y: opt.e.offsetY }, zoom);
      opt.e.preventDefault();
      opt.e.stopPropagation();
    });

    canvas.on('mouse:down', (opt) => {
      if (readonly) {
        return;
      }
      const pointer = canvas.getPointer(opt.e);
      if (tool === 'pan' || opt.e.button === 1 || (opt.e.button === 0 && opt.e.altKey)) {
        isPanning = true;
        lastPos = { x: opt.e.clientX, y: opt.e.clientY };
        canvas.selection = false;
        return;
      }

      if (tool === 'rect' || tool === 'ellipse' || tool === 'diamond' || tool === 'line' || tool === 'arrow') {
        const x = snapValue(pointer.x);
        const y = snapValue(pointer.y);
        if (tool === 'rect') {
          drawingShape = new fabric.Rect({
            left: x,
            top: y,
            width: 1,
            height: 1,
            fill: fillColor,
            stroke: strokeColor,
            strokeWidth: 2,
            sdType: 'rect'
          });
        } else if (tool === 'ellipse') {
          drawingShape = new fabric.Ellipse({
            left: x,
            top: y,
            rx: 1,
            ry: 1,
            fill: fillColor,
            stroke: strokeColor,
            strokeWidth: 2,
            sdType: 'ellipse'
          });
        } else if (tool === 'diamond') {
          drawingShape = createDiamond(x, y, 2);
        } else if (tool === 'line' || tool === 'arrow') {
          drawingShape = new fabric.Line([x, y, x, y], {
            stroke: strokeColor,
            strokeWidth: 2,
            sdType: tool === 'arrow' ? 'arrow' : 'line'
          });
        }
        if (drawingShape) {
          canvas.add(drawingShape);
        }
      }
    });

    canvas.on('mouse:move', (opt) => {
      if (isPanning && lastPos) {
        const vpt = canvas.viewportTransform;
        vpt[4] += opt.e.clientX - lastPos.x;
        vpt[5] += opt.e.clientY - lastPos.y;
        canvas.requestRenderAll();
        lastPos = { x: opt.e.clientX, y: opt.e.clientY };
        return;
      }
      if (!drawingShape) {
        return;
      }
      const pointer = canvas.getPointer(opt.e);
      const x = snapValue(pointer.x);
      const y = snapValue(pointer.y);
      if (drawingShape.type === 'rect') {
        const originX = drawingShape.left;
        const originY = drawingShape.top;
        drawingShape.set({
          width: Math.max(1, Math.abs(x - originX)),
          height: Math.max(1, Math.abs(y - originY)),
          left: Math.min(originX, x),
          top: Math.min(originY, y)
        });
      } else if (drawingShape.type === 'ellipse') {
        const originX = drawingShape.left;
        const originY = drawingShape.top;
        drawingShape.set({
          rx: Math.max(1, Math.abs(x - originX) / 2),
          ry: Math.max(1, Math.abs(y - originY) / 2),
          left: Math.min(originX, x),
          top: Math.min(originY, y)
        });
      } else if (drawingShape.sdType === 'diamond') {
        const size = Math.max(2, Math.max(Math.abs(x - drawingShape.left), Math.abs(y - drawingShape.top)));
        const points = [
          { x: size / 2, y: 0 },
          { x: size, y: size / 2 },
          { x: size / 2, y: size },
          { x: 0, y: size / 2 }
        ];
        drawingShape.set({ points, width: size, height: size });
      } else if (drawingShape.type === 'line') {
        drawingShape.set({ x2: x, y2: y });
      }
      canvas.requestRenderAll();
    });

    canvas.on('mouse:up', () => {
      if (isPanning) {
        isPanning = false;
        canvas.selection = tool === 'select';
        return;
      }
      if (drawingShape) {
        if (drawingShape.sdType === 'arrow') {
          const x1 = drawingShape.x1;
          const y1 = drawingShape.y1;
          const x2 = drawingShape.x2;
          const y2 = drawingShape.y2;
          const angle = (Math.atan2(y2 - y1, x2 - x1) * 180) / Math.PI;
          const head = new fabric.Triangle({
            left: x2,
            top: y2,
            originX: 'center',
            originY: 'center',
            width: 12,
            height: 14,
            fill: strokeColor,
            angle: angle + 90,
            sdType: 'arrow-head'
          });
          canvas.add(head);
        }
        drawingShape = null;
        setTool('select');
        pushHistory();
      }
    });

    canvas.on('object:modified', () => pushHistory());
    canvas.on('path:created', () => pushHistory());
    canvas.on('object:removed', () => {
      if (!applyingHistory) {
        scheduleSave();
      }
    });

    document.addEventListener('keydown', (event) => {
      if (readonly || !host.contains(document.activeElement) && document.activeElement !== document.body) {
        // still allow delete when canvas focused via selection
      }
      if (event.key === 'Delete' || event.key === 'Backspace') {
        const active = canvas.getActiveObjects();
        if (active.length && !['INPUT', 'TEXTAREA'].includes(document.activeElement.tagName)) {
          active.forEach((obj) => canvas.remove(obj));
          canvas.discardActiveObject();
          canvas.requestRenderAll();
          pushHistory();
        }
      }
    });

    loadScene(initialScene, true);

    return {
      canvas,
      toJSON: serialize,
      loadJSON: (json) => loadScene(json, true)
    };
  }

  return { mount };
})();
