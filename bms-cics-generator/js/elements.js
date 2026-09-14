/**
 * Screen model: create, update, and delete 3270 elements.
 */
(function (global) {
  var BMS = global.BMS || {};
  var nextId = 1;

  function createScreen() {
    return {
      rows: 24,
      columns: 80,
      mapName: "MPFT00",
      mapset: "MPFT00",
      screenName: "SCRN1",
      elements: []
    };
  }

  function createId() {
    var id = "element-" + nextId;
    nextId += 1;
    return id;
  }

  function clone(element) {
    var copy = {
      id: element.id,
      type: element.type,
      row: element.row,
      column: element.column,
      length: element.length,
      color: element.color || "GREEN"
    };

    if (element.type === "text") {
      copy.value = element.value;
    } else {
      copy.name = element.name;
    }

    if (element.type === "input") {
      copy.numeric = !!element.numeric;
    }

    if (element.align) {
      copy.align = element.align;
    }

    if (element.groupId) {
      copy.groupId = element.groupId;
    }

    return copy;
  }

  function normalizeText(value) {
    return String(value || "").replace(/\r?\n/g, "");
  }

  function normalizeName(value) {
    return String(value || "")
      .toUpperCase()
      .trim();
  }

  function normalizeFieldName(value) {
    return String(value || "")
      .toUpperCase()
      .replace(/[^A-Z0-9]/g, "")
      .slice(0, 7);
  }

  function normalizeNumeric(value) {
    if (value === true || value === 1) {
      return true;
    }
    var text = String(value || "")
      .toUpperCase()
      .trim();
    return text === "NUM" || text === "NUMERIC" || text === "TRUE" || text === "1";
  }

  function normalizeColor(value) {
    var color = String(value || "GREEN")
      .toUpperCase()
      .trim();
    return color || "GREEN";
  }

  function posFromDataColumn(dataColumn) {
    return Math.max(1, Number(dataColumn) - 1);
  }

  function centerColumn(length, columns) {
    var width = columns || 80;
    var visibleStart = Math.max(2, Math.floor((width - length) / 2) + 1);
    return posFromDataColumn(visibleStart);
  }

  function normalizeAlign(value) {
    var align = String(value || "").toLowerCase();
    if (align === "left" || align === "center" || align === "right") {
      return align;
    }
    return "";
  }

  function alignedColumn(align, length, columns) {
    var width = columns || 80;
    var len = Number(length) || 1;

    if (align === "right") {
      return Math.max(1, width - len);
    }
    if (align === "center") {
      return centerColumn(len, width);
    }
    return 1;
  }

  function buildText(row, column, value, extra) {
    var text = normalizeText(value);
    var element = {
      id: createId(),
      type: "text",
      row: row,
      column: column,
      length: text.length,
      value: text,
      color: normalizeColor(extra && extra.color)
    };

    if (extra && extra.align) {
      element.align = normalizeAlign(extra.align);
      if (element.align) {
        element.column = alignedColumn(
          element.align,
          element.length,
          extra.columns || 80
        );
      }
    }

    if (extra && extra.groupId) {
      element.groupId = extra.groupId;
    }

    return element;
  }

  function buildField(type, row, column, name, length, extra) {
    var element = {
      id: createId(),
      type: type,
      row: row,
      column: column,
      length: Number(length),
      name: normalizeFieldName(name),
      color: normalizeColor(extra && extra.color)
    };

    if (type === "input") {
      element.numeric = normalizeNumeric(extra && extra.numeric);
    }

    if (extra && extra.groupId) {
      element.groupId = extra.groupId;
    }

    if (extra && extra.align) {
      element.align = normalizeAlign(extra.align);
      if (element.align) {
        element.column = alignedColumn(
          element.align,
          element.length,
          extra.columns || 80
        );
      }
    }

    return element;
  }

  function findGroup(screen, groupId) {
    if (!groupId) {
      return [];
    }
    return (screen.elements || []).filter(function (el) {
      return el.groupId === groupId;
    });
  }

  function findById(screen, id) {
    for (var i = 0; i < screen.elements.length; i += 1) {
      if (screen.elements[i].id === id) {
        return screen.elements[i];
      }
    }
    return null;
  }

  function findAt(screen, row, column) {
    for (var i = screen.elements.length - 1; i >= 0; i -= 1) {
      var el = screen.elements[i];
      if (el.row === row && column >= el.column && column <= el.column + el.length) {
        return el;
      }
    }
    return null;
  }

  function defaultFieldName(screen, type) {
    var prefix = type === "input" ? "INP" : "OUT";
    var used = {};

    screen.elements.forEach(function (el) {
      if (el.name) {
        used[String(el.name).toUpperCase()] = true;
      }
    });

    var n = 1;
    var name = prefix + n;
    while (used[name]) {
      n += 1;
      name = prefix + n;
    }
    return name;
  }

  function addElement(screen, element) {
    var errors = BMS.Validation.validatePlacement(element, screen);
    if (errors.length) {
      return { ok: false, errors: errors };
    }
    screen.elements.push(element);
    return { ok: true, element: element };
  }

  function addMany(screen, elements) {
    var pending = [];
    var i;

    for (i = 0; i < elements.length; i += 1) {
      var preview = {
        rows: screen.rows,
        columns: screen.columns,
        mapName: screen.mapName,
        mapset: screen.mapset,
        screenName: screen.screenName,
        elements: screen.elements.concat(pending)
      };
      var errors = BMS.Validation.validatePlacement(elements[i], preview);
      if (errors.length) {
        return { ok: false, errors: errors };
      }
      pending.push(elements[i]);
    }

    pending.forEach(function (element) {
      screen.elements.push(element);
    });

    return { ok: true, elements: pending };
  }

  function applyPatch(element, patch, screen) {
    var next = clone(element);

    if (patch.row !== undefined) {
      next.row = Number(patch.row);
    }
    if (patch.column !== undefined) {
      next.column = Number(patch.column);
      if (patch.align === undefined) {
        delete next.align;
      }
    }
    if (patch.length !== undefined) {
      next.length = Number(patch.length);
    }
    if (patch.value !== undefined && next.type === "text") {
      next.value = normalizeText(patch.value);
      next.length = next.value.length;
    }
    if (patch.name !== undefined && next.type !== "text") {
      next.name = normalizeFieldName(patch.name);
    }
    if (patch.color !== undefined) {
      next.color = normalizeColor(patch.color);
    }
    if (patch.numeric !== undefined && next.type === "input") {
      next.numeric = normalizeNumeric(patch.numeric);
    }
    if (patch.align !== undefined) {
      next.align = normalizeAlign(patch.align);
      if (!next.align) {
        delete next.align;
      }
    }

    if (next.align && screen) {
      next.column = alignedColumn(next.align, next.length, screen.columns);
    }

    return next;
  }

  function updateElement(screen, id, patch) {
    var current = findById(screen, id);
    if (!current) {
      return { ok: false, errors: ["Element not found."] };
    }

    var next = applyPatch(current, patch, screen);
    var errors = BMS.Validation.validatePlacement(next, screen);
    if (errors.length) {
      return { ok: false, errors: errors, element: current };
    }

    current.row = next.row;
    current.column = next.column;
    current.length = next.length;
    current.color = next.color;
    if (next.align) {
      current.align = next.align;
    } else {
      delete current.align;
    }
    if (current.type === "text") {
      current.value = next.value;
    } else {
      current.name = next.name;
    }
    if (current.type === "input") {
      current.numeric = !!next.numeric;
    }

    return { ok: true, element: current };
  }

  function deleteElement(screen, id) {
    var index = -1;
    for (var i = 0; i < screen.elements.length; i += 1) {
      if (screen.elements[i].id === id) {
        index = i;
        break;
      }
    }

    if (index === -1) {
      return { ok: false, errors: ["Element not found."] };
    }

    screen.elements.splice(index, 1);
    return { ok: true };
  }

  function resetIdsFrom(elements) {
    var max = 0;
    (elements || []).forEach(function (el) {
      var match = String(el && el.id ? el.id : "").match(/(\d+)$/);
      if (match) {
        max = Math.max(max, Number(match[1]));
      }
    });
    nextId = max + 1;
  }

  function cloneScreen(screen) {
    var source = screen || createScreen();
    return {
      rows: 24,
      columns: 80,
      mapName: normalizeName(source.mapName || "MPFT00"),
      mapset: normalizeName(source.mapset || source.mapName || "MPFT00"),
      screenName: normalizeName(source.screenName || "SCRN1"),
      elements: (source.elements || []).map(function (element) {
        return clone(element);
      })
    };
  }

  function importScreen(raw) {
    var screen = cloneScreen(raw && typeof raw === "object" ? raw : createScreen());
    resetIdsFrom(screen.elements);
    screen.elements.forEach(function (element) {
      if (!element.id) {
        element.id = createId();
      }
    });
    return screen;
  }

  BMS.Elements = {
    createScreen: createScreen,
    createId: createId,
    clone: clone,
    normalizeText: normalizeText,
    normalizeName: normalizeName,
    normalizeFieldName: normalizeFieldName,
    normalizeNumeric: normalizeNumeric,
    normalizeColor: normalizeColor,
    buildText: buildText,
    buildField: buildField,
    findById: findById,
    findAt: findAt,
    findGroup: findGroup,
    defaultFieldName: defaultFieldName,
    addElement: addElement,
    addMany: addMany,
    posFromDataColumn: posFromDataColumn,
    centerColumn: centerColumn,
    alignedColumn: alignedColumn,
    normalizeAlign: normalizeAlign,
    updateElement: updateElement,
    deleteElement: deleteElement,
    cloneScreen: cloneScreen,
    importScreen: importScreen
  };

  global.BMS = BMS;
})(window);
