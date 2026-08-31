/**
 * Physical BMS source formatting.
 *
 * Source columns (1-based). Do not mix these with 24x80 screen coordinates.
 *
 *   1-8   name / label
 *   9     opcode (DFHMSD / DFHMDI / DFHMDF)
 *   16    continued parameter / first operand
 *   1-71  statement content
 *   72    continuation character X, immediately after column 71
 *         (no extra space; a pad space inside INITIAL would be another character)
 */
(function (global) {
  var BMS = global.BMS || {};

  var NAME_WIDTH = 8;
  var OPCODE_COLUMN = 9;
  var PARAM_COLUMN = 16;
  var CONTINUATION_COLUMN = 72;
  var MAX_CONTINUED_CONTENT = 71;
  var MAX_LAST_CONTENT = 71;

  function repeat(ch, count) {
    var out = "";
    var i;
    for (i = 0; i < count; i += 1) {
      out += ch;
    }
    return out;
  }

  function spaces(count) {
    return count > 0 ? repeat(" ", count) : "";
  }

  /**
   * Place content at a 1-based source column and optionally put X at column 72.
   * Column 72 is never appended blindly; it is padded to the physical column.
   */
  function formatLine(content, startColumn, continuation) {
    var start = startColumn || 1;
    var line = spaces(start - 1) + content;
    var maxContent = continuation ? MAX_CONTINUED_CONTENT : MAX_LAST_CONTENT;

    if (line.length > maxContent) {
      throw new Error(
        "BMS source content extends to column " +
          line.length +
          "; maximum is " +
          maxContent +
          (continuation ? " on a continued line." : ".")
      );
    }

    if (continuation) {
      return line + spaces(CONTINUATION_COLUMN - 1 - line.length) + "X";
    }

    return line;
  }

  function formatName(name) {
    var label = String(name || "")
      .toUpperCase()
      .replace(/[^A-Z0-9]/g, "")
      .slice(0, NAME_WIDTH);
    return label + spaces(NAME_WIDTH - label.length);
  }

  function escapeInitial(value) {
    return String(value || "").replace(/'/g, "''");
  }

  function remaining(line, continuation) {
    var max = continuation ? MAX_CONTINUED_CONTENT : MAX_LAST_CONTENT;
    return max - line.length;
  }

  function takeEscapedChunk(escaped, from, maxChars) {
    if (maxChars <= 0 || from >= escaped.length) {
      return "";
    }

    var end = Math.min(from + maxChars, escaped.length);
    if (
      end < escaped.length &&
      end > from &&
      escaped.charAt(end - 1) === "'" &&
      escaped.charAt(end) === "'"
    ) {
      end -= 1;
    }

    if (end <= from) {
      end = Math.min(from + maxChars, escaped.length);
    }

    return escaped.slice(from, end);
  }

  function parseQuotedParam(param) {
    var match = param.match(/^([A-Z][A-Z0-9]*)='([\s\S]*)'$/);
    if (!match) {
      return null;
    }
    return {
      keyword: match[1],
      escaped: match[2]
    };
  }

  /**
   * Write INITIAL='...' (or any quoted operand) across physical source lines.
   * Quotes and the keyword count toward the source length.
   */
  function writeQuotedParam(currentLine, param, moreParams) {
    var parsed = parseQuotedParam(param);
    if (!parsed) {
      throw new Error("Unable to split quoted BMS parameter: " + param);
    }

    var lines = [];
    var line = currentLine;
    var prefix = parsed.keyword + "='";
    var cursor = 0;

    function continueLine() {
      lines.push(formatLine(line, 1, true));
      line = spaces(PARAM_COLUMN - 1);
    }

    if (remaining(line, true) < prefix.length + 1) {
      if (line.replace(/\s/g, "").length) {
        continueLine();
      }
    }

    line += prefix;

    while (cursor < parsed.escaped.length) {
      var room = remaining(line, true);
      if (room < 1) {
        continueLine();
        room = remaining(line, true);
      }

      var chunk = takeEscapedChunk(parsed.escaped, cursor, room);
      if (!chunk) {
        continueLine();
        chunk = takeEscapedChunk(parsed.escaped, cursor, remaining(line, true));
      }

      line += chunk;
      cursor += chunk.length;

      if (cursor < parsed.escaped.length) {
        continueLine();
      }
    }

    var suffix = "'" + (moreParams ? "," : "");
    if (remaining(line, moreParams) < suffix.length) {
      continueLine();
    }
    line += suffix;

    return { lines: lines, line: line };
  }

  function writeToken(currentLines, currentLine, token, isLast, packed, firstLine) {
    var lines = currentLines;
    var line = currentLine;
    var quoted = parseQuotedParam(token.replace(/,$/, ""));
    var room = remaining(line, !isLast);

    function nextParamLine() {
      if (line.length) {
        lines.push(formatLine(line, 1, true));
      }
      line = spaces(PARAM_COLUMN - 1);
    }

    if (token.length <= room) {
      return { lines: lines, line: line + token };
    }

    if (quoted) {
      var split = writeQuotedParam(line, token.replace(/,$/, ""), !isLast);
      return { lines: lines.concat(split.lines), line: split.line };
    }

    if (packed || line !== firstLine) {
      nextParamLine();
      room = remaining(line, !isLast);
      if (token.length <= room) {
        return { lines: lines, line: line + token };
      }
      if (quoted) {
        split = writeQuotedParam(line, token.replace(/,$/, ""), !isLast);
        return { lines: lines.concat(split.lines), line: split.line };
      }
    }

    throw new Error("BMS parameter does not fit in a source line: " + token);
  }

  /**
   * Format a named assembler-style macro into physical source lines.
   * packed=true fills each line; packed=false writes one parameter per line.
   */
  function formatMacro(name, opcode, parameters, packed) {
    var params = parameters || [];
    var first = formatName(name) + String(opcode).toUpperCase() + (params.length ? " " : "");
    var lines = [];
    var line = first;
    var i;

    if (!params.length) {
      return first;
    }

    for (i = 0; i < params.length; i += 1) {
      var isLast = i === params.length - 1;
      var token = params[i] + (isLast ? "" : ",");

      if (!packed && i > 0) {
        lines.push(formatLine(line, 1, true));
        line = spaces(PARAM_COLUMN - 1);
      }

      var written = writeToken(lines, line, token, isLast, packed, first);
      lines = written.lines;
      line = written.line;
    }

    if (line.length) {
      lines.push(line);
    }

    return lines.join("\n");
  }

  function formatPackedMacro(name, opcode, parameters) {
    return formatMacro(name, opcode, parameters, true);
  }

  function formatFieldMacro(name, opcode, parameters) {
    return formatMacro(name, opcode, parameters, false);
  }

  BMS.Formatter = {
    NAME_WIDTH: NAME_WIDTH,
    OPCODE_COLUMN: OPCODE_COLUMN,
    PARAM_COLUMN: PARAM_COLUMN,
    CONTINUATION_COLUMN: CONTINUATION_COLUMN,
    MAX_CONTINUED_CONTENT: MAX_CONTINUED_CONTENT,
    MAX_LAST_CONTENT: MAX_LAST_CONTENT,
    spaces: spaces,
    formatLine: formatLine,
    formatName: formatName,
    escapeInitial: escapeInitial,
    formatMacro: formatMacro,
    formatPackedMacro: formatPackedMacro,
    formatFieldMacro: formatFieldMacro
  };

  global.BMS = BMS;
})(window);
