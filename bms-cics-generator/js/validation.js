/**
 * Screen-coordinate and BMS source-column validation.
 * Screen space is 24x80. Source space is columns 1-72.
 */
(function (global) {
  var BMS = global.BMS || {};

  var SCREEN_ROWS = 24;
  var SCREEN_COLS = 80;
  var SOURCE_CONT_COL = 72;
  var SOURCE_MAX_CONTENT = 71;
  var SOURCE_MAX_CONTINUED = 71;

  var LABEL_RE = /^[A-Z][A-Z0-9]{0,7}$/;
  var FIELD_NAME_RE = /^[A-Z][A-Z0-9]{0,6}$/;
  var COLORS = ["BLUE", "GREEN", "NEUTRAL", "PINK", "RED", "TURQUOISE", "YELLOW"];

  /**
   * 3270 attribute byte sits at POS. Data starts at POS+1 and ends at POS+LENGTH.
   * A field at POS=(1,1) with LENGTH=79 occupies columns 2-80.
   */
  function lastDataColumn(element) {
    return element.column + element.length;
  }

  function maxDataLength(column, columns) {
    return (columns || SCREEN_COLS) - column;
  }

  function elementExtent(element) {
    return lastDataColumn(element);
  }

  function validateElement(element, screen) {
    var errors = [];
    var rows = screen && screen.rows ? screen.rows : SCREEN_ROWS;
    var cols = screen && screen.columns ? screen.columns : SCREEN_COLS;

    if (!Number.isInteger(element.row) || element.row < 1 || element.row > rows) {
      errors.push("Row must be between 1 and " + rows + ".");
    }

    if (!Number.isInteger(element.column) || element.column < 1 || element.column > cols) {
      errors.push("POS column must be between 1 and " + cols + ".");
    }

    if (!Number.isInteger(element.length) || element.length < 1) {
      errors.push("Length must be at least 1.");
    }

    if (
      Number.isInteger(element.column) &&
      Number.isInteger(element.length) &&
      lastDataColumn(element) > cols
    ) {
      errors.push(
        "Field overflows the screen. POS=(" +
          element.row +
          "," +
          element.column +
          ") uses an attribute byte, so data starts at column " +
          (element.column + 1) +
          " and ends at column " +
          lastDataColumn(element) +
          ". Maximum length here is " +
          maxDataLength(element.column, cols) +
          "."
      );
    }

    if (element.color && COLORS.indexOf(String(element.color).toUpperCase()) === -1) {
      errors.push(
        "Color must be BLUE, GREEN, NEUTRAL, PINK, RED, TURQUOISE, or YELLOW."
      );
    }

    if (element.type === "text") {
      if (!element.value) {
        errors.push("Text cannot be empty.");
      }
    } else {
      if (!element.name) {
        errors.push("Name is required.");
      } else if (!FIELD_NAME_RE.test(String(element.name).toUpperCase())) {
        errors.push(
          "Name must start with a letter, use A-Z and 0-9 only, and be 1-7 characters (BMS label columns 1-7)."
        );
      }
    }

    return errors;
  }

  function validateHeader(screen) {
    var errors = [];
    var fields = [
      ["mapset", "Mapset"],
      ["mapName", "Map name"],
      ["screenName", "Screen name"]
    ];

    fields.forEach(function (pair) {
      var value = String(screen[pair[0]] || "").toUpperCase();
      if (!value) {
        errors.push(pair[1] + " is required.");
      } else if (!LABEL_RE.test(value)) {
        errors.push(
          pair[1] +
            " must start with a letter, use A-Z and 0-9 only, and be 1-8 characters."
        );
      }
    });

    return errors;
  }

  function namedElements(screen) {
    return (screen.elements || []).filter(function (el) {
      return el.type !== "text" && el.name;
    });
  }

  function validateUniqueNames(screen, candidate) {
    var errors = [];
    if (!candidate || candidate.type === "text" || !candidate.name) {
      return errors;
    }

    var name = String(candidate.name).toUpperCase();
    var clash = namedElements(screen).some(function (el) {
      return el.id !== candidate.id && String(el.name).toUpperCase() === name;
    });

    if (clash) {
      errors.push("Name " + name + " is already used by another field.");
    }

    return errors;
  }

  function validatePlacement(element, screen) {
    return validateElement(element, screen).concat(
      validateUniqueNames(screen, element)
    );
  }

  function validateScreen(screen) {
    var errors = validateHeader(screen);

    (screen.elements || []).forEach(function (el, index) {
      validatePlacement(el, screen).forEach(function (message) {
        errors.push("Element " + (index + 1) + ": " + message);
      });
    });

    return errors;
  }

  /**
   * Source-space checks. Do not reuse these numbers for the 24x80 screen.
   */
  function validateSource(source) {
    var errors = [];
    var lines = String(source || "").split("\n");

    lines.forEach(function (line, index) {
      var n = index + 1;
      if (line.length > SOURCE_CONT_COL) {
        errors.push(
          "Source line " +
            n +
            " is " +
            line.length +
            " columns; maximum is " +
            SOURCE_CONT_COL +
            "."
        );
      }

      if (line.length === SOURCE_CONT_COL && line.charAt(SOURCE_CONT_COL - 1) === "X") {
        if (line.length > SOURCE_CONT_COL) {
          errors.push("Source line " + n + " continues past column 72.");
        }
      } else if (line.length > SOURCE_MAX_CONTENT) {
        errors.push(
          "Source line " + n + " exceeds content column " + SOURCE_MAX_CONTENT + "."
        );
      }
    });

    return errors;
  }

  BMS.Validation = {
    SCREEN_ROWS: SCREEN_ROWS,
    SCREEN_COLS: SCREEN_COLS,
    SOURCE_CONT_COL: SOURCE_CONT_COL,
    SOURCE_MAX_CONTENT: SOURCE_MAX_CONTENT,
    SOURCE_MAX_CONTINUED: SOURCE_MAX_CONTINUED,
    LABEL_RE: LABEL_RE,
    FIELD_NAME_RE: FIELD_NAME_RE,
    COLORS: COLORS,
    lastDataColumn: lastDataColumn,
    maxDataLength: maxDataLength,
    elementExtent: elementExtent,
    validateElement: validateElement,
    validateHeader: validateHeader,
    validateUniqueNames: validateUniqueNames,
    validatePlacement: validatePlacement,
    validateScreen: validateScreen,
    validateSource: validateSource
  };

  global.BMS = BMS;
})(window);
