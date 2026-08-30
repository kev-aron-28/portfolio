/**
 * Application wiring: tools, dialogs, properties, and live BMS preview.
 */
(function () {
  var state = {
    screen: BMS.Elements.createScreen(),
    selectedId: null,
    tool: "select",
    dragging: false
  };

  var interaction = null;
  var lastBms = "";

  var cursorPos = document.getElementById("cursor-pos");
  var toolHint = document.getElementById("tool-hint");
  var propertiesBody = document.getElementById("properties-body");
  var validationMsg = document.getElementById("validation-msg");
  var bmsSource = document.getElementById("bms-source");
  var bmsLines = document.getElementById("bms-lines");
  var copyBtn = document.getElementById("btn-copy");
  var downloadBtn = document.getElementById("btn-download");
  var modal = document.getElementById("modal");
  var modalTitle = document.getElementById("modal-title");
  var modalError = document.getElementById("modal-error");
  var modalForm = document.getElementById("modal-form");

  var hints = {
    select: "Select an element to move it, choose Text / Input / Output, or add a Header / Footer component.",
    text: "Click a start cell on the screen, then enter the protected text.",
    input: "Click or drag on the screen to place an unprotected input field.",
    output: "Click or drag on the screen to place a protected output field."
  };

  function showError(message) {
    validationMsg.textContent = message || "";
    validationMsg.classList.toggle("is-ok", false);
  }

  function showOk(message) {
    validationMsg.textContent = message || "";
    validationMsg.classList.toggle("is-ok", true);
  }

  function copySource(text, okMessage) {
    if (!text) {
      showError("Nothing to copy.");
      return;
    }

    function copied() {
      showOk(okMessage || "Copied.");
      window.setTimeout(function () {
        if (validationMsg.classList.contains("is-ok")) {
          validationMsg.textContent = "";
          validationMsg.classList.remove("is-ok");
        }
      }, 1600);
    }

    function fallbackCopy() {
      var area = document.createElement("textarea");
      area.value = text;
      document.body.appendChild(area);
      area.select();
      try {
        document.execCommand("copy");
        copied();
      } catch (err) {
        showError("Could not copy the BMS source.");
      }
      document.body.removeChild(area);
    }

    if (navigator.clipboard && navigator.clipboard.writeText) {
      navigator.clipboard.writeText(text).then(copied).catch(fallbackCopy);
      return;
    }
    fallbackCopy();
  }

  function sourceLineCount(text) {
    var lines = String(text || "").split("\n");
    if (lines.length && lines[lines.length - 1] === "") {
      lines.pop();
    }
    return lines.length;
  }

  function renderLineNumbers(text) {
    var count = sourceLineCount(text);
    var width = String(count || 1).length;
    var nums = [];
    var i;
    var label;

    for (i = 1; i <= count; i += 1) {
      label = String(i);
      while (label.length < width) {
        label = " " + label;
      }
      nums.push(label);
    }

    bmsLines.textContent = nums.join("\n");
  }

  function refreshBms() {
    try {
      lastBms = BMS.Generator.generate(state.screen);
      bmsSource.textContent = lastBms;
      renderLineNumbers(lastBms);
      var sourceErrors = BMS.Validation.validateSource(lastBms);
      if (sourceErrors.length) {
        showError(sourceErrors[0]);
      }
    } catch (err) {
      lastBms = "";
      bmsSource.textContent = "* BMS formatting error:\n" + err.message;
      renderLineNumbers(bmsSource.textContent);
      showError(err.message);
    }
  }

  function render(draft) {
    BMS.Screen.render(state, draft);
    renderProperties();
    refreshBms();
  }

  function selectedElement() {
    return BMS.Elements.findById(state.screen, state.selectedId);
  }

  function syncPropertyFields() {
    var el = selectedElement();
    if (!el) {
      return;
    }
    var row = propertiesBody.querySelector('input[name="row"]');
    var column = propertiesBody.querySelector('input[name="column"]');
    var length = propertiesBody.querySelector('input[name="length"]');
    if (row) {
      row.value = String(el.row);
    }
    if (column) {
      column.value = String(el.column);
    }
    if (length) {
      length.value = String(el.length);
    }
  }

  function typeLabel(type) {
    if (type === "input") {
      return "Input";
    }
    if (type === "output") {
      return "Output";
    }
    return "Text";
  }

  function inputEl(name, value, extraClass) {
    var input = document.createElement("input");
    input.name = name;
    input.value = value;
    input.spellcheck = false;
    if (extraClass) {
      input.className = extraClass;
    }
    return input;
  }

  function colorSelect(value) {
    var select = document.createElement("select");
    select.name = "color";
    select.className = "color-select";
    BMS.Validation.COLORS.forEach(function (color) {
      var opt = document.createElement("option");
      opt.value = color;
      opt.textContent = color;
      if (color === (value || "GREEN")) {
        opt.selected = true;
      }
      select.appendChild(opt);
    });
    return select;
  }

  function colorField(value) {
    return {
      name: "color",
      label: "Color",
      type: "select",
      value: value || "GREEN",
      options: BMS.Validation.COLORS.map(function (color) {
        return { value: color, label: color };
      })
    };
  }

  function labeled(labelText, control) {
    var wrap = document.createElement("label");
    wrap.className = "prop-field";
    wrap.appendChild(document.createTextNode(labelText));
    wrap.appendChild(control);
    return wrap;
  }

  function renderProperties() {
    var el = selectedElement();
    propertiesBody.innerHTML = "";

    if (!el) {
      var empty = document.createElement("p");
      empty.className = "properties-empty";
      empty.textContent = "No element selected.";
      propertiesBody.appendChild(empty);
      return;
    }

    var grid = document.createElement("div");
    grid.className = "prop-grid";

    var type = document.createElement("div");
    type.className = "prop-field";
    type.innerHTML = "Type<span class=\"prop-type\">" + typeLabel(el.type) + "</span>";
    grid.appendChild(type);

    if (el.type !== "text") {
      grid.appendChild(labeled("Name", inputEl("name", el.name, "wide")));
    }

    var row = inputEl("row", String(el.row), "narrow");
    row.type = "number";
    row.min = "1";
    row.max = String(state.screen.rows);
    grid.appendChild(labeled("Row", row));

    var col = inputEl("column", String(el.column), "narrow");
    col.type = "number";
    col.min = "1";
    col.max = String(state.screen.columns);
    grid.appendChild(labeled("Column", col));

    var length = inputEl("length", String(el.length), "narrow");
    length.type = "number";
    length.min = "1";
    length.max = String(state.screen.columns);
    if (el.type === "text") {
      length.readOnly = true;
      length.title = "Length is taken from the text.";
    }
    grid.appendChild(labeled("Length", length));

    if (el.type === "text") {
      grid.appendChild(labeled("Value", inputEl("value", el.value, "wide")));
    }

    grid.appendChild(labeled("Color", colorSelect(el.color)));

    var copyField = document.createElement("button");
    copyField.type = "button";
    copyField.className = "ghost-btn";
    copyField.textContent = "Copy DFHMDF";
    copyField.title = "Copy only this field's DFHMDF";
    copyField.addEventListener("click", function () {
      copySource(BMS.Generator.generateField(el), "DFHMDF copied.");
    });
    grid.appendChild(copyField);

    var group = BMS.Elements.findGroup(state.screen, el.groupId);
    if (group.length > 1) {
      var copyGroup = document.createElement("button");
      copyGroup.type = "button";
      copyGroup.className = "ghost-btn";
      copyGroup.textContent = "Copy component";
      copyGroup.title = "Copy every DFHMDF in this header or footer";
      copyGroup.addEventListener("click", function () {
        copySource(BMS.Generator.generateFields(group), "Component DFHMDF copied.");
      });
      grid.appendChild(copyGroup);
    }

    var del = document.createElement("button");
    del.type = "button";
    del.className = "danger-btn";
    del.textContent = "Delete";
    del.addEventListener("click", deleteSelected);
    grid.appendChild(del);

    propertiesBody.appendChild(grid);

    grid.addEventListener("input", function (event) {
      var target = event.target;
      if (!target.name || target.readOnly) {
        return;
      }
      applyProperty(target.name, target.value);
    });

    grid.addEventListener("focusout", function (event) {
      var current = selectedElement();
      var field = event.target.name;
      if (!current || !field) {
        return;
      }
      var live = {
        row: current.row,
        column: current.column,
        length: current.length,
        name: current.name,
        value: current.value,
        color: current.color
      };
      if (live[field] !== undefined) {
        event.target.value = String(live[field]);
      }
    });
  }

  function applyProperty(name, value) {
    var patch = {};
    if (name === "row" || name === "column" || name === "length") {
      if (value === "" || Number.isNaN(Number(value))) {
        showError("Enter a valid number.");
        return;
      }
      patch[name] = Number(value);
    } else {
      patch[name] = value;
    }

    var result = BMS.Elements.updateElement(state.screen, state.selectedId, patch);
    if (!result.ok) {
      showError(result.errors[0]);
      return;
    }

    showError("");
    BMS.Screen.render(state);
    refreshBms();

    if (name === "value") {
      var lengthInput = propertiesBody.querySelector('input[name="length"]');
      var columnInput = propertiesBody.querySelector('input[name="column"]');
      if (lengthInput) {
        lengthInput.value = String(result.element.length);
      }
      if (columnInput) {
        columnInput.value = String(result.element.column);
      }
    }
  }

  function deleteSelected() {
    if (!state.selectedId) {
      return;
    }
    BMS.Elements.deleteElement(state.screen, state.selectedId);
    state.selectedId = null;
    showError("");
    render();
  }

  function setTool(tool) {
    state.tool = tool;
    document.querySelectorAll(".tool-btn").forEach(function (btn) {
      btn.classList.toggle("is-active", btn.getAttribute("data-tool") === tool);
    });
    toolHint.textContent = hints[tool];
  }

  function regionFromDrag(start, end) {
    var finish = end || start;
    var col = Math.min(start.col, finish.col);
    return {
      row: start.row,
      column: col,
      length: Math.abs(finish.col - start.col) + 1
    };
  }

  function showModalError(message) {
    modalError.hidden = !message;
    modalError.textContent = message || "";
  }

  function closeModal() {
    modal.classList.add("is-hidden");
    modalForm.innerHTML = "";
    showModalError("");
    modalForm.onsubmit = null;
  }

  function openDialog(title, fields, onSubmit) {
    modalTitle.textContent = title;
    modalForm.innerHTML = "";
    showModalError("");

    fields.forEach(function (field) {
      var label = document.createElement("label");
      label.textContent = field.label;
      var input;

      if (field.type === "select") {
        input = document.createElement("select");
        input.name = field.name;
        (field.options || []).forEach(function (option) {
          var opt = document.createElement("option");
          opt.value = option.value;
          opt.textContent = option.label;
          if (option.value === field.value) {
            opt.selected = true;
          }
          input.appendChild(opt);
        });
      } else {
        input = document.createElement("input");
        input.name = field.name;
        input.value = field.value || "";
        input.required = !!field.required;
        if (field.type) {
          input.type = field.type;
        }
        if (field.maxlength) {
          input.maxLength = field.maxlength;
        }
        input.spellcheck = false;
      }

      label.appendChild(input);
      modalForm.appendChild(label);
    });

    modalForm.onsubmit = function (event) {
      event.preventDefault();
      var values = {};
      fields.forEach(function (field) {
        values[field.name] = modalForm.elements[field.name].value;
      });
      var error = onSubmit(values);
      if (error) {
        showModalError(error);
        return;
      }
      closeModal();
    };

    modal.classList.remove("is-hidden");
    var first = modalForm.querySelector("input");
    if (first) {
      first.focus();
      first.select();
    }
  }

  function placeText(row, column) {
    openDialog(
      "Static text",
      [
        { name: "value", label: "Text", value: "", required: true },
        colorField("GREEN")
      ],
      function (values) {
        var element = BMS.Elements.buildText(row, column, values.value, {
          color: values.color
        });
        var result = BMS.Elements.addElement(state.screen, element);
        if (!result.ok) {
          return result.errors[0];
        }
        state.selectedId = element.id;
        showError("");
        render();
        return null;
      }
    );
  }

  function placeField(type, row, column, suggestedLength) {
    var title = type === "input" ? "Input field" : "Output field";
    var length = suggestedLength && suggestedLength > 1 ? suggestedLength : 8;
    openDialog(
      title,
      [
        {
          name: "name",
          label: "Name",
          value: BMS.Elements.defaultFieldName(state.screen, type),
          required: true,
          maxlength: 16
        },
        {
          name: "length",
          label: "Length",
          value: String(length),
          required: true,
          type: "number"
        },
        colorField(type === "input" ? "TURQUOISE" : "YELLOW")
      ],
      function (values) {
        var element = BMS.Elements.buildField(
          type,
          row,
          column,
          values.name,
          Number(values.length),
          { color: values.color }
        );
        var result = BMS.Elements.addElement(state.screen, element);
        if (!result.ok) {
          return result.errors[0];
        }
        state.selectedId = element.id;
        showError("");
        render();
        return null;
      }
    );
  }

  function insertComponent(built) {
    if (!built.ok) {
      return built.errors[0];
    }
    var result = BMS.Elements.addMany(state.screen, built.elements);
    if (!result.ok) {
      return result.errors[0];
    }
    state.selectedId = built.elements.length > 1 ? built.elements[1].id : built.elements[0].id;
    setTool("select");
    showError("");
    render();
    return null;
  }

  function ruleField() {
    return {
      name: "rule",
      label: "Delimiter",
      type: "select",
      value: "=",
      options: [
        { value: "=", label: "=======  equals, full width" },
        { value: ".", label: ".......  dots, full width" }
      ]
    };
  }

  function openHeaderDialog() {
    openDialog(
      "Header",
      [
        {
          name: "title",
          label: "Title",
          value: "APPLICATION TITLE",
          required: true,
          maxlength: 80
        },
        {
          name: "subtitle",
          label: "Subtitle",
          value: "MAIN MENU",
          maxlength: 80
        },
        ruleField(),
        colorField("YELLOW")
      ],
      function (values) {
        return insertComponent(BMS.Components.buildHeader(state.screen, values));
      }
    );
  }

  function openFooterDialog() {
    openDialog(
      "Footer",
      [
        {
          name: "options",
          label: "Options (comma separated, one horizontal row)",
          value: "F1=Help, F3=Exit, F7=Bkwd, F8=Fwd, F12=Cancel",
          required: true
        },
        ruleField(),
        colorField("NEUTRAL")
      ],
      function (values) {
        return insertComponent(BMS.Components.buildFooter(state.screen, values));
      }
    );
  }

  function beginCreate(cell) {
    interaction = {
      mode: "create",
      start: cell,
      end: cell
    };
    render({ start: cell, end: cell });
  }

  function beginMove(element, event) {
    var cell = BMS.Screen.cellFromEvent(event);
    if (!cell) {
      return;
    }
    state.selectedId = element.id;
    state.dragging = true;
    interaction = {
      mode: "move",
      id: element.id,
      grabRow: cell.row - element.row,
      grabCol: cell.col - element.column
    };
    render();
    BMS.Screen.setDragging(element.id, true);
  }

  function moveTo(cell) {
    var nextRow = cell.row - interaction.grabRow;
    var nextCol = cell.col - interaction.grabCol;
    var result = BMS.Elements.updateElement(state.screen, interaction.id, {
      row: nextRow,
      column: nextCol
    });

    if (!result.ok) {
      showError(result.errors[0]);
      return;
    }

    showError("");
    BMS.Screen.render(state);
    BMS.Screen.setDragging(interaction.id, true);
    syncPropertyFields();
    refreshBms();
  }

  function finishCreate() {
    var region = regionFromDrag(interaction.start, interaction.end);
    var tool = state.tool;
    interaction = null;
    BMS.Screen.hideRubber();
    render();

    if (tool === "text") {
      placeText(region.row, region.column);
      return;
    }
    if (tool === "input" || tool === "output") {
      placeField(tool, region.row, region.column, region.length);
    }
  }

  BMS.Screen.init(
    {
      stage: document.getElementById("screen-stage"),
      grid: document.getElementById("screen-grid"),
      layer: document.getElementById("element-layer"),
      rubber: document.getElementById("rubber-band"),
      colRuler: document.getElementById("col-ruler"),
      rowRuler: document.getElementById("row-ruler")
    },
    {
      isInteracting: function () {
        return !!interaction;
      },
      onHover: function (cell) {
        if (!cell) {
          cursorPos.textContent = "Row — · Col —";
          return;
        }
        cursorPos.textContent = "Row " + cell.row + " · Col " + cell.col;
      },
      onGridPointerDown: function (cell, event) {
        var hit = BMS.Elements.findAt(state.screen, cell.row, cell.col);
        if (hit) {
          beginMove(hit, event);
          return;
        }

        if (state.tool === "select") {
          state.selectedId = null;
          showError("");
          render();
          return;
        }

        beginCreate(cell);
      },
      onElementPointerDown: function (element, event) {
        beginMove(element, event);
      },
      onPointerMove: function (cell) {
        if (!interaction || !cell) {
          return;
        }
        if (interaction.mode === "create") {
          interaction.end = { row: interaction.start.row, col: cell.col };
          BMS.Screen.render(state, { start: interaction.start, end: interaction.end });
          return;
        }
        if (interaction.mode === "move") {
          moveTo(cell);
        }
      },
      onPointerUp: function () {
        if (!interaction) {
          return;
        }
        if (interaction.mode === "create") {
          finishCreate();
          return;
        }
        state.dragging = false;
        interaction = null;
        render();
      }
    }
  );

  document.querySelectorAll(".tool-btn[data-tool]").forEach(function (btn) {
    btn.addEventListener("click", function () {
      setTool(btn.getAttribute("data-tool"));
    });
  });

  document.getElementById("btn-comp-header").addEventListener("click", function () {
    setTool("select");
    openHeaderDialog();
  });

  document.getElementById("btn-comp-footer").addEventListener("click", function () {
    setTool("select");
    openFooterDialog();
  });

  function bindHeaderField(id, key) {
    document.getElementById(id).addEventListener("input", function (event) {
      state.screen[key] = BMS.Elements.normalizeName(event.target.value);
      var errors = BMS.Validation.validateHeader(state.screen);
      showError(errors.length ? errors[0] : "");
      refreshBms();
    });
  }

  bindHeaderField("cfg-mapset", "mapset");
  bindHeaderField("cfg-map-name", "mapName");
  bindHeaderField("cfg-screen-name", "screenName");

  document.querySelectorAll("[data-modal-cancel]").forEach(function (node) {
    node.addEventListener("click", closeModal);
  });

  document.addEventListener("keydown", function (event) {
    if (event.key === "Escape") {
      if (!modal.classList.contains("is-hidden")) {
        closeModal();
        return;
      }
      if (interaction) {
        interaction = null;
        state.dragging = false;
        render();
      }
      return;
    }

    if (
      (event.key === "Delete" || event.key === "Backspace") &&
      state.selectedId &&
      modal.classList.contains("is-hidden") &&
      document.activeElement &&
      document.activeElement.tagName !== "INPUT"
    ) {
      event.preventDefault();
      deleteSelected();
    }
  });

  copyBtn.addEventListener("click", function () {
    copySource(lastBms, "BMS source copied.");
  });

  document.getElementById("btn-copy-fields").addEventListener("click", function () {
    if (!state.screen.elements.length) {
      showError("No DFHMDF fields to copy.");
      return;
    }
    copySource(
      BMS.Generator.generateFields(state.screen.elements),
      "All DFHMDF fields copied."
    );
  });

  downloadBtn.addEventListener("click", function () {
    if (!lastBms) {
      showError("Nothing to download.");
      return;
    }
    var name = (state.screen.mapset || "MAPSET").toUpperCase() + ".bms";
    var blob = new Blob([lastBms], { type: "text/plain" });
    var url = URL.createObjectURL(blob);
    var link = document.createElement("a");
    link.href = url;
    link.download = name;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
    showOk("Downloaded " + name + ".");
    window.setTimeout(function () {
      if (validationMsg.classList.contains("is-ok")) {
        validationMsg.textContent = "";
        validationMsg.classList.remove("is-ok");
      }
    }, 1600);
  });

  setTool("select");
  render();
})();
