/**
 * Convert the logical 24x80 screen model into BMS definitions,
 * then hand them to the source formatter.
 */
(function (global) {
  var BMS = global.BMS || {};

  function attrbFor(element) {
    if (element.type === "input") {
      return "UNPROT";
    }
    return "PROT";
  }

  function fieldParams(element) {
    var params = [
      "POS=(" + element.row + "," + element.column + ")",
      "LENGTH=" + element.length,
      "ATTRB=" + attrbFor(element),
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
        params: ["SIZE=(" + screen.rows + "," + screen.columns + ")", "LINE=1", "COLUMN=1"]
      }
    ];

    (screen.elements || []).forEach(function (element) {
      definitions.push({
        name: "",
        opcode: "DFHMDF",
        packed: false,
        params: fieldParams(element)
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

  function generateField(element) {
    return formatDefinition({
      name: "",
      opcode: "DFHMDF",
      packed: false,
      params: fieldParams(element)
    }) + "\n";
  }

  function generateFields(elements) {
    return (elements || []).map(function (element) {
      return generateField(element).replace(/\n$/, "");
    }).join("\n\n") + "\n";
  }

  BMS.Generator = {
    attrbFor: attrbFor,
    generateDefinitions: generateDefinitions,
    generate: generate,
    generateField: generateField,
    generateFields: generateFields
  };

  global.BMS = BMS;
})(window);
