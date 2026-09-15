/**
 * Convert the logical 24x80 screen model into BMS definitions,
 * then hand them to the source formatter.
 */
(function (global) {
  var BMS = global.BMS || {};

  function firstInput(screen) {
    var list = BMS.Elements.sortedElements(
      Array.isArray(screen) ? screen : (screen && screen.elements) || []
    );
    var i;
    for (i = 0; i < list.length; i += 1) {
      if (list[i] && list[i].type === "input") {
        return list[i];
      }
    }
    return null;
  }

  function isInitialCursor(element, screen) {
    var first = firstInput(screen);
    return !!(first && element && first.id === element.id);
  }

  function attrbFor(element, screen) {
    if (element.type !== "input") {
      return "PROT";
    }

    var parts = ["UNPROT"];
    if (element.numeric) {
      parts.push("NUM");
    }
    if (isInitialCursor(element, screen)) {
      parts.push("IC");
    }
    parts.push("FSET");
    return "(" + parts.join(",") + ")";
  }

  function fieldLabel(element) {
    if (!element || element.type === "text" || !element.name) {
      return "";
    }
    return String(element.name)
      .toUpperCase()
      .replace(/[^A-Z0-9]/g, "")
      .slice(0, 7);
  }

  function fieldParams(element, screen) {
    var params = [
      "POS=(" + element.row + "," + element.column + ")",
      "LENGTH=" + element.length,
      "ATTRB=" + attrbFor(element, screen),
      "COLOR=" + (element.color || "GREEN").toUpperCase()
    ];

    if (element.type === "text") {
      params.push("INITIAL='" + BMS.Formatter.escapeInitial(element.value) + "'");
    }

    return params;
  }

  function generateDefinitions(screen) {
    var mapset = String(screen.mapset || screen.mapName || "MAPSET").toUpperCase();
    var mapName = String(screen.screenName || screen.mapName || "MAP").toUpperCase();

    var definitions = [
      {
        name: mapset,
        opcode: "DFHMSD",
        packed: true,
        params: [
          "TYPE=MAP",
          "STORAGE=AUTO",
          "LANG=COBOL",
          "TIOAPFX=YES",
          "TERM=3270",
          "CTRL=FREEKB",
          "MODE=INOUT"
        ]
      },
      {
        name: mapName,
        opcode: "DFHMDI",
        packed: true,
        params: [
          "SIZE=(" + screen.rows + "," + screen.columns + ")",
          "LINE=1",
          "COLUMN=1",
          "MAPATTS=COLOR"
        ]
      }
    ];

    BMS.Elements.sortedElements(screen.elements || []).forEach(function (element) {
      definitions.push({
        name: fieldLabel(element),
        opcode: "DFHMDF",
        packed: false,
        params: fieldParams(element, screen)
      });
    });

    definitions.push({
      name: "",
      opcode: "DFHMSD",
      packed: true,
      params: ["TYPE=FINAL"]
    });

    return definitions;
  }

  function formatDefinition(def) {
    if (def.packed) {
      return BMS.Formatter.formatPackedMacro(def.name, def.opcode, def.params);
    }
    return BMS.Formatter.formatFieldMacro(def.name, def.opcode, def.params);
  }

  function generate(screen) {
    return generateDefinitions(screen).map(formatDefinition).join("\n\n") + "\n";
  }

  function generateField(element, screen) {
    return formatDefinition({
      name: fieldLabel(element),
      opcode: "DFHMDF",
      packed: false,
      params: fieldParams(element, screen)
    }) + "\n";
  }

  function generateFields(elements, screen) {
    var list = BMS.Elements.sortedElements(elements || []);
    var context = screen || { elements: list };
    return list.map(function (element) {
      return generateField(element, context).replace(/\n$/, "");
    }).join("\n\n") + "\n";
  }

  BMS.Generator = {
    firstInput: firstInput,
    isInitialCursor: isInitialCursor,
    attrbFor: attrbFor,
    fieldLabel: fieldLabel,
    generateDefinitions: generateDefinitions,
    generate: generate,
    generateField: generateField,
    generateFields: generateFields
  };

  global.BMS = BMS;
})(window);
