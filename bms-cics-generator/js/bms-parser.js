/**
 * Parse BMS source (DFHMSD / DFHMDI / DFHMDF) back into a screen model.
 */
(function (global) {
  var BMS = global.BMS || {};

  function stripLine(line) {
    var raw = String(line || "").replace(/\r$/, "");
    if (raw.length > 72) {
      raw = raw.slice(0, 72);
    }
    return raw;
  }

  function isComment(line) {
    return /^\s*\*/.test(line);
  }

  function quoteState(text, inQuote) {
    var i;
    for (i = 0; i < text.length; i += 1) {
      if (text.charAt(i) === "'") {
        inQuote = !inQuote;
      }
    }
    return inQuote;
  }

  function joinStatements(source) {
    var lines = String(source || "")
      .replace(/\r\n/g, "\n")
      .replace(/\r/g, "\n")
      .split("\n");
    var statements = [];
    var current = "";
    var inQuote = false;
    var i;
    var raw;
    var continued;
    var body;

    for (i = 0; i < lines.length; i += 1) {
      raw = stripLine(lines[i]);
      if (!current && (!raw.trim() || isComment(raw))) {
        continue;
      }

      continued = raw.length >= 72 && raw.charAt(71).toUpperCase() === "X";
      body = continued ? raw.slice(0, 71) : raw;
      if (!inQuote) {
        if (current) {
          body = body.replace(/^\s+/, "");
        }
        body = body.replace(/\s+$/, "");
      }

      if (current && !inQuote) {
        current += " " + body;
      } else {
        current += body;
      }
      inQuote = quoteState(body, inQuote);

      if (!continued) {
        if (current.trim()) {
          statements.push(current.trim());
        }
        current = "";
        inQuote = false;
      }
    }

    if (current.trim()) {
      statements.push(current.trim());
    }
    return statements;
  }

  function parseStatement(text) {
    var aligned = text.match(/^(.{8})(DFHMSD|DFHMDI|DFHMDF)\b\s*(.*)$/i);
    if (aligned) {
      return {
        name: aligned[1].trim().toUpperCase(),
        opcode: aligned[2].toUpperCase(),
        paramsText: aligned[3] || ""
      };
    }

    var loose = text.match(/^(\S+)?\s*(DFHMSD|DFHMDI|DFHMDF)\b\s*(.*)$/i);
    if (loose && (!loose[1] || !/^DFHM/.test(loose[1].toUpperCase()))) {
      return {
        name: (loose[1] || "").toUpperCase(),
        opcode: loose[2].toUpperCase(),
        paramsText: loose[3] || ""
      };
    }

    var unnamed = text.match(/^(DFHMSD|DFHMDI|DFHMDF)\b\s*(.*)$/i);
    if (unnamed) {
      return {
        name: "",
        opcode: unnamed[1].toUpperCase(),
        paramsText: unnamed[2] || ""
      };
    }

    return null;
  }

  function splitParams(text) {
    var parts = [];
    var cur = "";
    var depth = 0;
    var inQuote = false;
    var i;
    var ch;

    for (i = 0; i < text.length; i += 1) {
      ch = text.charAt(i);
      if (ch === "'") {
        inQuote = !inQuote;
        cur += ch;
      } else if (!inQuote && ch === "(") {
        depth += 1;
        cur += ch;
      } else if (!inQuote && ch === ")") {
        depth -= 1;
        cur += ch;
      } else if (!inQuote && depth <= 0 && ch === ",") {
        if (cur.trim()) {
          parts.push(cur.trim());
        }
        cur = "";
      } else {
        cur += ch;
      }
    }
    if (cur.trim()) {
      parts.push(cur.trim());
    }
    return parts;
  }

  function parseParams(text) {
    var out = {};
    splitParams(text).forEach(function (part) {
      var eq = part.indexOf("=");
      var key;
      var value;
      if (eq === -1) {
        return;
      }
      key = part.slice(0, eq).toUpperCase().trim();
      value = part.slice(eq + 1).trim();
      out[key] = value;
    });
    return out;
  }

  function parsePair(value) {
    var match = String(value || "").match(/\(\s*(\d+)\s*,\s*(\d+)\s*\)/);
    if (!match) {
      return null;
    }
    return [Number(match[1]), Number(match[2])];
  }

  function parseNumber(value) {
    var n = parseInt(String(value || "").replace(/[^\d-]/g, ""), 10);
    return Number.isInteger(n) ? n : null;
  }

  function unescapeInitial(value) {
    var text = String(value || "").trim();
    if (text.charAt(0) === "'" && text.charAt(text.length - 1) === "'") {
      text = text.slice(1, -1);
    }
    return text.replace(/''/g, "'");
  }

  function parseAttrb(value) {
    var raw = String(value || "").toUpperCase();
    raw = raw.replace(/[()]/g, " ");
    var parts = raw.split(/[\s,]+/).filter(Boolean);
    var flags = {};
    parts.forEach(function (part) {
      flags[part] = true;
    });
    return flags;
  }

  function fieldFromMacro(stmt) {
    var params = parseParams(stmt.paramsText);
    var pos = parsePair(params.POS);
    var length = parseNumber(params.LENGTH);
    var attrb = parseAttrb(params.ATTRB);
    var color = params.COLOR ? String(params.COLOR).toUpperCase() : "";
    var initial = params.INITIAL != null ? unescapeInitial(params.INITIAL) : "";
    var name = stmt.name || "";

    if (!pos) {
      return null;
    }

    var element;
    if (attrb.UNPROT) {
      element = BMS.Elements.buildField("input", pos[0], pos[1], name, length || 1, {
        color: color || "TURQUOISE",
        numeric: !!attrb.NUM
      });
    } else if (initial) {
      element = BMS.Elements.buildText(pos[0], pos[1], initial, {
        color: color || "GREEN"
      });
    } else if (name) {
      element = BMS.Elements.buildField("output", pos[0], pos[1], name, length || 1, {
        color: color || "YELLOW"
      });
    } else {
      return null;
    }

    if (length && element.type !== "text") {
      element.length = length;
    }
    if (color) {
      element.color = BMS.Elements.normalizeColor(color);
    }
    return element;
  }

  function parse(source) {
    var statements = joinStatements(source);
    var screen = BMS.Elements.createScreen();
    var found = false;
    var i;
    var stmt;
    var params;
    var size;
    var type;
    var element;

    screen.elements = [];

    for (i = 0; i < statements.length; i += 1) {
      stmt = parseStatement(statements[i]);
      if (!stmt) {
        continue;
      }

      if (stmt.opcode === "DFHMSD") {
        params = parseParams(stmt.paramsText);
        type = String(params.TYPE || "").toUpperCase();
        if (type === "FINAL") {
          continue;
        }
        found = true;
        if (stmt.name) {
          screen.mapset = BMS.Elements.normalizeName(stmt.name);
          screen.mapName = screen.mapset;
        }
      } else if (stmt.opcode === "DFHMDI") {
        found = true;
        if (stmt.name) {
          screen.screenName = BMS.Elements.normalizeName(stmt.name);
          if (!screen.mapName) {
            screen.mapName = screen.screenName;
          }
        }
        params = parseParams(stmt.paramsText);
        size = parsePair(params.SIZE);
        if (size) {
          screen.rows = size[0] || 24;
          screen.columns = size[1] || 80;
        }
      } else if (stmt.opcode === "DFHMDF") {
        found = true;
        element = fieldFromMacro(stmt);
        if (element) {
          screen.elements.push(element);
        }
      }
    }

    if (!found) {
      return { ok: false, error: "No DFHMSD, DFHMDI, or DFHMDF macros were found." };
    }

    screen = BMS.Elements.importScreen(screen);
    return {
      ok: true,
      screen: screen,
      name: screen.screenName || screen.mapset || "IMPORTED"
    };
  }

  BMS.Parser = {
    parse: parse,
    joinStatements: joinStatements
  };

  global.BMS = BMS;
})(window);
