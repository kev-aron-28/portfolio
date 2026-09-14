/**
 * Persist projects, folders, and named 3270 maps in localStorage.
 *
 * Tree: project (root folder) → optional subfolder → maps.
 */
(function (global) {
  var BMS = global.BMS || {};
  var STORAGE_KEY = "bms-cics-generator:v1";
  var VERSION = 2;
  var MAX_FOLDER_DEPTH = 1;

  function now() {
    return Date.now();
  }

  function createMapId() {
    return "map-" + now().toString(36) + "-" + Math.floor(Math.random() * 1e6).toString(36);
  }

  function createFolderId() {
    return "fld-" + now().toString(36) + "-" + Math.floor(Math.random() * 1e6).toString(36);
  }

  function emptyStore() {
    return {
      version: VERSION,
      activeId: "",
      folders: [],
      maps: []
    };
  }

  function normalizeFolderName(value) {
    return String(value || "").replace(/\s+/g, " ").trim().slice(0, 40);
  }

  function byName(a, b) {
    return String(a.name || "").localeCompare(String(b.name || ""), undefined, {
      sensitivity: "base"
    });
  }

  function makeFolder(name, parentId) {
    var stamp = now();
    return {
      id: createFolderId(),
      name: normalizeFolderName(name) || "Folder",
      parentId: parentId || null,
      collapsed: false,
      createdAt: stamp,
      updatedAt: stamp
    };
  }

  function folderMap(store) {
    var out = {};
    (store.folders || []).forEach(function (folder) {
      out[folder.id] = folder;
    });
    return out;
  }

  function folderDepth(store, folder) {
    var ids = folderMap(store);
    var depth = 0;
    var current = folder;
    var guard = 0;
    while (current && current.parentId && ids[current.parentId] && guard < 8) {
      depth += 1;
      current = ids[current.parentId];
      guard += 1;
    }
    return depth;
  }

  function defaultFolder(store) {
    var roots = (store.folders || []).filter(function (folder) {
      return !folder.parentId;
    });
    if (roots.length) {
      return roots.slice().sort(byName)[0];
    }
    var created = makeFolder("Maps", null);
    store.folders.push(created);
    return created;
  }

  function migrate(store) {
    if (!store || typeof store !== "object") {
      store = emptyStore();
    }
    if (!Array.isArray(store.maps)) {
      store.maps = [];
    }
    if (!Array.isArray(store.folders)) {
      store.folders = [];
    }

    var ids = folderMap(store);
    store.folders.forEach(function (folder) {
      if (folder.parentId && !ids[folder.parentId]) {
        folder.parentId = null;
      }
      folder.name = normalizeFolderName(folder.name) || "Folder";
      folder.collapsed = !!folder.collapsed;
    });

    var fallback = defaultFolder(store);
    ids = folderMap(store);
    store.maps.forEach(function (map) {
      if (!map.folderId || !ids[map.folderId]) {
        map.folderId = fallback.id;
      }
    });

    store.version = VERSION;
    store.activeId = store.activeId || "";
    return store;
  }

  function readRaw() {
    try {
      var raw = window.localStorage.getItem(STORAGE_KEY);
      if (!raw) {
        return migrate(emptyStore());
      }
      var parsed = JSON.parse(raw);
      return migrate(parsed);
    } catch (err) {
      return migrate(emptyStore());
    }
  }

  function writeRaw(store) {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(store));
  }

  function persist(store) {
    try {
      writeRaw(migrate(store));
      return { ok: true, store: store };
    } catch (err) {
      return { ok: false, error: "Could not write to localStorage." };
    }
  }

  function defaultName(screen) {
    return BMS.Elements.normalizeName(
      (screen && (screen.mapset || screen.mapName)) || "MAP"
    );
  }

  function sortMaps(maps) {
    return maps.slice().sort(function (a, b) {
      return (b.updatedAt || 0) - (a.updatedAt || 0);
    });
  }

  function findIndex(store, id) {
    var i;
    for (i = 0; i < store.maps.length; i += 1) {
      if (store.maps[i].id === id) {
        return i;
      }
    }
    return -1;
  }

  function findFolderIndex(store, id) {
    var i;
    for (i = 0; i < store.folders.length; i += 1) {
      if (store.folders[i].id === id) {
        return i;
      }
    }
    return -1;
  }

  function resolveFolderId(store, folderId) {
    var ids = folderMap(store);
    if (folderId && ids[folderId]) {
      return folderId;
    }
    return defaultFolder(store).id;
  }

  function snapshot(map, screen, extras) {
    extras = extras || {};
    var copy = {
      id: map.id,
      name: extras.name != null ? String(extras.name || "").trim() : map.name,
      folderId: extras.folderId !== undefined ? extras.folderId : map.folderId || "",
      createdAt: map.createdAt,
      updatedAt: now(),
      selectedId: extras.selectedId !== undefined ? extras.selectedId : map.selectedId || null,
      screen: BMS.Elements.cloneScreen(screen || map.screen)
    };
    if (!copy.name) {
      copy.name = defaultName(copy.screen);
    }
    return copy;
  }

  function list() {
    return sortMaps(readRaw().maps);
  }

  function listFolders() {
    return readRaw().folders.slice().sort(byName);
  }

  function get(id) {
    var store = readRaw();
    var index = findIndex(store, id);
    return index === -1 ? null : store.maps[index];
  }

  function getFolder(id) {
    var store = readRaw();
    var index = findFolderIndex(store, id);
    return index === -1 ? null : store.folders[index];
  }

  function activeId() {
    return readRaw().activeId || "";
  }

  function defaultFolderId() {
    return defaultFolder(readRaw()).id;
  }

  function children(parentId) {
    var store = readRaw();
    var pid = parentId || null;
    return {
      folders: store.folders
        .filter(function (folder) {
          return (folder.parentId || null) === pid;
        })
        .slice()
        .sort(byName),
      maps: store.maps
        .filter(function (map) {
          return map.folderId === pid;
        })
        .slice()
        .sort(byName)
    };
  }

  function roots() {
    return children(null).folders;
  }

  function folderPath(id) {
    var store = readRaw();
    var ids = folderMap(store);
    var parts = [];
    var current = ids[id];
    var guard = 0;
    while (current && guard < 8) {
      parts.unshift(current.name);
      current = current.parentId ? ids[current.parentId] : null;
      guard += 1;
    }
    return parts.join(" / ");
  }

  function folderOptions() {
    var store = readRaw();
    var options = [];
    roots().forEach(function (project) {
      options.push({
        id: project.id,
        label: project.name,
        depth: 0
      });
      children(project.id).folders.forEach(function (folder) {
        options.push({
          id: folder.id,
          label: project.name + " / " + folder.name,
          depth: 1
        });
      });
    });
    if (!options.length) {
      var created = defaultFolder(store);
      persist(store);
      options.push({ id: created.id, label: created.name, depth: 0 });
    }
    return options;
  }

  function descendantFolderIds(store, folderId) {
    var ids = [folderId];
    var changed = true;
    while (changed) {
      changed = false;
      store.folders.forEach(function (folder) {
        if (folder.parentId && ids.indexOf(folder.parentId) !== -1 && ids.indexOf(folder.id) === -1) {
          ids.push(folder.id);
          changed = true;
        }
      });
    }
    return ids;
  }

  function create(screen, extras) {
    extras = extras || {};
    var store = readRaw();
    var map = snapshot(
      {
        id: createMapId(),
        name: extras.name || defaultName(screen),
        folderId: resolveFolderId(store, extras.folderId),
        createdAt: now(),
        selectedId: extras.selectedId || null
      },
      screen,
      extras
    );
    map.folderId = resolveFolderId(store, map.folderId);
    store.maps.push(map);
    store.activeId = map.id;
    var result = persist(store);
    if (!result.ok) {
      return result;
    }
    result.map = map;
    return result;
  }

  function update(id, screen, extras) {
    var store = readRaw();
    var index = findIndex(store, id);
    if (index === -1) {
      return create(screen, extras);
    }
    var next = snapshot(store.maps[index], screen, extras);
    next.folderId = resolveFolderId(store, next.folderId);
    store.maps[index] = next;
    store.activeId = id;
    var result = persist(store);
    if (!result.ok) {
      return result;
    }
    result.map = store.maps[index];
    return result;
  }

  function rename(id, name) {
    var store = readRaw();
    var index = findIndex(store, id);
    if (index === -1) {
      return { ok: false, error: "Save not found." };
    }
    store.maps[index].name = String(name || "").trim() || defaultName(store.maps[index].screen);
    store.maps[index].updatedAt = now();
    var result = persist(store);
    if (!result.ok) {
      return result;
    }
    result.map = store.maps[index];
    return result;
  }

  function moveMap(id, folderId) {
    var store = readRaw();
    var index = findIndex(store, id);
    if (index === -1) {
      return { ok: false, error: "Save not found." };
    }
    store.maps[index].folderId = resolveFolderId(store, folderId);
    store.maps[index].updatedAt = now();
    var result = persist(store);
    if (!result.ok) {
      return result;
    }
    result.map = store.maps[index];
    return result;
  }

  function setActive(id) {
    var store = readRaw();
    if (findIndex(store, id) === -1) {
      return { ok: false, error: "Save not found." };
    }
    store.activeId = id;
    return persist(store);
  }

  function remove(id) {
    var store = readRaw();
    var index = findIndex(store, id);
    if (index === -1) {
      return { ok: false, error: "Save not found." };
    }
    store.maps.splice(index, 1);
    if (store.activeId === id) {
      store.activeId = store.maps.length ? sortMaps(store.maps)[0].id : "";
    }
    var result = persist(store);
    if (!result.ok) {
      return result;
    }
    result.activeId = store.activeId;
    return result;
  }

  function namesInFolder(store, folderId) {
    var used = {};
    store.maps.forEach(function (map) {
      if (map.folderId === folderId && map.name) {
        used[String(map.name).toUpperCase()] = true;
      }
    });
    return used;
  }

  function nextCopyName(folderId, name) {
    var store = readRaw();
    var fid = resolveFolderId(store, folderId);
    var used = namesInFolder(store, fid);
    var base = String(name || "MP").replace(/\s+/g, " ").trim();
    var suffix = base.match(/^(.*)-(\d+)$/);
    if (suffix) {
      base = suffix[1];
    }
    if (!base) {
      base = "MP";
    }
    if (base.length > 36) {
      base = base.slice(0, 36);
    }
    var n = 1;
    var candidate = base + "-" + n;
    while (used[candidate.toUpperCase()]) {
      n += 1;
      candidate = base + "-" + n;
    }
    return candidate;
  }

  function duplicate(id, extras) {
    extras = extras || {};
    var current = get(id);
    if (!current) {
      return { ok: false, error: "Save not found." };
    }
    var folderId = extras.folderId || current.folderId;
    return create(current.screen, {
      name: extras.name || nextCopyName(folderId, current.name || defaultName(current.screen)),
      folderId: folderId,
      selectedId: null
    });
  }

  function createFolder(name, parentId) {
    var store = readRaw();
    var label = normalizeFolderName(name);
    if (!label) {
      return { ok: false, error: "Name is required." };
    }
    var parent = null;
    if (parentId) {
      var parentIndex = findFolderIndex(store, parentId);
      if (parentIndex === -1) {
        return { ok: false, error: "Project not found." };
      }
      parent = store.folders[parentIndex];
      if (folderDepth(store, parent) >= MAX_FOLDER_DEPTH) {
        return { ok: false, error: "Folders can only go one level under a project." };
      }
    }
    var folder = makeFolder(label, parent ? parent.id : null);
    if (parent) {
      parent.collapsed = false;
      parent.updatedAt = now();
    }
    store.folders.push(folder);
    var result = persist(store);
    if (!result.ok) {
      return result;
    }
    result.folder = folder;
    return result;
  }

  function renameFolder(id, name) {
    var store = readRaw();
    var index = findFolderIndex(store, id);
    if (index === -1) {
      return { ok: false, error: "Folder not found." };
    }
    var label = normalizeFolderName(name);
    if (!label) {
      return { ok: false, error: "Name is required." };
    }
    store.folders[index].name = label;
    store.folders[index].updatedAt = now();
    var result = persist(store);
    if (!result.ok) {
      return result;
    }
    result.folder = store.folders[index];
    return result;
  }

  function toggleCollapsed(id) {
    var store = readRaw();
    var index = findFolderIndex(store, id);
    if (index === -1) {
      return { ok: false, error: "Folder not found." };
    }
    store.folders[index].collapsed = !store.folders[index].collapsed;
    var result = persist(store);
    if (!result.ok) {
      return result;
    }
    result.folder = store.folders[index];
    return result;
  }

  function removeFolder(id) {
    var store = readRaw();
    if (findFolderIndex(store, id) === -1) {
      return { ok: false, error: "Folder not found." };
    }
    var doomed = descendantFolderIds(store, id);
    store.folders = store.folders.filter(function (folder) {
      return doomed.indexOf(folder.id) === -1;
    });
    store.maps = store.maps.filter(function (map) {
      return doomed.indexOf(map.folderId) === -1;
    });
    if (!store.folders.length) {
      defaultFolder(store);
    }
    if (store.activeId && findIndex(store, store.activeId) === -1) {
      store.activeId = store.maps.length ? sortMaps(store.maps)[0].id : "";
    }
    var result = persist(store);
    if (!result.ok) {
      return result;
    }
    result.activeId = store.activeId;
    return result;
  }

  function ensureActive(screen) {
    var store = readRaw();
    defaultFolder(store);
    if (store.activeId && findIndex(store, store.activeId) !== -1) {
      persist(store);
      return { ok: true, map: store.maps[findIndex(store, store.activeId)], created: false };
    }
    if (store.maps.length) {
      store.activeId = sortMaps(store.maps)[0].id;
      persist(store);
      return { ok: true, map: store.maps[findIndex(store, store.activeId)], created: false };
    }
    persist(store);
    return create(screen || BMS.Elements.createScreen(), {
      name: defaultName(screen),
      folderId: defaultFolder(store).id
    });
  }

  BMS.Storage = {
    STORAGE_KEY: STORAGE_KEY,
    defaultName: defaultName,
    list: list,
    listFolders: listFolders,
    get: get,
    getFolder: getFolder,
    activeId: activeId,
    defaultFolderId: defaultFolderId,
    children: children,
    roots: roots,
    folderPath: folderPath,
    folderOptions: folderOptions,
    create: create,
    update: update,
    rename: rename,
    moveMap: moveMap,
    setActive: setActive,
    remove: remove,
    nextCopyName: nextCopyName,
    duplicate: duplicate,
    createFolder: createFolder,
    renameFolder: renameFolder,
    toggleCollapsed: toggleCollapsed,
    removeFolder: removeFolder,
    ensureActive: ensureActive
  };

  global.BMS = BMS;
})(window);
