/**
 * Application wiring: tools, dialogs, properties, and live BMS preview.
 */
(function () {
  var state = {
    screen: BMS.Elements.createScreen(),
    selectedId: null,
    tool: "select",
    dragging: false,
    previewElements: null,
    projectId: null,
    folderId: null
  };

  var interaction = null;
  var lastBms = "";
  var persistTimer = null;
  var persistQuiet = false;
  var lastSavedAt = 0;

  var cursorPos = document.getElementById("cursor-pos");
  var toolHint = document.getElementById("tool-hint");
  var propertiesBody = document.getElementById("properties-body");
  var validationMsg = document.getElementById("validation-msg");
  var bmsSource = document.getElementById("bms-source");
  var bmsLines = document.getElementById("bms-lines");
  var copyBtn = document.getElementById("btn-copy");
  var downloadBtn = document.getElementById("btn-download");
  var bmsScroll = document.querySelector(".bms-scroll");
  var pageSizeInput = document.getElementById("bms-page-size");
  var pageMeta = document.getElementById("bms-page-meta");
  var pagePrevBtn = document.getElementById("btn-page-prev");
  var pageNextBtn = document.getElementById("btn-page-next");
  var copyPageBtn = document.getElementById("btn-copy-page");
  var pageIndex = 0;
  var PAGE_SIZE_KEY = "bms-cics-generator:page-size";
  var modal = document.getElementById("modal");
  var modalTitle = document.getElementById("modal-title");
  var modalError = document.getElementById("modal-error");
  var modalForm = document.getElementById("modal-form");
  var modalSubmit = document.getElementById("modal-submit");
  var mapsList = document.getElementById("maps-list");
  var mapsPath = document.getElementById("maps-path");
  var saveStatus = document.getElementById("save-status");

  var hints = {
    select: "Select an element to move it, choose Text / Input / Output, or add a Header / Footer component.",
    text: "Click a start cell, then type. The text appears on the 3270 screen as you type.",
    input: "Click or drag to size the field. Length and color update live on the screen.",
    output: "Click or drag to size the field. Length and color update live on the screen."
  };

  function showError(message) {
    validationMsg.textContent = message || "";
    validationMsg.classList.toggle("is-ok", false);
  }

  function showOk(message) {
    validationMsg.textContent = message || "";
    validationMsg.classList.toggle("is-ok", true);
  }

  function formatSavedAt(ts) {
    if (!ts) {
      return "Not saved";
    }
    var delta = Date.now() - ts;
    if (delta < 8000) {
      return "Saved just now";
    }
    if (delta < 60000) {
      return "Saved " + Math.floor(delta / 1000) + "s ago";
    }
    if (delta < 3600000) {
      return "Saved " + Math.floor(delta / 60000) + "m ago";
    }
    return "Saved " + new Date(ts).toLocaleString();
  }

  function updateSaveStatus() {
    saveStatus.textContent = formatSavedAt(lastSavedAt);
  }

  function persistNow(options) {
    if (persistQuiet) {
      return { ok: true, skipped: true };
    }
    options = options || {};
    var extras = {
      selectedId: state.selectedId
    };
    if (options.name != null) {
      extras.name = options.name;
    }
    if (options.folderId != null) {
      extras.folderId = options.folderId;
    } else if (!state.projectId && state.folderId) {
      extras.folderId = state.folderId;
    }

    var result;
    if (!state.projectId) {
      extras.name = extras.name || BMS.Storage.defaultName(state.screen);
      extras.folderId = extras.folderId || currentFolderId();
      result = BMS.Storage.create(state.screen, extras);
    } else {
      result = BMS.Storage.update(state.projectId, state.screen, extras);
    }

    if (!result.ok) {
      showError(result.error);
      return result;
    }

    state.projectId = result.map.id;
    if (result.map.folderId) {
      state.folderId = result.map.folderId;
    }
    lastSavedAt = result.map.updatedAt;
    renderMapsList();
    return result;
  }

  function cancelPersist() {
    window.clearTimeout(persistTimer);
    persistTimer = null;
  }

  function schedulePersist() {
    if (persistQuiet) {
      return;
    }
    cancelPersist();
    persistTimer = window.setTimeout(function () {
      persistNow();
    }, 350);
  }

  function syncConfigFields() {
    document.getElementById("cfg-mapset").value = state.screen.mapset || "";
    document.getElementById("cfg-map-name").value = state.screen.mapName || "";
    document.getElementById("cfg-screen-name").value = state.screen.screenName || "";
  }

  function applyMap(map) {
    cancelPersist();
    persistQuiet = true;
    state.projectId = map.id;
    state.screen = BMS.Elements.importScreen(map.screen);
    state.selectedId =
      map.selectedId && BMS.Elements.findById(state.screen, map.selectedId)
        ? map.selectedId
        : null;
    state.previewElements = null;
    lastSavedAt = map.updatedAt || Date.now();
    state.folderId = map.folderId || BMS.Storage.defaultFolderId();
    syncConfigFields();
    persistQuiet = false;
  }

  function loadMap(id) {
    if (id === state.projectId) {
      return;
    }
    persistNow();
    var map = BMS.Storage.get(id);
    if (!map) {
      showError("Save not found.");
      renderMapsList();
      return;
    }
    BMS.Storage.setActive(id);
    applyMap(map);
    showError("");
    render();
  }

  function currentFolderId() {
    if (state.folderId && BMS.Storage.getFolder(state.folderId)) {
      return state.folderId;
    }
    return BMS.Storage.defaultFolderId();
  }

  function projectIdForFolder(folderId) {
    var folder = BMS.Storage.getFolder(folderId);
    if (!folder) {
      return BMS.Storage.defaultFolderId();
    }
    return folder.parentId || folder.id;
  }

  function selectFolder(id) {
    if (!BMS.Storage.getFolder(id)) {
      return;
    }
    state.folderId = id;
    renderMapsList();
  }

  function folderSelectField(selectedId) {
    return {
      name: "folderId",
      label: "Project / folder",
      type: "select",
      value: selectedId || currentFolderId(),
      options: BMS.Storage.folderOptions().map(function (folder) {
        return { value: folder.id, label: folder.label };
      })
    };
  }

  function duplicateMap(id) {
    persistNow();
    var sourceId = id || state.projectId;
    if (!sourceId) {
      showError("Nothing to duplicate.");
      return;
    }
    var result = BMS.Storage.duplicate(sourceId);
    if (!result.ok) {
      showError(result.error);
      return;
    }
    applyMap(result.map);
    showOk("Copied as " + result.map.name + ".");
    render();
  }

  function openImportDialog() {
    openDialog(
      "Import BMS source",
      [
        {
          name: "source",
          label: "Paste DFHMSD / DFHMDI / DFHMDF source",
          type: "textarea",
          value: "",
          required: true
        }
      ],
      function (values) {
        var parsed = BMS.Parser.parse(values.source);
        if (!parsed.ok) {
          return parsed.error;
        }
        persistNow();
        var created = BMS.Storage.create(parsed.screen, {
          name: BMS.Storage.nextCopyName(
            currentFolderId(),
            parsed.name || "MP"
          ),
          folderId: currentFolderId()
        });
        if (!created.ok) {
          return created.error;
        }
        applyMap(created.map);
        showOk(
          "Imported " +
            created.map.name +
            " · " +
            parsed.screen.elements.length +
            " fields."
        );
        return null;
      },
      null,
      "Import"
    );
  }

  function newMap() {
    persistNow();
    var created = BMS.Storage.create(BMS.Elements.createScreen(), {
      name: "Untitled",
      folderId: currentFolderId()
    });
    if (!created.ok) {
      showError(created.error);
      return;
    }
    applyMap(created.map);
    showOk("New map in " + (BMS.Storage.folderPath(created.map.folderId) || "Maps") + ".");
    render();
  }

  function deleteMap(id, name) {
    if (!window.confirm('Delete save "' + name + '"?')) {
      return;
    }
    cancelPersist();
    var wasActive = id === state.projectId;
    if (wasActive) {
      persistQuiet = true;
    }
    var result = BMS.Storage.remove(id);
    persistQuiet = false;
    if (!result.ok) {
      showError(result.error);
      return;
    }
    if (wasActive) {
      if (result.activeId) {
        applyMap(BMS.Storage.get(result.activeId));
      } else {
        var created = BMS.Storage.create(BMS.Elements.createScreen(), {
          name: "Untitled",
          folderId: currentFolderId()
        });
        if (!created.ok) {
          showError(created.error);
          return;
        }
        applyMap(created.map);
      }
      render();
      return;
    }
    renderMapsList();
  }

  function openSaveDialog(asCopy) {
    var current = state.projectId ? BMS.Storage.get(state.projectId) : null;
    var suggested =
      current && current.name
        ? asCopy
          ? current.name + " copy"
          : current.name
        : BMS.Storage.defaultName(state.screen);
    var folderId = (current && current.folderId) || currentFolderId();

    openDialog(
      asCopy ? "Save as" : "Save map",
      [
        {
          name: "saveName",
          label: "Save name",
          value: suggested,
          required: true,
          maxlength: 40
        },
        folderSelectField(folderId)
      ],
      function (values) {
        var name = String(values.saveName || "").trim();
        if (!name) {
          return "Name is required.";
        }
        var dest = values.folderId || folderId;
        var result;
        if (asCopy) {
          result = BMS.Storage.create(state.screen, {
            name: name,
            folderId: dest,
            selectedId: state.selectedId
          });
          if (!result.ok) {
            return result.error;
          }
          applyMap(result.map);
        } else {
          result = persistNow({ name: name, folderId: dest });
          if (!result.ok) {
            return result.error;
          }
        }
        showOk("Saved " + name + ".");
        return null;
      },
      null,
      asCopy ? "Save as" : "Save"
    );
  }

  function openProjectDialog() {
    openDialog(
      "New project",
      [
        {
          name: "folderName",
          label: "Project name",
          value: "",
          required: true,
          maxlength: 40
        }
      ],
      function (values) {
        var result = BMS.Storage.createFolder(values.folderName, null);
        if (!result.ok) {
          return result.error;
        }
        state.folderId = result.folder.id;
        showOk("Project " + result.folder.name + " created.");
        return null;
      },
      null,
      "Create"
    );
  }

  function openFolderDialog() {
    var projects = BMS.Storage.roots();
    if (!projects.length) {
      showError("Create a project first.");
      return;
    }
    var parentId = projectIdForFolder(currentFolderId());
    openDialog(
      "New folder",
      [
        {
          name: "folderName",
          label: "Folder name",
          value: "",
          required: true,
          maxlength: 40
        },
        {
          name: "parentId",
          label: "Project",
          type: "select",
          value: parentId,
          options: projects.map(function (project) {
            return { value: project.id, label: project.name };
          })
        }
      ],
      function (values) {
        var result = BMS.Storage.createFolder(values.folderName, values.parentId);
        if (!result.ok) {
          return result.error;
        }
        state.folderId = result.folder.id;
        showOk("Folder " + result.folder.name + " created.");
        return null;
      },
      null,
      "Create"
    );
  }

  function openRenameFolderDialog(id) {
    var folder = BMS.Storage.getFolder(id);
    if (!folder) {
      return;
    }
    openDialog(
      folder.parentId ? "Rename folder" : "Rename project",
      [
        {
          name: "folderName",
          label: folder.parentId ? "Folder name" : "Project name",
          value: folder.name,
          required: true,
          maxlength: 40
        }
      ],
      function (values) {
        var result = BMS.Storage.renameFolder(id, values.folderName);
        if (!result.ok) {
          return result.error;
        }
        showOk("Renamed to " + result.folder.name + ".");
        return null;
      },
      null,
      "Rename"
    );
  }

  function deleteFolder(id) {
    var folder = BMS.Storage.getFolder(id);
    if (!folder) {
      return;
    }
    var kind = folder.parentId ? "folder" : "project";
    if (!window.confirm('Delete ' + kind + ' "' + folder.name + '" and every map inside it?')) {
      return;
    }
    cancelPersist();
    var wasInside =
      state.folderId === id ||
      (state.projectId &&
        BMS.Storage.get(state.projectId) &&
        descendantContainsMap(id, state.projectId));
    persistQuiet = true;
    var result = BMS.Storage.removeFolder(id);
    persistQuiet = false;
    if (!result.ok) {
      showError(result.error);
      return;
    }
    if (result.activeId) {
      applyMap(BMS.Storage.get(result.activeId));
    } else if (wasInside || !state.projectId) {
      var created = BMS.Storage.create(BMS.Elements.createScreen(), {
        name: "Untitled",
        folderId: BMS.Storage.defaultFolderId()
      });
      if (!created.ok) {
        showError(created.error);
        return;
      }
      applyMap(created.map);
    } else {
      state.folderId = currentFolderId();
    }
    render();
  }

  function descendantContainsMap(folderId, mapId) {
    var map = BMS.Storage.get(mapId);
    if (!map) {
      return false;
    }
    var current = BMS.Storage.getFolder(map.folderId);
    var guard = 0;
    while (current && guard < 8) {
      if (current.id === folderId) {
        return true;
      }
      current = current.parentId ? BMS.Storage.getFolder(current.parentId) : null;
      guard += 1;
    }
    return false;
  }

  function clearTreeDropTargets() {
    if (!mapsList) {
      return;
    }
    var nodes = mapsList.querySelectorAll(".is-drop-target");
    var i;
    for (i = 0; i < nodes.length; i += 1) {
      nodes[i].classList.remove("is-drop-target");
    }
  }

  function moveMapToFolder(mapId, folderId) {
    var map = BMS.Storage.get(mapId);
    var folder = BMS.Storage.getFolder(folderId);
    if (!map || !folder) {
      return;
    }
    if (map.folderId === folderId) {
      return;
    }
    cancelPersist();
    var result = BMS.Storage.moveMap(mapId, folderId);
    if (!result.ok) {
      showError(result.error);
      return;
    }
    if (folder.collapsed) {
      BMS.Storage.toggleCollapsed(folder.id);
    }
    state.folderId = folderId;
    showOk("Moved to " + BMS.Storage.folderPath(folderId) + ".");
    renderMapsList();
  }

  function bindFolderDrop(item, folderId) {
    item.addEventListener("dragover", function (event) {
      if (!event.dataTransfer) {
        return;
      }
      event.preventDefault();
      event.dataTransfer.dropEffect = "move";
      clearTreeDropTargets();
      item.classList.add("is-drop-target");
    });
    item.addEventListener("dragleave", function (event) {
      if (event.relatedTarget && item.contains(event.relatedTarget)) {
        return;
      }
      item.classList.remove("is-drop-target");
    });
    item.addEventListener("drop", function (event) {
      event.preventDefault();
      event.stopPropagation();
      item.classList.remove("is-drop-target");
      var mapId = event.dataTransfer.getData("text/plain");
      moveMapToFolder(mapId, folderId);
    });
  }

  function treeAction(label, title, onClick) {
    var button = document.createElement("button");
    button.type = "button";
    button.className = "ghost-btn";
    button.textContent = label;
    button.title = title;
    button.addEventListener("click", function (event) {
      event.stopPropagation();
      onClick();
    });
    return button;
  }

  function appendMapRow(container, map, depth) {
    var item = document.createElement("div");
    item.className = "tree-row tree-map" + (map.id === state.projectId ? " is-active" : "");
    item.style.paddingLeft = 8 + depth * 14 + "px";
    item.title = "Updated " + new Date(map.updatedAt).toLocaleString();

    var spacer = document.createElement("span");
    spacer.className = "tree-toggle";
    spacer.setAttribute("aria-hidden", "true");

    var title = document.createElement("span");
    title.className = "tree-name";
    title.textContent = map.name || BMS.Storage.defaultName(map.screen);

    var count = ((map.screen && map.screen.elements) || []).length;
    var meta = document.createElement("span");
    meta.className = "tree-meta";
    meta.textContent = String(count);

    var actions = document.createElement("div");
    actions.className = "map-item-actions";
    actions.appendChild(
      treeAction("Dup", "Duplicate map in this folder", function () {
        duplicateMap(map.id);
      })
    );
    actions.appendChild(
      treeAction("Del", "Delete map", function () {
        deleteMap(map.id, map.name || BMS.Storage.defaultName(map.screen));
      })
    );

    item.appendChild(spacer);
    item.appendChild(title);
    item.appendChild(meta);
    item.appendChild(actions);
    item.draggable = true;
    item.addEventListener("dragstart", function (event) {
      var from = event.target.nodeType === 1 ? event.target : event.target.parentElement;
      if (from && from.closest && from.closest("button")) {
        event.preventDefault();
        return;
      }
      event.dataTransfer.setData("text/plain", map.id);
      event.dataTransfer.effectAllowed = "move";
      item.classList.add("is-dragging");
    });
    item.addEventListener("dragend", function () {
      item.classList.remove("is-dragging");
      item.dataset.justDragged = "1";
      clearTreeDropTargets();
    });
    bindFolderDrop(item, map.folderId);
    item.addEventListener("click", function () {
      if (item.dataset.justDragged === "1") {
        delete item.dataset.justDragged;
        return;
      }
      loadMap(map.id);
    });
    container.appendChild(item);
  }

  function appendFolderRow(container, folder, depth) {
    var kids = BMS.Storage.children(folder.id);
    var item = document.createElement("div");
    item.className =
      "tree-row tree-folder" + (folder.id === state.folderId ? " is-selected" : "");
    item.style.paddingLeft = 8 + depth * 14 + "px";
    item.title = folder.parentId ? "Folder" : "Project";

    var toggle = document.createElement("button");
    toggle.type = "button";
    toggle.className = "tree-toggle";
    toggle.textContent = folder.collapsed ? "▸" : "▾";
    toggle.title = folder.collapsed ? "Expand" : "Collapse";
    toggle.addEventListener("click", function (event) {
      event.stopPropagation();
      BMS.Storage.toggleCollapsed(folder.id);
      renderMapsList();
    });

    var title = document.createElement("span");
    title.className = "tree-name";
    title.textContent = folder.name;

    var actions = document.createElement("div");
    actions.className = "map-item-actions";
    actions.appendChild(
      treeAction("Ren", folder.parentId ? "Rename folder" : "Rename project", function () {
        openRenameFolderDialog(folder.id);
      })
    );
    actions.appendChild(
      treeAction("Del", folder.parentId ? "Delete folder" : "Delete project", function () {
        deleteFolder(folder.id);
      })
    );

    item.appendChild(toggle);
    item.appendChild(title);
    item.appendChild(actions);
    bindFolderDrop(item, folder.id);
    item.addEventListener("click", function () {
      selectFolder(folder.id);
    });
    container.appendChild(item);

    if (folder.collapsed) {
      return;
    }
    kids.folders.forEach(function (child) {
      appendFolderRow(container, child, depth + 1);
    });
    kids.maps.forEach(function (map) {
      appendMapRow(container, map, depth + 1);
    });
  }

  function renderMapsList() {
    var projects = BMS.Storage.roots();
    mapsList.innerHTML = "";

    if (mapsPath) {
      mapsPath.textContent =
        (BMS.Storage.folderPath(currentFolderId()) || "Select a project or folder") +
        ". Drag a map onto a folder to move it.";
    }

    if (!projects.length) {
      var empty = document.createElement("p");
      empty.className = "maps-empty";
      empty.textContent = "No projects yet. Create one to store maps.";
      mapsList.appendChild(empty);
      updateSaveStatus();
      return;
    }

    projects.forEach(function (project) {
      appendFolderRow(mapsList, project, 0);
    });

    updateSaveStatus();
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
    return splitSourceLines(text).length;
  }

  function splitSourceLines(text) {
    var lines = String(text || "").split("\n");
    if (lines.length && lines[lines.length - 1] === "") {
      lines.pop();
    }
    return lines;
  }

  function currentPageSize() {
    var size = parseInt(pageSizeInput && pageSizeInput.value, 10);
    if (!Number.isInteger(size) || size < 1) {
      size = 20;
    }
    if (size > 200) {
      size = 200;
    }
    return size;
  }

  function pageBounds(text) {
    var lines = splitSourceLines(text);
    var size = currentPageSize();
    var pages = Math.max(1, Math.ceil(lines.length / size) || 1);
    if (pageIndex > pages - 1) {
      pageIndex = pages - 1;
    }
    if (pageIndex < 0) {
      pageIndex = 0;
    }
    var start = lines.length ? pageIndex * size : 0;
    var end = Math.min(start + size, lines.length);
    return {
      lines: lines,
      size: size,
      pages: pages,
      start: start,
      end: end
    };
  }

  function renderSourceLines(text) {
    var lines = splitSourceLines(text);
    var width = String(lines.length || 1).length;
    var i;
    var label;
    var num;
    var src;

    bmsLines.innerHTML = "";
    bmsSource.innerHTML = "";

    for (i = 0; i < lines.length; i += 1) {
      label = String(i + 1);
      while (label.length < width) {
        label = " " + label;
      }
      num = document.createElement("div");
      num.className = "bms-src-line";
      num.textContent = label;
      bmsLines.appendChild(num);

      src = document.createElement("div");
      src.className = "bms-src-line";
      src.textContent = lines[i] === "" ? " " : lines[i];
      bmsSource.appendChild(src);
    }
  }

  function scrollToPage(start) {
    if (!bmsScroll || !bmsSource.children[start]) {
      return;
    }
    var first = bmsSource.children[start];
    var scrollRect = bmsScroll.getBoundingClientRect();
    var lineRect = first.getBoundingClientRect();
    var ruler = bmsScroll.querySelector(".bms-ruler");
    var offset = ruler ? ruler.offsetHeight + 10 : 8;
    bmsScroll.scrollTop += lineRect.top - scrollRect.top - offset;
  }

  function updateSourcePage(scroll) {
    var bounds = pageBounds(lastBms);
    var i;

    for (i = 0; i < bmsSource.children.length; i += 1) {
      var on = i >= bounds.start && i < bounds.end;
      bmsSource.children[i].classList.toggle("is-page", on);
      if (bmsLines.children[i]) {
        bmsLines.children[i].classList.toggle("is-page", on);
      }
    }

    if (pageMeta) {
      pageMeta.textContent = bounds.lines.length
        ? "Page " +
          (pageIndex + 1) +
          "/" +
          bounds.pages +
          " · " +
          (bounds.start + 1) +
          "–" +
          bounds.end
        : "No source";
    }
    if (pagePrevBtn) {
      pagePrevBtn.disabled = pageIndex <= 0;
    }
    if (pageNextBtn) {
      pageNextBtn.disabled = pageIndex >= bounds.pages - 1 || !bounds.lines.length;
    }

    if (scroll && bounds.lines.length) {
      scrollToPage(bounds.start);
    }
  }

  function goToPage(nextIndex, scroll) {
    var bounds = pageBounds(lastBms);
    pageIndex = Math.max(0, Math.min(nextIndex, bounds.pages - 1));
    updateSourcePage(scroll !== false);
  }

  function copyCurrentPage(advance) {
    var bounds = pageBounds(lastBms);
    if (!bounds.lines.length) {
      showError("Nothing to copy.");
      return;
    }
    var chunk = bounds.lines.slice(bounds.start, bounds.end).join("\n") + "\n";
    copySource(
      chunk,
      "Copied lines " + (bounds.start + 1) + "–" + bounds.end + "."
    );
    if (advance && pageIndex < bounds.pages - 1) {
      goToPage(pageIndex + 1, true);
    } else {
      updateSourcePage(true);
    }
  }

  function refreshBms() {
    try {
      lastBms = BMS.Generator.generate(state.screen);
      renderSourceLines(lastBms);
      updateSourcePage(false);
      var sourceErrors = BMS.Validation.validateSource(lastBms);
      if (sourceErrors.length) {
        showError(sourceErrors[0]);
      }
      schedulePersist();
    } catch (err) {
      lastBms = "";
      renderSourceLines("* BMS formatting error:\n" + err.message);
      updateSourcePage(false);
      showError(err.message);
    }
  }

  function render(draft) {
    BMS.Screen.render(state, draft);
    renderProperties();
    refreshBms();
    renderMapsList();
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

  function alignButtons(current) {
    var wrap = document.createElement("div");
    wrap.className = "align-group";
    [
      { id: "left", label: "Left" },
      { id: "center", label: "Center" },
      { id: "right", label: "Right" }
    ].forEach(function (item) {
      var btn = document.createElement("button");
      btn.type = "button";
      btn.className = "tool-btn" + (current === item.id ? " is-active" : "");
      btn.textContent = item.label;
      btn.addEventListener("click", function () {
        applyProperty("align", item.id);
        renderProperties();
      });
      wrap.appendChild(btn);
    });
    return wrap;
  }

  function alignField(value) {
    return {
      name: "align",
      label: "Align",
      type: "select",
      value: value || "left",
      options: [
        { value: "left", label: "Left" },
        { value: "center", label: "Center" },
        { value: "right", label: "Right" }
      ]
    };
  }

  function dataTypeField(numeric) {
    return {
      name: "numeric",
      label: "Data type",
      type: "select",
      value: numeric ? "NUM" : "CHAR",
      options: [
        { value: "CHAR", label: "Character" },
        { value: "NUM", label: "Numeric" }
      ]
    };
  }

  function dataTypeSelect(numeric) {
    var select = document.createElement("select");
    select.name = "numeric";
    [
      { value: "CHAR", label: "Character" },
      { value: "NUM", label: "Numeric" }
    ].forEach(function (option) {
      var opt = document.createElement("option");
      opt.value = option.value;
      opt.textContent = option.label;
      if (option.value === (numeric ? "NUM" : "CHAR")) {
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

  function fieldTypeSelect(type) {
    var select = document.createElement("select");
    select.name = "type";
    [
      { value: "input", label: "Input" },
      { value: "output", label: "Output" }
    ].forEach(function (option) {
      var opt = document.createElement("option");
      opt.value = option.value;
      opt.textContent = option.label;
      if (option.value === type) {
        opt.selected = true;
      }
      select.appendChild(opt);
    });
    return select;
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

    if (el.type === "input" || el.type === "output") {
      grid.appendChild(labeled("Type", fieldTypeSelect(el.type)));
    } else {
      var type = document.createElement("div");
      type.className = "prop-field";
      type.innerHTML = "Type<span class=\"prop-type\">" + typeLabel(el.type) + "</span>";
      grid.appendChild(type);
    }

    if (el.type !== "text") {
      var nameInput = inputEl("name", el.name, "wide");
      nameInput.maxLength = 7;
      nameInput.pattern = "[A-Za-z][A-Za-z0-9]{0,6}";
      nameInput.title = "BMS label in source columns 1-7";
      nameInput.classList.add("field-name-input");
      grid.appendChild(labeled("Name (1–7)", nameInput));
    }

    var row = inputEl("row", String(el.row), "narrow");
    row.type = "number";
    row.min = "1";
    row.max = String(state.screen.rows);
    grid.appendChild(labeled("Row", row));

    var col = inputEl("column", String(el.column), "narrow");
    col.type = "number";
    col.min = "1";
    col.max = String(state.screen.columns - 1);
    grid.appendChild(labeled("POS", col));

    var dataAt = document.createElement("div");
    dataAt.className = "prop-field";
    dataAt.id = "prop-data-at";
    dataAt.innerHTML =
      "Data at<span class=\"prop-type\">Col " +
      (el.column + 1) +
      "–" +
      (el.column + el.length) +
      "</span>";
    grid.appendChild(dataAt);

    var length = inputEl("length", String(el.length), "narrow");
    length.type = "number";
    length.min = "1";
    length.max = String(BMS.Validation.maxDataLength(el.column, state.screen.columns));
    if (el.type === "text") {
      length.readOnly = true;
      length.title = "Length is taken from the text.";
    }
    grid.appendChild(labeled("Length", length));

    if (el.type === "text") {
      grid.appendChild(labeled("Value", inputEl("value", el.value, "wide")));
    }

    if (el.type === "input" || el.type === "output") {
      if (el.type === "input") {
        grid.appendChild(labeled("Data type", dataTypeSelect(el.numeric)));
      }
      var attrb = document.createElement("div");
      attrb.className = "prop-field";
      attrb.id = "prop-attrb";
      attrb.innerHTML =
        "ATTRB<span class=\"prop-type\">" +
        BMS.Generator.attrbFor(el, state.screen) +
        "</span>";
      grid.appendChild(attrb);
    }

    grid.appendChild(labeled("Color", colorSelect(el.color)));
    grid.appendChild(labeled("Align", alignButtons(el.align)));

    var copyField = document.createElement("button");
    copyField.type = "button";
    copyField.className = "ghost-btn";
    copyField.textContent = "Copy DFHMDF";
    copyField.title = "Copy only this field's DFHMDF";
    copyField.addEventListener("click", function () {
      copySource(BMS.Generator.generateField(el, state.screen), "DFHMDF copied.");
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
        copySource(BMS.Generator.generateFields(group, state.screen), "Component DFHMDF copied.");
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
      if (!target.name || target.readOnly || target.tagName === "SELECT") {
        return;
      }
      applyProperty(target.name, target.value);
    });

    grid.addEventListener("change", function (event) {
      var target = event.target;
      if (!target.name || target.tagName !== "SELECT") {
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
        color: current.color,
        numeric: current.numeric ? "NUM" : "CHAR",
        type: current.type
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
    if (name === "type") {
      render();
      return;
    }
    BMS.Screen.render(state);
    refreshBms();

    var attrb = document.getElementById("prop-attrb");
    if (attrb) {
      attrb.innerHTML =
        "ATTRB<span class=\"prop-type\">" +
        BMS.Generator.attrbFor(result.element, state.screen) +
        "</span>";
    }

    if (name === "value" || name === "column" || name === "length" || name === "align") {
      var lengthInput = propertiesBody.querySelector('input[name="length"]');
      var columnInput = propertiesBody.querySelector('input[name="column"]');
      var dataAt = document.getElementById("prop-data-at");
      if (lengthInput) {
        lengthInput.value = String(result.element.length);
      }
      if (columnInput) {
        columnInput.value = String(result.element.column);
      }
      if (dataAt) {
        dataAt.innerHTML =
          "Data at<span class=\"prop-type\">Col " +
          (result.element.column + 1) +
          "–" +
          (result.element.column + result.element.length) +
          "</span>";
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
    var dataCol = Math.min(start.col, finish.col);
    var lastCol = Math.max(start.col, finish.col);
    var pos = BMS.Elements.posFromDataColumn(dataCol);
    var firstData = pos + 1;
    var length = lastCol - firstData + 1;

    if (length < 1) {
      length = 1;
    }

    return {
      row: start.row,
      column: pos,
      length: length
    };
  }

  function showModalError(message) {
    modalError.hidden = !message;
    modalError.textContent = message || "";
  }

  function fillDots(count) {
    var out = "";
    var i;
    for (i = 0; i < count; i += 1) {
      out += "·";
    }
    return out || "·";
  }

  function updatePlacementHint(region, label) {
    if (!region) {
      toolHint.textContent = hints[state.tool];
      return;
    }
    toolHint.textContent =
      "Placing " +
      (label || state.tool) +
      " · POS=(" +
      region.row +
      "," +
      region.column +
      ") · data columns " +
      (region.column + 1) +
      "–" +
      (region.column + region.length);
  }

  function showScreenPreview(elements, hintRegion, hintLabel) {
    state.previewElements = elements && elements.length ? elements : null;
    updatePlacementHint(hintRegion, hintLabel);
    BMS.Screen.render(state);
  }

  function clearPreview() {
    state.previewElements = null;
    toolHint.textContent = hints[state.tool];
  }

  function ghostField(type, row, column, length, extras) {
    extras = extras || {};
    var color = extras.color || (type === "input" ? "TURQUOISE" : type === "output" ? "YELLOW" : "GREEN");
    var align = BMS.Elements.normalizeAlign(extras.align);
    var value = extras.value;
    var fieldLength;
    var pos = column;

    if (type === "text") {
      if (!value) {
        value = fillDots(Math.max(1, length || 1));
      }
      fieldLength = value.length;
      if (align) {
        pos = BMS.Elements.alignedColumn(align, fieldLength, state.screen.columns);
      }
      return {
        id: "preview-text",
        type: "text",
        row: row,
        column: pos,
        length: fieldLength,
        value: value,
        color: color,
        align: align || undefined
      };
    }

    fieldLength = Math.max(1, Number(length) || 1);
    if (align) {
      pos = BMS.Elements.alignedColumn(align, fieldLength, state.screen.columns);
    }
    return {
      id: "preview-" + type,
      type: type,
      row: row,
      column: pos,
      length: fieldLength,
      name: BMS.Elements.normalizeFieldName(extras.name || ""),
      color: color,
      numeric: type === "input" ? BMS.Elements.normalizeNumeric(extras.numeric) : undefined,
      align: align || undefined
    };
  }

  function previewCreateRegion(start, end) {
    var region = regionFromDrag(start, end);
    var extras = {};
    if (state.tool === "input" || state.tool === "output") {
      extras.name = BMS.Elements.defaultFieldName(state.screen, state.tool);
    }
    showScreenPreview(
      [ghostField(state.tool, region.row, region.column, region.length, extras)],
      region,
      state.tool
    );
    return region;
  }

  function readDialogValues(fields) {
    var values = {};
    fields.forEach(function (field) {
      if (modalForm.elements[field.name]) {
        values[field.name] = modalForm.elements[field.name].value;
      }
    });
    return values;
  }

  function closeModal() {
    modal.classList.add("is-hidden");
    modalForm.innerHTML = "";
    showModalError("");
    modalForm.onsubmit = null;
    modalForm.oninput = null;
    modalForm.onchange = null;
    clearPreview();
    BMS.Screen.render(state);
  }

  function openDialog(title, fields, onSubmit, onChange, submitLabel) {
    modalTitle.textContent = title;
    modalForm.innerHTML = "";
    showModalError("");
    if (modalSubmit) {
      modalSubmit.textContent = submitLabel || "Place";
    }

    fields.forEach(function (field) {
      var label = document.createElement("label");
      label.textContent = field.label;
      var input;

      if (field.type === "textarea") {
        input = document.createElement("textarea");
        input.name = field.name;
        input.value = field.value || "";
        input.required = !!field.required;
        input.spellcheck = false;
        input.rows = field.rows || 12;
      } else if (field.type === "select") {
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
        if (field.pattern) {
          input.pattern = field.pattern;
        }
        if (field.name === "name") {
          input.classList.add("field-name-input");
        }
        input.spellcheck = false;
      }

      label.appendChild(input);
      modalForm.appendChild(label);
    });

    function emitChange() {
      if (onChange) {
        onChange(readDialogValues(fields));
      }
    }

    modalForm.oninput = emitChange;
    modalForm.onchange = emitChange;

    modalForm.onsubmit = function (event) {
      event.preventDefault();
      var error = onSubmit(readDialogValues(fields));
      if (error) {
        showModalError(error);
        return;
      }
      closeModal();
      render();
    };

    modal.classList.remove("is-hidden");
    emitChange();
    var first = modalForm.querySelector("input, textarea, select");
    if (first) {
      first.focus();
      first.select();
    }
  }

  function placeText(row, column) {
    openDialog(
      "Static text",
      [
        { name: "value", label: "Text", value: "", required: true, maxlength: 79 },
        colorField("GREEN"),
        alignField("left")
      ],
      function (values) {
        var element = BMS.Elements.buildText(row, column, values.value, {
          color: values.color,
          align: values.align,
          columns: state.screen.columns
        });
        var result = BMS.Elements.addElement(state.screen, element);
        if (!result.ok) {
          return result.errors[0];
        }
        state.selectedId = element.id;
        showError("");
        return null;
      },
      function (values) {
        var ghost = ghostField("text", row, column, 1, {
          value: values.value,
          color: values.color,
          align: values.align
        });
        showScreenPreview([ghost], {
          row: ghost.row,
          column: ghost.column,
          length: ghost.length
        }, "text");
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
          label: "Name (cols 1–7)",
          value: BMS.Elements.defaultFieldName(state.screen, type),
          required: true,
          maxlength: 7,
          pattern: "[A-Za-z][A-Za-z0-9]{0,6}"
        },
        {
          name: "fieldLength",
          label: "Length",
          value: String(length),
          required: true,
          type: "number"
        },
        colorField(type === "input" ? "TURQUOISE" : "YELLOW"),
        alignField("left")
      ].concat(type === "input" ? [dataTypeField(false)] : []),
      function (values) {
        var fieldLength = parseInt(values.fieldLength, 10);
        if (!Number.isInteger(fieldLength) || fieldLength < 1) {
          return "Length must be at least 1.";
        }
        var element = BMS.Elements.buildField(
          type,
          row,
          column,
          values.name,
          fieldLength,
          {
            color: values.color,
            align: values.align,
            columns: state.screen.columns,
            numeric: values.numeric
          }
        );
        var result = BMS.Elements.addElement(state.screen, element);
        if (!result.ok) {
          return result.errors[0];
        }
        state.selectedId = element.id;
        showError("");
        return null;
      },
      function (values) {
        var fieldLength = parseInt(values.fieldLength, 10);
        if (!fieldLength || fieldLength < 1) {
          fieldLength = 1;
        }
        var ghost = ghostField(type, row, column, fieldLength, {
          name: values.name,
          color: values.color,
          align: values.align,
          numeric: values.numeric
        });
        showScreenPreview([ghost], {
          row: ghost.row,
          column: ghost.column,
          length: ghost.length
        }, type);
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
    return null;
  }

  function previewBuilt(built, label, fallbackRegion) {
    if (!built.ok) {
      showScreenPreview(null, null, label);
      return;
    }
    showScreenPreview(built.elements, fallbackRegion, label);
  }

  function ruleField() {
    return {
      name: "rule",
      label: "Delimiter",
      type: "select",
      value: "=",
      options: [
        { value: "=", label: "=======  equals" },
        { value: ".", label: ".......  dots" },
        { value: "-", label: "-------  dashes" },
        { value: "*", label: "*******  asterisks" },
        { value: "_", label: "_______  underscores" }
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
          maxlength: 79
        },
        {
          name: "subtitle",
          label: "Subtitle",
          value: "MAIN MENU",
          maxlength: 79
        },
        ruleField(),
        colorField("YELLOW")
      ],
      function (values) {
        return insertComponent(BMS.Components.buildHeader(state.screen, values));
      },
      function (values) {
        previewBuilt(BMS.Components.buildHeader(state.screen, values), "header", {
          row: 1,
          column: 1,
          length: 79
        });
      }
    );
  }

  function openSeparatorDialog() {
    openDialog(
      "Separator",
      [
        {
          name: "row",
          label: "Row",
          value: String(BMS.Components.firstFreeRow(state.screen, 5, 21)),
          required: true,
          type: "number"
        },
        ruleField(),
        {
          name: "custom",
          label: "Other character (optional, 1 character)",
          value: "",
          maxlength: 1
        },
        colorField("NEUTRAL")
      ],
      function (values) {
        return insertComponent(BMS.Components.buildSeparator(state.screen, values));
      },
      function (values) {
        previewBuilt(BMS.Components.buildSeparator(state.screen, values), "separator", {
          row: Number(values.row) || 1,
          column: 1,
          length: 79
        });
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
      },
      function (values) {
        previewBuilt(BMS.Components.buildFooter(state.screen, values), "footer", {
          row: state.screen.rows - 1,
          column: 1,
          length: 79
        });
      }
    );
  }

  function beginCreate(cell) {
    interaction = {
      mode: "create",
      start: cell,
      end: cell
    };
    previewCreateRegion(cell, cell);
    BMS.Screen.render(state, { start: cell, end: cell });
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
          previewCreateRegion(interaction.start, interaction.end);
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

  document.getElementById("btn-comp-separator").addEventListener("click", function () {
    setTool("select");
    openSeparatorDialog();
  });

  function bindHeaderField(id, key) {
    document.getElementById(id).addEventListener("input", function (event) {
      state.screen[key] = BMS.Elements.normalizeName(event.target.value);
      var errors = BMS.Validation.validateHeader(state.screen);
      showError(errors.length ? errors[0] : "");
      refreshBms();
      schedulePersist();
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
        clearPreview();
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

  try {
    var savedSize = parseInt(window.localStorage.getItem(PAGE_SIZE_KEY), 10);
    if (Number.isInteger(savedSize) && savedSize >= 1 && savedSize <= 200 && pageSizeInput) {
      pageSizeInput.value = String(savedSize);
    }
  } catch (err) {
    /* ignore */
  }

  if (pageSizeInput) {
    pageSizeInput.addEventListener("change", function () {
      var size = currentPageSize();
      pageSizeInput.value = String(size);
      try {
        window.localStorage.setItem(PAGE_SIZE_KEY, String(size));
      } catch (err) {
        /* ignore */
      }
      pageIndex = 0;
      updateSourcePage(true);
    });
  }

  if (pagePrevBtn) {
    pagePrevBtn.addEventListener("click", function () {
      goToPage(pageIndex - 1, true);
    });
  }

  if (pageNextBtn) {
    pageNextBtn.addEventListener("click", function () {
      goToPage(pageIndex + 1, true);
    });
  }

  if (copyPageBtn) {
    copyPageBtn.addEventListener("click", function () {
      copyCurrentPage(true);
    });
  }

  document.getElementById("btn-copy-fields").addEventListener("click", function () {
    if (!state.screen.elements.length) {
      showError("No DFHMDF fields to copy.");
      return;
    }
    copySource(
      BMS.Generator.generateFields(state.screen.elements, state.screen),
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

  document.getElementById("btn-save").addEventListener("click", function () {
    openSaveDialog(false);
  });

  document.getElementById("btn-save-as").addEventListener("click", function () {
    openSaveDialog(true);
  });

  document.getElementById("btn-duplicate-map").addEventListener("click", function () {
    duplicateMap(state.projectId);
  });

  document.getElementById("btn-import-bms").addEventListener("click", function () {
    openImportDialog();
  });

  document.getElementById("btn-new-map").addEventListener("click", function () {
    newMap();
  });

  document.getElementById("btn-new-map-here").addEventListener("click", function () {
    newMap();
  });

  document.getElementById("btn-new-project").addEventListener("click", function () {
    openProjectDialog();
  });

  document.getElementById("btn-new-folder").addEventListener("click", function () {
    openFolderDialog();
  });

  window.addEventListener("beforeunload", function () {
    cancelPersist();
    if (!persistQuiet) {
      persistNow();
    }
  });

  window.setInterval(updateSaveStatus, 15000);

  (function boot() {
    var ensured = BMS.Storage.ensureActive(BMS.Elements.createScreen());
    if (ensured.ok && ensured.map) {
      applyMap(ensured.map);
    }
    setTool("select");
    render();
  })();
})();
