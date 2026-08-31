/**
 * 24x80 screen renderer and mouse interaction.
 * Positions snap to character cells. Never uses BMS source columns.
 */
(function (global) {
  var BMS = global.BMS || {};

  var ROWS = 24;
  var COLS = 80;
  var cellWidth = 11;
  var cellHeight = 18;

  var stage;
  var grid;
  var layer;
  var rubber;
  var colRuler;
  var rowRuler;
  var callbacks = {};
  var hoverCell = null;

  function readCellSize() {
    var styles = window.getComputedStyle(document.documentElement);
    var w = parseFloat(styles.getPropertyValue("--cell-w"));
    var h = parseFloat(styles.getPropertyValue("--cell-h"));
    if (w) {
      cellWidth = w;
    }
    if (h) {
      cellHeight = h;
    }
  }

  function clampCell(row, col) {
    if (row < 1 || row > ROWS || col < 1 || col > COLS) {
      return null;
    }
    return { row: row, col: col };
  }

  function cellFromEvent(event) {
    var rect = stage.getBoundingClientRect();
    var x = event.clientX - rect.left;
    var y = event.clientY - rect.top;
    var col = Math.floor(x / cellWidth) + 1;
    var row = Math.floor(y / cellHeight) + 1;
    return clampCell(row, col);
  }

  function cellIndex(row, col) {
    return (row - 1) * COLS + (col - 1);
  }

  function buildRulers() {
    var c;
    var r;
    colRuler.innerHTML = "";
    rowRuler.innerHTML = "";

    for (c = 1; c <= COLS; c += 1) {
      var mark = document.createElement("div");
      mark.className = "col-mark";
      if (c === 1 || c % 10 === 0) {
        mark.textContent = String(c);
      }
      colRuler.appendChild(mark);
    }

    for (r = 1; r <= ROWS; r += 1) {
      var rowMark = document.createElement("div");
      rowMark.className = "row-mark";
      rowMark.textContent = String(r);
      rowRuler.appendChild(rowMark);
    }
  }

  function buildGrid() {
    var frag = document.createDocumentFragment();
    var i;
    grid.innerHTML = "";
    for (i = 0; i < ROWS * COLS; i += 1) {
      var cell = document.createElement("div");
      cell.className = "cell";
      cell.dataset.row = String(Math.floor(i / COLS) + 1);
      cell.dataset.col = String((i % COLS) + 1);
      frag.appendChild(cell);
    }
    grid.appendChild(frag);
  }

  function clearCellState() {
    var cells = grid.children;
    var i;
    for (i = 0; i < cells.length; i += 1) {
      cells[i].classList.remove("is-hover", "is-draft");
    }
  }

  function markCell(row, col, className) {
    var cell = clampCell(row, col);
    if (!cell) {
      return;
    }
    grid.children[cellIndex(cell.row, cell.col)].classList.add(className);
  }

  function markRange(row, colA, colB, className) {
    var from = Math.min(colA, colB);
    var to = Math.max(colA, colB);
    var col;
    for (col = from; col <= to; col += 1) {
      markCell(row, col, className);
    }
  }

  function setRubber(start, end) {
    if (!start || !end) {
      rubber.classList.add("is-hidden");
      return;
    }

    var row = start.row;
    var col = Math.min(start.col, end.col);
    var length = Math.abs(end.col - start.col) + 1;

    rubber.style.left = (col - 1) * cellWidth + "px";
    rubber.style.top = (row - 1) * cellHeight + "px";
    rubber.style.width = length * cellWidth + "px";
    rubber.style.height = cellHeight + "px";
    rubber.classList.remove("is-hidden");
  }

  function hideRubber() {
    rubber.classList.add("is-hidden");
  }

  function displayValue(element) {
    if (element.type === "text") {
      return element.value;
    }

    if (element.type === "output") {
      var name = String(element.name || "").toUpperCase().replace(/[^A-Z0-9]/g, "").slice(0, 7);
      if (!name) {
        name = "OUT";
      }
      var body = name;
      while (body.length < element.length) {
        body += " ";
      }
      return body.slice(0, element.length);
    }

    var fill = "_";
    var out = "";
    var i;
    for (i = 0; i < element.length; i += 1) {
      out += fill;
    }
    return out;
  }

  function fieldNode(element, options) {
    options = options || {};
    var node = document.createElement("div");
    var color = String(element.color || "GREEN").toLowerCase();
    node.className = "field field-" + element.type + " field-color-" + color;
    node.dataset.id = element.id;
    node.style.left = (element.column - 1) * cellWidth + "px";
    node.style.top = (element.row - 1) * cellHeight + "px";
    node.style.width = (element.length + 1) * cellWidth + "px";

    var attr = document.createElement("span");
    attr.className = "field-ch field-attr";
    attr.title = "3270 attribute byte at POS=(" + element.row + "," + element.column + ")";
    attr.textContent = " ";
    node.appendChild(attr);

    var text = displayValue(element);
    var i;
    for (i = 0; i < element.length; i += 1) {
      var ch = document.createElement("span");
      ch.className = "field-ch";
      ch.textContent = text.charAt(i) || " ";
      node.appendChild(ch);
    }

    if (element.type === "output") {
      var cover = document.createElement("span");
      cover.className = "field-coverage";
      cover.setAttribute("aria-hidden", "true");
      node.appendChild(cover);

      var extent = document.createElement("span");
      extent.className = "field-extent";
      extent.textContent =
        (element.name ? String(element.name).toUpperCase() + " " : "") +
        "→" +
        (element.column + element.length);
      node.appendChild(extent);
    }

    if (options.selected) {
      node.classList.add("is-selected");
    }

    if (options.preview) {
      node.classList.add("is-preview");
      if (options.invalid) {
        node.classList.add("is-preview-invalid");
      }
      return node;
    }

    node.addEventListener("mousedown", function (event) {
      event.preventDefault();
      event.stopPropagation();
      if (callbacks.onElementPointerDown) {
        callbacks.onElementPointerDown(element, event);
      }
    });

    return node;
  }

  function previewOverflows(element, screen) {
    var cols = screen && screen.columns ? screen.columns : COLS;
    return !element.column || !element.length || element.column + element.length > cols;
  }

  function renderElements(state) {
    layer.innerHTML = "";

    state.screen.elements.forEach(function (element) {
      layer.appendChild(
        fieldNode(element, { selected: element.id === state.selectedId })
      );
    });

    (state.previewElements || []).forEach(function (element) {
      layer.appendChild(
        fieldNode(element, {
          preview: true,
          invalid: previewOverflows(element, state.screen)
        })
      );
    });
  }

  function updateHover(cell) {
    clearCellState();
    hoverCell = cell;
    if (cell) {
      markCell(cell.row, cell.col, "is-hover");
    }
    if (callbacks.onHover) {
      callbacks.onHover(cell);
    }
  }

  function init(elements, handlers) {
    stage = elements.stage;
    grid = elements.grid;
    layer = elements.layer;
    rubber = elements.rubber;
    colRuler = elements.colRuler;
    rowRuler = elements.rowRuler;
    callbacks = handlers || {};

    readCellSize();
    buildRulers();
    buildGrid();

    stage.addEventListener("mousedown", function (event) {
      if (event.button !== 0) {
        return;
      }
      var cell = cellFromEvent(event);
      if (!cell) {
        return;
      }
      event.preventDefault();
      if (callbacks.onGridPointerDown) {
        callbacks.onGridPointerDown(cell, event);
      }
    });

    window.addEventListener("mousemove", function (event) {
      if (!stage) {
        return;
      }
      var overStage = stage.contains(event.target) || event.target === stage;
      var cell = overStage ? cellFromEvent(event) : null;

      if (callbacks.isInteracting && callbacks.isInteracting()) {
        if (cell && callbacks.onHover) {
          callbacks.onHover(cell);
        }
        if (cell && callbacks.onPointerMove) {
          callbacks.onPointerMove(cell, event);
        }
        return;
      }

      if (overStage) {
        updateHover(cell);
      } else if (hoverCell) {
        updateHover(null);
      }
    });

    window.addEventListener("mouseup", function (event) {
      if (event.button !== 0) {
        return;
      }
      if (callbacks.onPointerUp) {
        callbacks.onPointerUp(cellFromEvent(event), event);
      }
    });
  }

  function render(state, draft) {
    readCellSize();
    renderElements(state);
    clearCellState();

    if (hoverCell) {
      markCell(hoverCell.row, hoverCell.col, "is-hover");
    }

    if (draft && draft.start && draft.end) {
      markRange(draft.start.row, draft.start.col, draft.end.col, "is-draft");
      setRubber(draft.start, draft.end);
    } else {
      hideRubber();
    }

    if (state.selectedId) {
      var selected = layer.querySelector('[data-id="' + state.selectedId + '"]');
      if (selected && state.dragging) {
        selected.classList.add("is-dragging");
      }
    }
  }

  function setDragging(id, dragging) {
    var node = layer.querySelector('[data-id="' + id + '"]');
    if (node) {
      node.classList.toggle("is-dragging", dragging);
    }
  }

  BMS.Screen = {
    ROWS: ROWS,
    COLS: COLS,
    init: init,
    render: render,
    cellFromEvent: cellFromEvent,
    setDragging: setDragging,
    hideRubber: hideRubber
  };

  global.BMS = BMS;
})(window);
