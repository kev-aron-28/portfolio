/**
 * One-click 3270 screen components: header, footer, and horizontal options.
 */
(function (global) {
  var BMS = global.BMS || {};
  var nextGroup = 1;

  function repeat(ch, count) {
    var out = "";
    var i;
    for (i = 0; i < count; i += 1) {
      out += ch;
    }
    return out;
  }

  function ruleChar(value) {
    var ch = String(value || "=").charAt(0);
    if (!ch || /\s/.test(ch)) {
      return "=";
    }
    return ch;
  }

  function rowBusy(screen, row) {
    return (screen.elements || []).some(function (el) {
      return el.row === row;
    });
  }

  function rangeBusy(screen, fromRow, toRow) {
    var row;
    for (row = fromRow; row <= toRow; row += 1) {
      if (rowBusy(screen, row)) {
        return true;
      }
    }
    return false;
  }

  function parseOptions(text) {
    return String(text || "")
      .split(/[,;|]+|\s{2,}/)
      .map(function (item) {
        return item.replace(/\s+/g, " ").trim();
      })
      .filter(Boolean);
  }

  function layoutHorizontal(items, row, columns) {
    var n = items.length;
    var total = 0;
    var i;

    if (!n) {
      return { ok: false, errors: ["Enter at least one footer option."] };
    }

    for (i = 0; i < n; i += 1) {
      if (items[i].length > columns - 1) {
        return {
          ok: false,
          errors: ["Option \"" + items[i] + "\" exceeds " + (columns - 1) + " data columns."]
        };
      }
      total += items[i].length;
    }

    if (total + Math.max(0, n - 1) > columns - 1) {
      return {
        ok: false,
        errors: ["Footer options do not fit on one " + columns + "-column row."]
      };
    }

    var placements = [];
    var between = n > 1 ? Math.max(1, Math.floor((columns - 1 - total) / (n + 1))) : 0;
    var used = total + between * Math.max(0, n - 1);
    var dataCol = n > 1 ? Math.max(2, Math.floor((columns - used) / 2) + 1) : 0;

    if (n === 1) {
      placements.push({
        row: row,
        column: BMS.Elements.centerColumn(items[0].length, columns),
        value: items[0]
      });
      return { ok: true, placements: placements };
    }

    for (i = 0; i < n; i += 1) {
      placements.push({
        row: row,
        column: BMS.Elements.posFromDataColumn(dataCol),
        value: items[i]
      });
      dataCol += items[i].length + between;
    }

    return { ok: true, placements: placements };
  }

  function buildHeader(screen, options) {
    var columns = screen.columns;
    var ch = ruleChar(options.rule);
    var title = BMS.Elements.normalizeText(options.title || "");
    var subtitle = BMS.Elements.normalizeText(options.subtitle || "");
    var color = options.color;
    var groupId = "header-" + nextGroup;
    nextGroup += 1;
    var extra = { color: color, columns: columns, groupId: groupId };
    var lastRow = subtitle ? 4 : 3;
    var rule = repeat(ch, columns - 1);

    if (!title) {
      return { ok: false, errors: ["Title is required."] };
    }
    if (title.length > columns - 1) {
      return { ok: false, errors: ["Title exceeds " + (columns - 1) + " data columns."] };
    }
    if (subtitle.length > columns - 1) {
      return { ok: false, errors: ["Subtitle exceeds " + (columns - 1) + " data columns."] };
    }
    if (rangeBusy(screen, 1, lastRow)) {
      return {
        ok: false,
        errors: [
          "Rows 1-" + lastRow + " already have elements. Move or delete them before adding a header."
        ]
      };
    }

    var elements = [
      BMS.Elements.buildText(1, 1, rule, extra),
      BMS.Elements.buildText(2, 1, title, {
        align: "center",
        columns: columns,
        color: color,
        groupId: groupId
      })
    ];

    if (subtitle) {
      elements.push(
        BMS.Elements.buildText(3, 1, subtitle, {
          align: "center",
          columns: columns,
          color: color,
          groupId: groupId
        })
      );
      elements.push(BMS.Elements.buildText(4, 1, rule, extra));
    } else {
      elements.push(BMS.Elements.buildText(3, 1, rule, extra));
    }

    return { ok: true, elements: elements };
  }

  function buildFooter(screen, options) {
    var columns = screen.columns;
    var rows = screen.rows;
    var ch = ruleChar(options.rule);
    var items = parseOptions(options.options);
    var color = options.color;
    var groupId = "footer-" + nextGroup;
    nextGroup += 1;
    var extra = { color: color, groupId: groupId };
    var top = rows - 2;
    var mid = rows - 1;
    var bot = rows;

    if (!items.length) {
      return { ok: false, errors: ["Enter at least one footer option."] };
    }
    if (rangeBusy(screen, top, bot)) {
      return {
        ok: false,
        errors: [
          "Rows " + top + "-" + bot + " already have elements. Move or delete them before adding a footer."
        ]
      };
    }

    var laid = layoutHorizontal(items, mid, columns);
    if (!laid.ok) {
      return laid;
    }

    var rule = repeat(ch, columns - 1);
    var elements = [BMS.Elements.buildText(top, 1, rule, extra)];
    laid.placements.forEach(function (place) {
      elements.push(BMS.Elements.buildText(place.row, place.column, place.value, extra));
    });
    elements.push(BMS.Elements.buildText(bot, 1, rule, extra));

    return { ok: true, elements: elements };
  }

  function firstFreeRow(screen, fromRow, toRow) {
    var start = fromRow || 1;
    var end = toRow || screen.rows;
    var row;
    for (row = start; row <= end; row += 1) {
      if (!rowBusy(screen, row)) {
        return row;
      }
    }
    return start;
  }

  function buildSeparator(screen, options) {
    var columns = screen.columns;
    var custom = String(options.custom || "").charAt(0);
    var ch = custom && !/\s/.test(custom) ? custom : ruleChar(options.rule);
    var row = Number(options.row);
    var groupId = "separator-" + nextGroup;
    nextGroup += 1;

    if (!Number.isInteger(row) || row < 1 || row > screen.rows) {
      return { ok: false, errors: ["Row must be between 1 and " + screen.rows + "."] };
    }
    if (rowBusy(screen, row)) {
      return {
        ok: false,
        errors: ["Row " + row + " already has elements. Choose an empty row."]
      };
    }

    return {
      ok: true,
      elements: [
        BMS.Elements.buildText(row, 1, repeat(ch, columns - 1), {
          color: options.color,
          groupId: groupId
        })
      ]
    };
  }

  BMS.Components = {
    parseOptions: parseOptions,
    layoutHorizontal: layoutHorizontal,
    firstFreeRow: firstFreeRow,
    buildHeader: buildHeader,
    buildFooter: buildFooter,
    buildSeparator: buildSeparator
  };

  global.BMS = BMS;
})(window);
