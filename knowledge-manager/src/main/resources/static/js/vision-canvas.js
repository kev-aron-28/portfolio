/**
 * Personal Strategy & Vision — infinite canvas engine.
 * HTML nodes + SVG edges + CSS transform viewport.
 */
window.VisionCanvas = (function () {
  'use strict';

  var TIMELINE_ORIGIN_X = 0;
  var NODE_SIZES = {
    vision: { w: 240, h: 88 },
    milestone: { w: 168, h: 72 },
    skill: { w: 100, h: 122 },
    section: { w: 200, h: 44 },
    project: { w: 108, h: 128 },
    habit: { w: 150, h: 68 },
    knowledge: { w: 150, h: 68 },
    label: { w: 220, h: 48 },
    note: { w: 180, h: 120 },
    zone: { w: 280, h: 180 }
  };

  var ANNOTATION_COLORS = ['teal', 'amber', 'sky', 'slate', 'rose'];

  var EDGE_KINDS = ['supports', 'requires', 'unlocks', 'reinforces', 'contributes'];
  var MONTH_NAMES = [
    'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
    'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
  ];

  function uid(prefix) {
    return prefix + '-' + Math.random().toString(36).slice(2, 9);
  }

  function daysInMonth(y, m) {
    return new Date(y, m, 0).getDate();
  }

  function parseDate(str) {
    if (!str || typeof str !== 'string') {
      return null;
    }
    var parts = str.split('-');
    if (parts.length < 2) {
      return null;
    }
    var y = parseInt(parts[0], 10);
    var m = parseInt(parts[1], 10);
    var d = parts.length >= 3 ? parseInt(parts[2], 10) : 1;
    if (!y || !m || !d) {
      return null;
    }
    d = Math.max(1, Math.min(d, daysInMonth(y, m)));
    return { y: y, m: m, d: d };
  }

  function formatDate(y, m, d) {
    return (
      y +
      '-' +
      String(m).padStart(2, '0') +
      '-' +
      String(d == null ? 1 : d).padStart(2, '0')
    );
  }

  function normalizeDateString(str) {
    var p = parseDate(str);
    return p ? formatDate(p.y, p.m, p.d) : null;
  }

  function prettyDate(str) {
    var p = parseDate(str);
    if (!p) {
      return str || '';
    }
    return MONTH_NAMES[p.m - 1] + ' ' + p.d + ', ' + p.y;
  }

  function monthsBetween(a, b) {
    return (b.y - a.y) * 12 + (b.m - a.m);
  }

  function addMonths(start, offset) {
    var total = start.y * 12 + (start.m - 1) + offset;
    var y = Math.floor(total / 12);
    var m = (total % 12) + 1;
    return { y: y, m: m, d: 1 };
  }

  function clamp(n, min, max) {
    return Math.max(min, Math.min(max, n));
  }

  function livesOnTimeline(type) {
    return type === 'vision' || type === 'milestone';
  }

  function isAnnotation(type) {
    return type === 'label' || type === 'note' || type === 'zone';
  }

  function isSection(type) {
    return type === 'section';
  }

  function isTreeOrbType(type) {
    return type === 'skill' || type === 'project';
  }

  function staysOffTimeline(type) {
    return isAnnotation(type) || isSection(type);
  }

  function hasAccentColor(type) {
    return isAnnotation(type) || isSection(type);
  }

  function normalizeColor(color) {
    return ANNOTATION_COLORS.indexOf(color) >= 0 ? color : 'teal';
  }

  function defaultScene() {
    return {
      version: 1,
      viewport: { x: 0, y: 0, zoom: 1 },
      timeline: { start: '2026-01-01', end: '2036-01-01', unit: 'day', y: 180 },
      bookmarks: [],
      skillPoints: 24,
      nodes: [],
      edges: []
    };
  }

  function loadSceneFromPage() {
    var el = document.getElementById('visionSceneData');
    if (!el) {
      return defaultScene();
    }
    try {
      var raw = typeof el.value === 'string' ? el.value : el.textContent || '';
      var parsed = JSON.parse(raw || '{}');
      return normalizeScene(parsed);
    } catch (e) {
      return defaultScene();
    }
  }

  function normalizeScene(raw) {
    var scene = Object.assign(defaultScene(), raw || {});
    scene.viewport = Object.assign({ x: 0, y: 0, zoom: 1 }, scene.viewport || {});
    scene.timeline = Object.assign(
      { start: '2026-01-01', end: '2036-01-01', unit: 'day', y: 180 },
      scene.timeline || {}
    );
    scene.timeline.start = normalizeDateString(scene.timeline.start) || '2026-01-01';
    scene.timeline.end = normalizeDateString(scene.timeline.end) || '2036-01-01';
    scene.timeline.unit = 'day';
    scene.bookmarks = Array.isArray(scene.bookmarks) ? scene.bookmarks : [];
    scene.nodes = Array.isArray(scene.nodes) ? scene.nodes : [];
    scene.edges = Array.isArray(scene.edges) ? scene.edges : [];
    scene.timelineStacks = Array.isArray(scene.timelineStacks) ? scene.timelineStacks : [];
    if (scene.skillPoints == null || isNaN(scene.skillPoints)) {
      scene.skillPoints = 24;
    }
    scene.skillPoints = clamp(parseInt(scene.skillPoints, 10) || 24, 1, 500);
    var hasRoot = false;
    scene.nodes.forEach(function (n) {
      if (n.date) {
        n.date = normalizeDateString(n.date) || n.date;
      }
      if (n.type === 'skill') {
        if (n.isRoot) {
          hasRoot = true;
          n.skillState = 'intro';
          n.progress = 0;
        }
        if (!n.skillState) {
          n.skillState = n.isRoot ? 'intro' : 'locked';
        }
      }
      if (n.type === 'project') {
        n.isRoot = false;
        if (!n.skillState) {
          n.skillState = 'locked';
        }
        if (n.progress == null) {
          n.progress = 0;
        }
      }
      if (staysOffTimeline(n.type)) {
        n.anchoredToTimeline = false;
        n.color = normalizeColor(n.color);
        var base = NODE_SIZES[n.type] || NODE_SIZES.note;
        if (n.type === 'zone') {
          n.w = n.w != null ? n.w : base.w;
          n.h = n.h != null ? n.h : base.h;
        } else if (n.w == null) {
          n.w = base.w;
        }
        if (n.type !== 'zone' && n.h == null) {
          n.h = base.h;
        }
      } else if (livesOnTimeline(n.type) && n.date) {
        n.anchoredToTimeline = true;
      }
    });
    if (!hasRoot) {
      var firstSkill = null;
      for (var si = 0; si < scene.nodes.length; si++) {
        if (scene.nodes[si].type === 'skill') {
          firstSkill = scene.nodes[si];
          break;
        }
      }
      if (firstSkill) {
        firstSkill.isRoot = true;
        if (firstSkill.skillState === 'locked') {
          firstSkill.skillState = 'available';
        }
      }
    }
    scene.viewport.zoom = clamp(scene.viewport.zoom || 1, 0.25, 2.5);
    return scene;
  }

  function mount(host) {
    if (!host) {
      return null;
    }

    var saveUrl = host.dataset.saveUrl || null;
    var scene = loadSceneFromPage();
    var selectedId = null;
    var selectedEdgeId = null;
    var connectMode = false;
    var connectFrom = null;
    var focusMode = false;
    var trainMode = false;
    var hoverSkillId = null;
    var pendingTrainAction = null;
    var skillMenuNodeId = null;
    var lastTrainTap = { id: null, time: 0 };
    var spaceDown = false;
    var isPanning = false;
    var panLast = null;
    var dragNode = null;
    var dragOffset = null;
    var resizeNode = null;
    var resizeStart = null;
    var timelineDragging = false;
    var rangeMonths = 36;
    var saveTimer = null;
    var rafPending = false;
    var createDraft = null;

    var statusEl = document.getElementById('visionSaveStatus');
    var zoomLabel = document.getElementById('vcZoomLabel');
    var inspector = document.getElementById('vcInspector');
    var minimapCanvas = document.getElementById('vcMinimapCanvas');
    var minimapViewport = document.getElementById('vcMinimapViewport');
    var bookmarksEl = document.getElementById('vcBookmarks');
    var searchEl = document.getElementById('vcSearch');
    var searchInput = document.getElementById('vcSearchInput');
    var searchResults = document.getElementById('vcSearchResults');
    var createModal = document.getElementById('vcCreateModal');
    var createDateHint = document.getElementById('vcCreateDateHint');
    var createTitle = document.getElementById('vcCreateTitle');

    host.innerHTML = '';
    var world = document.createElement('div');
    world.className = 'vc-world';
    var timelineBand = document.createElement('div');
    timelineBand.className = 'vc-timeline-band';
    var timeline = document.createElement('div');
    timeline.className = 'vc-timeline';
    var scrubber = document.createElement('div');
    scrubber.className = 'vc-timeline-scrubber';
    scrubber.hidden = true;
    scrubber.innerHTML = '<div class="vc-timeline-scrubber-line"></div><span class="vc-timeline-scrubber-label"></span>';
    var edgesSvg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    edgesSvg.classList.add('vc-edges');
    edgesSvg.setAttribute('width', '16000');
    edgesSvg.setAttribute('height', '8000');
    var nodesLayer = document.createElement('div');
    nodesLayer.className = 'vc-nodes';
    world.appendChild(timelineBand);
    world.appendChild(timeline);
    world.appendChild(scrubber);
    world.appendChild(edgesSvg);
    world.appendChild(nodesLayer);
    host.appendChild(world);

    function setStatus(text) {
      if (statusEl) {
        statusEl.textContent = text;
      }
    }

    function monthPx() {
      if (rangeMonths <= 1) {
        return 360;
      }
      if (rangeMonths <= 6) {
        return 180;
      }
      if (rangeMonths <= 12) {
        return 130;
      }
      if (rangeMonths <= 36) {
        return 78;
      }
      if (rangeMonths <= 60) {
        return 52;
      }
      return 36;
    }

    function timelineStart() {
      return parseDate(scene.timeline.start) || { y: 2026, m: 1, d: 1 };
    }

    function timelineY() {
      return scene.timeline.y == null ? 180 : scene.timeline.y;
    }

    function dateToX(dateStr) {
      var start = timelineStart();
      var p = parseDate(dateStr);
      if (!p) {
        return null;
      }
      var monthOffset = monthsBetween(start, p);
      var dim = daysInMonth(p.y, p.m);
      var dayFrac = (p.d - 1) / dim;
      return TIMELINE_ORIGIN_X + (monthOffset + dayFrac) * monthPx();
    }

    function timelineBounds() {
      return {
        minX: TIMELINE_ORIGIN_X,
        maxX: TIMELINE_ORIGIN_X + rangeMonths * monthPx()
      };
    }

    function clampTimelineX(x) {
      var b = timelineBounds();
      return clamp(x, b.minX, b.maxX);
    }

    function xToDate(x) {
      var start = timelineStart();
      var px = monthPx();
      var raw = clamp((x - TIMELINE_ORIGIN_X) / px, 0, rangeMonths);
      var monthOffset = Math.min(Math.floor(raw), Math.max(rangeMonths - 0.0001, 0));
      if (raw >= rangeMonths) {
        var end = addMonths(start, rangeMonths);
        return formatDate(end.y, end.m, 1);
      }
      var frac = raw - Math.floor(raw);
      var m = addMonths(start, Math.floor(raw));
      var dim = daysInMonth(m.y, m.m);
      var day = clamp(Math.round(frac * dim) + 1, 1, dim);
      if (frac >= 0.999) {
        day = dim;
      }
      return formatDate(m.y, m.m, day);
    }

    function isDateInRange(dateStr) {
      var start = timelineStart();
      var p = parseDate(dateStr);
      if (!p) {
        return false;
      }
      var x = dateToX(dateStr);
      if (x == null) {
        return false;
      }
      var b = timelineBounds();
      return x >= b.minX - 0.5 && x <= b.maxX + 0.5;
    }

    // Back-compat aliases used during transition
    function monthToX(s) {
      return dateToX(s);
    }
    function xToMonth(x) {
      return xToDate(x);
    }
    function prettyMonth(s) {
      return prettyDate(s);
    }
    function isMonthInRange(s) {
      return isDateInRange(s);
    }
    function formatMonth(y, m) {
      return formatDate(y, m, 1);
    }

    function nodeSize(typeOrNode) {
      var type =
        typeof typeOrNode === 'object' && typeOrNode ? typeOrNode.type : typeOrNode;
      var node = typeof typeOrNode === 'object' && typeOrNode ? typeOrNode : null;
      var base = NODE_SIZES[type] || NODE_SIZES.milestone;
      if (node && hasAccentColor(type)) {
        return {
          w: node.w != null ? node.w : base.w,
          h: node.h != null ? node.h : base.h
        };
      }
      return { w: base.w, h: base.h };
    }

    function applyViewport() {
      var z = scene.viewport.zoom;
      var x = scene.viewport.x;
      var y = scene.viewport.y;
      world.style.transform = 'translate(' + x + 'px,' + y + 'px) scale(' + z + ')';
      var size = 22 * z;
      host.style.backgroundPosition = x + 'px ' + y + 'px';
      host.style.backgroundSize = size + 'px ' + size + 'px';
      if (zoomLabel) {
        zoomLabel.textContent = Math.round(z * 100) + '%';
      }
      updateMinimap();
    }

    function scheduleViewport() {
      if (rafPending) {
        return;
      }
      rafPending = true;
      requestAnimationFrame(function () {
        rafPending = false;
        applyViewport();
      });
    }

    function serialize() {
      return JSON.stringify(scene);
    }

    function scheduleSave() {
      if (!saveUrl) {
        return;
      }
      setStatus('Saving…');
      clearTimeout(saveTimer);
      saveTimer = setTimeout(persist, 700);
    }

    function persist() {
      if (!saveUrl) {
        return;
      }
      fetch(saveUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ sceneJson: serialize() })
      })
        .then(function (res) {
          if (!res.ok) {
            throw new Error('save failed');
          }
          setStatus('Saved');
        })
        .catch(function () {
          setStatus('Save failed');
        });
    }

    function hiddenIds() {
      var hidden = {};
      var children = {};
      scene.edges.forEach(function (e) {
        if (!children[e.from]) {
          children[e.from] = [];
        }
        children[e.from].push(e.to);
      });

      function markDescendants(id) {
        (children[id] || []).forEach(function (cid) {
          if (!hidden[cid]) {
            hidden[cid] = true;
            markDescendants(cid);
          }
        });
      }

      function centerInsideZone(node, zone) {
        if (!node || !zone || node.id === zone.id || node.type === 'zone') {
          return false;
        }
        var c = nodeCenter(node);
        var zs = nodeSize(zone);
        return (
          c.x >= zone.x &&
          c.x <= zone.x + zs.w &&
          c.y >= zone.y &&
          c.y <= zone.y + zs.h
        );
      }

      scene.nodes.forEach(function (n) {
        if (!n.collapsed) {
          return;
        }
        if (n.type === 'zone') {
          scene.nodes.forEach(function (other) {
            if (centerInsideZone(other, n) && !hidden[other.id]) {
              hidden[other.id] = true;
              markDescendants(other.id);
            }
          });
          return;
        }
        markDescendants(n.id);
      });
      return hidden;
    }

    function focusSet(rootId) {
      var set = {};
      set[rootId] = true;
      scene.edges.forEach(function (e) {
        if (e.from === rootId || e.to === rootId) {
          set[e.from] = true;
          set[e.to] = true;
        }
      });
      return set;
    }

    function skillGlyph(state) {
      if (state === 'mastered') {
        return '<i class="bi bi-check-lg"></i>';
      }
      if (state === 'learning') {
        return '<i class="bi bi-lightning-charge-fill"></i>';
      }
      if (state === 'locked') {
        return '<i class="bi bi-lock-fill"></i>';
      }
      return '<i class="bi bi-unlock-fill"></i>';
    }

    function skillRingSvg(progress) {
      var r = 30;
      var c = 2 * Math.PI * r;
      var p = clamp(progress == null ? 0 : progress, 0, 1);
      var offset = c * (1 - p);
      return (
        '<svg class="vc-skill-ring" viewBox="0 0 72 72" aria-hidden="true">' +
        '<circle class="vc-skill-ring-track" cx="36" cy="36" r="' +
        r +
        '"></circle>' +
        '<g transform="rotate(-90 36 36)">' +
        '<circle class="vc-skill-ring-progress" cx="36" cy="36" r="' +
        r +
        '" stroke-dasharray="' +
        c +
        '" stroke-dashoffset="' +
        offset +
        '"></circle>' +
        '</g></svg>'
      );
    }

    function compactRingSvg(progress) {
      var r = 8;
      var c = 2 * Math.PI * r;
      var p = clamp(progress == null ? 0 : progress, 0, 1);
      var offset = c * (1 - p);
      return (
        '<svg class="vc-node-ring" viewBox="0 0 22 22" aria-hidden="true">' +
        '<circle class="vc-ring-track" cx="11" cy="11" r="' + r + '"></circle>' +
        '<circle class="vc-ring-progress" cx="11" cy="11" r="' + r + '"' +
        ' stroke-dasharray="' + c + '" stroke-dashoffset="' + offset + '"></circle>' +
        '</svg>'
      );
    }

    function isTimelineAnchored(node) {
      if (!node || !node.date || staysOffTimeline(node.type)) {
        return false;
      }
      return livesOnTimeline(node.type) || !!node.anchoredToTimeline;
    }

    function layoutTimelineStacks() {
      // Only sync X to the chosen date. Y stays where the user dragged it.
      var baseY = timelineY() + 68;
      var byDate = {};

      scene.nodes.forEach(function (node) {
        if (!isTimelineAnchored(node) || !node.date) {
          return;
        }
        var x = monthToX(node.date);
        if (x == null) {
          return;
        }
        var s = nodeSize(node);
        node.x = x - s.w / 2;
        if (node.y < baseY) {
          node.y = baseY;
        }
        node.anchoredToTimeline = true;
        if (!byDate[node.date]) {
          byDate[node.date] = [];
        }
        byDate[node.date].push(node);
      });

      // Visual links follow the user's vertical order; positions are not forced
      scene.timelineStacks = [];
      Object.keys(byDate).forEach(function (date) {
        var nodes = byDate[date].slice().sort(function (a, b) {
          if (a.y !== b.y) {
            return a.y - b.y;
          }
          return String(a.id).localeCompare(String(b.id));
        });
        nodes.forEach(function (node, idx) {
          node.timelineOrder = idx;
        });
        if (nodes.length > 1) {
          scene.timelineStacks.push(
            nodes.map(function (n) {
              return n.id;
            })
          );
        }
      });
    }

    function syncTimelineBoundNodes() {
      layoutTimelineStacks();
    }

    function isNearTimeline(node) {
      if (staysOffTimeline(node.type)) {
        return false;
      }
      var s = nodeSize(node);
      var cy = node.y + s.h / 2;
      return Math.abs(cy - (timelineY() + 28)) < 120;
    }

    function renderTimeline() {
      var start = timelineStart();
      var y = timelineY();
      var px = monthPx();
      var bounds = timelineBounds();
      var width = bounds.maxX - bounds.minX;
      timeline.style.top = y + 'px';
      timeline.style.left = bounds.minX + 'px';
      timeline.style.width = width + 'px';
      timelineBand.style.top = y - 18 + 'px';
      timelineBand.style.left = bounds.minX + 'px';
      timelineBand.style.width = width + 'px';
      scrubber.style.top = y - 24 + 'px';

      var html =
        '<div class="vc-timeline-cap vc-timeline-cap-start" title="Timeline start"></div>' +
        '<div class="vc-timeline-cap vc-timeline-cap-end" title="Timeline end"></div>' +
        '<div class="vc-timeline-label">Timeline</div>' +
        '<div class="vc-timeline-track"></div>' +
        '<div class="vc-timeline-track-glow"></div>';

      function appendTick(leftPx, dateStr, label, major, isStart, isEnd) {
        html +=
          '<button type="button" class="vc-timeline-tick' +
          (major ? ' is-major' : '') +
          (label ? '' : ' is-minor') +
          (isStart ? ' is-start' : '') +
          (isEnd ? ' is-end' : '') +
          '" data-date="' +
          dateStr +
          '" style="left:' +
          leftPx +
          'px" title="' +
          prettyDate(dateStr) +
          '">' +
          '<span class="vc-timeline-tick-mark"></span>' +
          (label ? '<span class="vc-timeline-tick-label">' + label + '</span>' : '') +
          '</button>';
      }

      if (rangeMonths <= 1) {
        var dim = daysInMonth(start.y, start.m);
        for (var day = 1; day <= dim; day++) {
          var dateStr = formatDate(start.y, start.m, day);
          var left = ((day - 1) / dim) * px;
          var majorDay = day === 1 || day === 15 || day === dim;
          var label = majorDay ? String(day) : '';
          if (day === 1) {
            label = MONTH_NAMES[start.m - 1] + ' 1';
          }
          appendTick(left, dateStr, label, majorDay, day === 1, day === dim);
        }
      } else if (rangeMonths <= 6) {
        for (var mi = 0; mi <= rangeMonths; mi++) {
          var mm = addMonths(start, mi);
          var mDate = formatDate(mm.y, mm.m, 1);
          var mLabel =
            mm.m === 1 || mi === 0
              ? MONTH_NAMES[mm.m - 1] + ' ‘' + String(mm.y).slice(2)
              : MONTH_NAMES[mm.m - 1];
          appendTick(mi * px, mDate, mLabel, true, mi === 0, mi === rangeMonths);
          if (mi < rangeMonths) {
            // mid-month guides
            var mid = formatDate(mm.y, mm.m, 15);
            appendTick(mi * px + px * ((15 - 1) / daysInMonth(mm.y, mm.m)), mid, '', false, false, false);
          }
        }
      } else {
        var step = 1;
        if (rangeMonths > 12 && rangeMonths <= 36) {
          step = 3;
        } else if (rangeMonths > 36 && rangeMonths <= 60) {
          step = 6;
        } else if (rangeMonths > 60) {
          step = 12;
        }

        for (var i = 0; i <= rangeMonths; i += step) {
          var m = addMonths(start, i);
          var label = '';
          var major = false;

          if (rangeMonths <= 12) {
            major = m.m === 1 || i === 0;
            label = major
              ? MONTH_NAMES[m.m - 1] + ' ‘' + String(m.y).slice(2)
              : MONTH_NAMES[m.m - 1];
          } else if (step === 3) {
            if (m.m === 1) {
              label = String(m.y);
              major = true;
            } else {
              label = 'Q' + Math.ceil(m.m / 3);
              major = false;
            }
          } else if (step === 6) {
            if (m.m === 1) {
              label = String(m.y);
              major = true;
            }
          } else {
            label = String(m.y);
            major = true;
          }

          appendTick(
            i * px,
            formatDate(m.y, m.m, 1),
            label,
            major,
            i === 0,
            i === rangeMonths
          );
        }
      }

      // today marker if in range
      var now = new Date();
      var todayStr = formatDate(now.getFullYear(), now.getMonth() + 1, now.getDate());
      var todayX = dateToX(todayStr);
      if (todayX != null && isDateInRange(todayStr)) {
        html +=
          '<div class="vc-timeline-today" style="left:' +
          (todayX - bounds.minX) +
          'px"><span>Now</span></div>';
      }

      timeline.innerHTML = html;
    }

    function updateScrubber(worldX, visible) {
      if (!visible) {
        scrubber.hidden = true;
        return;
      }
      var x = clampTimelineX(worldX);
      var dateStr = xToDate(x);
      scrubber.hidden = false;
      scrubber.style.left = x + 'px';
      var label = scrubber.querySelector('.vc-timeline-scrubber-label');
      if (label) {
        label.textContent = prettyDate(dateStr);
      }
    }

    function centerOnMonth(monthStr) {
      var tx = dateToX(monthStr);
      if (tx == null) {
        return;
      }
      var rect = host.getBoundingClientRect();
      var z = scene.viewport.zoom;
      scene.viewport.x = rect.width / 2 - tx * z;
      scene.viewport.y = rect.height * 0.35 - timelineY() * z;
      scheduleViewport();
      scheduleSave();
    }

    function renderBookmarks() {
      if (!bookmarksEl) {
        return;
      }
      bookmarksEl.innerHTML = '';
      (scene.bookmarks || []).forEach(function (bm, index) {
        var chip = document.createElement('div');
        chip.className = 'vc-bookmark-chip';

        var goBtn = document.createElement('button');
        goBtn.type = 'button';
        goBtn.className = 'vc-bookmark-go';
        goBtn.textContent = bm.label || 'Bookmark';
        goBtn.title = 'Go to bookmark';
        goBtn.addEventListener('click', function () {
          scene.viewport.x = bm.x;
          scene.viewport.y = bm.y;
          scene.viewport.zoom = bm.zoom || 1;
          scheduleViewport();
          scheduleSave();
        });

        var removeBtn = document.createElement('button');
        removeBtn.type = 'button';
        removeBtn.className = 'vc-bookmark-remove';
        removeBtn.title = 'Remove bookmark';
        removeBtn.setAttribute('aria-label', 'Remove bookmark');
        removeBtn.innerHTML = '<i class="bi bi-x"></i>';
        removeBtn.addEventListener('click', function (e) {
          e.preventDefault();
          e.stopPropagation();
          if (bm.id) {
            scene.bookmarks = (scene.bookmarks || []).filter(function (item) {
              return item.id !== bm.id;
            });
          } else {
            scene.bookmarks.splice(index, 1);
          }
          renderBookmarks();
          scheduleSave();
        });

        chip.appendChild(goBtn);
        chip.appendChild(removeBtn);
        bookmarksEl.appendChild(chip);
      });
    }

    function renderNodes() {
      var hidden = hiddenIds();
      var focused = selectedId && focusMode ? focusSet(selectedId) : null;
      var existing = {};
      Array.prototype.forEach.call(nodesLayer.children, function (el) {
        existing[el.dataset.id] = el;
      });
      var seen = {};

      scene.nodes.forEach(function (node) {
        seen[node.id] = true;
        var el = existing[node.id];
        if (!el) {
          el = document.createElement('div');
          el.dataset.id = node.id;
          nodesLayer.appendChild(el);
        }
        var size = nodeSize(node);
        var stacked = (scene.timelineStacks || []).some(function (ids) {
          return ids.indexOf(node.id) >= 0;
        });
        var color = normalizeColor(node.color);
        var pathSet = pathHighlightSet();
        el.className =
          'vc-node vc-node-' +
          node.type +
          (selectedId === node.id ? ' selected' : '') +
          (hidden[node.id] ? ' hidden-collapsed' : '') +
          (focused && !focused[node.id] ? ' dimmed' : '') +
          (isTreeOrbType(node.type) && node.skillState && !node.isRoot
            ? ' is-' + node.skillState
            : '') +
          (stacked ? ' is-stacked' : '') +
          (hasAccentColor(node.type) ? ' is-color-' + color : '') +
          (node.collapsed && (node.type === 'zone' || node.type === 'section')
            ? ' is-collapsed'
            : '') +
          (node.type === 'section'
            ? node.sectionOpen
              ? ' is-section-open'
              : ' is-section-locked'
            : '') +
          (connectMode && connectFrom === node.id ? ' is-connect-from' : '') +
          (node.type === 'skill' && node.isRoot ? ' is-root is-intro-label' : '') +
          (node.type === 'project' ? ' is-project-orb' : '') +
          (pathSet && pathSet[node.id] ? ' is-on-path' : '') +
          (trainMode &&
          node.type !== 'skill' &&
          node.type !== 'project' &&
          node.type !== 'section'
            ? ' is-train-dim'
            : '');
        el.style.left = node.x + 'px';
        el.style.top = node.y + 'px';
        el.style.width = size.w + 'px';
        if (node.type === 'zone') {
          el.style.height = node.collapsed ? '42px' : size.h + 'px';
        } else if (node.type === 'note') {
          el.style.height = size.h + 'px';
        } else {
          el.style.height = '';
        }

        if (node.type === 'label') {
          el.innerHTML =
            '<h3 class="vc-label-text" data-title>' +
            escapeHtml(node.title || 'Label') +
            '</h3>';
          return;
        }

        if (node.type === 'note') {
          el.innerHTML =
            '<h3 class="vc-sticky-title" data-title>' +
            escapeHtml(node.title || 'Note') +
            '</h3>' +
            '<p class="vc-sticky-body">' +
            escapeHtml(node.note || '') +
            '</p>';
          return;
        }

        if (node.type === 'zone') {
          el.innerHTML =
            '<button type="button" class="vc-node-collapse" data-collapse title="Collapse zone contents">' +
            (node.collapsed ? '▸' : '▾') +
            '</button>' +
            '<span class="vc-zone-title" data-title>' +
            escapeHtml(node.title || 'Zone') +
            '</span>' +
            (node.collapsed
              ? '<span class="vc-zone-collapsed-hint">collapsed</span>'
              : '') +
            (node.collapsed
              ? ''
              : '<button type="button" class="vc-zone-resize" data-resize title="Resize" aria-label="Resize zone"></button>');
          return;
        }

        if (node.type === 'section') {
          var sectionOpen = !!node.sectionOpen;
          el.innerHTML =
            '<button type="button" class="vc-node-collapse" data-collapse title="Collapse section">' +
            (node.collapsed ? '▸' : '▾') +
            '</button>' +
            '<div class="vc-section-inner">' +
            '<span class="vc-tree-badge vc-tree-badge-section">Section</span>' +
            '<span class="vc-section-kind">' +
            (sectionOpen ? 'Abierta · primer nodo' : 'Bloqueada · completa el previo') +
            '</span>' +
            '<h3 class="vc-section-title" data-title>' +
            escapeHtml(node.title || 'Section') +
            '</h3>' +
            '</div>';
          return;
        }

        if (node.type === 'skill' || node.type === 'project') {
          var isIntro = !!node.isRoot && node.type === 'skill';
          var isProject = node.type === 'project';
          var stateLabel = isIntro
            ? 'etiqueta de inicio'
            : node.skillState === 'locked'
              ? 'locked'
              : node.skillState === 'available'
                ? 'available'
                : node.skillState === 'learning'
                  ? 'learning'
                  : 'mastered';
          var roleBadge = isIntro ? 'Intro' : isProject ? 'Project' : 'Node';
          var roleClass = isIntro
            ? 'vc-tree-badge-intro'
            : isProject
              ? 'vc-tree-badge-project'
              : 'vc-tree-badge-node';
          var coreIcon = isIntro
            ? '<i class="bi bi-diamond-fill"></i>'
            : isProject
              ? '<i class="bi bi-box-fill"></i>'
              : skillGlyph(node.skillState || 'available');
          el.innerHTML =
            (trainMode
              ? ''
              : '<button type="button" class="vc-node-collapse" data-collapse title="Collapse branch">' +
                (node.collapsed ? '▸' : '▾') +
                '</button>') +
            '<span class="vc-tree-badge ' +
            roleClass +
            '">' +
            roleBadge +
            '</span>' +
            '<div class="vc-skill-orb' +
            (isProject ? ' vc-project-orb' : '') +
            '" data-skill-orb>' +
            (isIntro
              ? '<div class="vc-skill-core">' + coreIcon + '</div>'
              : skillRingSvg(node.progress) +
                '<div class="vc-skill-core">' +
                coreIcon +
                '</div>') +
            '</div>' +
            '<h3 class="vc-node-title vc-skill-title" data-title>' +
            escapeHtml(node.title || 'Untitled') +
            '</h3>' +
            '<span class="vc-skill-state">' +
            escapeHtml(stateLabel) +
            '</span>';
          return;
        }

        var noteHtml = node.note
          ? '<p class="vc-node-blurb">' + escapeHtml(node.note) + '</p>'
          : '';

        el.innerHTML =
          '<button type="button" class="vc-node-collapse" data-collapse title="Collapse branch">' +
          (node.collapsed ? '▸' : '▾') +
          '</button>' +
          '<div class="vc-node-inner">' +
          compactRingSvg(node.progress) +
          '<div class="vc-node-body">' +
          '<div class="vc-node-meta">' +
          '<span class="vc-node-kind">' +
          escapeHtml(node.type) +
          '</span>' +
          (node.date ? '<span class="vc-node-date">' + escapeHtml(prettyMonth(node.date)) + '</span>' : '') +
          '</div>' +
          '<h3 class="vc-node-title" data-title>' +
          escapeHtml(node.title || 'Untitled') +
          '</h3>' +
          noteHtml +
          '</div></div>';
      });

      Object.keys(existing).forEach(function (id) {
        if (!seen[id]) {
          existing[id].remove();
        }
      });
    }

    function escapeHtml(str) {
      return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
    }

    function nodeCenter(node) {
      var size = nodeSize(node);
      if (isTreeOrbType(node.type)) {
        // Badge (~18px) + half orb (36px)
        var orbCenterY = 18 + 36;
        return { x: node.x + size.w / 2, y: node.y + orbCenterY };
      }
      return { x: node.x + size.w / 2, y: node.y + size.h / 2 };
    }

    function edgePath(a, b, fromNode, toNode) {
      var ca = nodeCenter(a);
      var cb = nodeCenter(b);
      var skillLink =
        (isTreeOrbType(fromNode.type) || fromNode.type === 'section') &&
        (isTreeOrbType(toNode.type) || toNode.type === 'section');
      if (skillLink) {
        var midX = ca.x + (cb.x - ca.x) * 0.55;
        return (
          'M ' +
          ca.x +
          ' ' +
          ca.y +
          ' L ' +
          midX +
          ' ' +
          ca.y +
          ' L ' +
          midX +
          ' ' +
          cb.y +
          ' L ' +
          cb.x +
          ' ' +
          cb.y
        );
      }
      var dx = cb.x - ca.x;
      var midQX = (ca.x + cb.x) / 2;
      var midQY = (ca.y + cb.y) / 2 - Math.min(80, Math.abs(dx) * 0.15);
      return 'M ' + ca.x + ' ' + ca.y + ' Q ' + midQX + ' ' + midQY + ' ' + cb.x + ' ' + cb.y;
    }

    function renderEdges() {
      var byId = {};
      scene.nodes.forEach(function (n) {
        byId[n.id] = n;
      });
      var hidden = hiddenIds();
      var focused = selectedId && focusMode ? focusSet(selectedId) : null;
      var pathSet = pathHighlightSet();
      var html = '';
      scene.edges.forEach(function (edge) {
        var a = byId[edge.from];
        var b = byId[edge.to];
        if (!a || !b || hidden[edge.from] || hidden[edge.to]) {
          return;
        }
        var strength = clamp(edge.strength || 1, 1, 3);
        var width = 1.4 + strength * 0.85;
        var opacity = 0.4 + strength * 0.2;
        if (focused && !(focused[edge.from] && focused[edge.to])) {
          opacity *= 0.2;
        }
        var skillLink =
          (isTreeOrbType(a.type) || a.type === 'section') &&
          (isTreeOrbType(b.type) || b.type === 'section');
        var bothAllocated =
          trainMode &&
          ((isTreeOrbType(a.type) &&
            isTreeOrbType(b.type) &&
            ((isIntroLabel(a) && isAllocatedSkill(b)) ||
              (isIntroLabel(b) && isAllocatedSkill(a)) ||
              (isAllocatedSkill(a) && isAllocatedSkill(b)))) ||
            (a.type === 'section' && a.sectionOpen && isAllocatedSkill(b)) ||
            (b.type === 'section' && b.sectionOpen && isAllocatedSkill(a)));
        var sectionGateDim =
          trainMode &&
          ((a.type === 'section' && !a.sectionOpen) ||
            (b.type === 'section' && !b.sectionOpen));
        if (
          trainMode &&
          skillLink &&
          (sectionGateDim || (!bothAllocated && !edgeOnSkillPath(edge, pathSet, byId)))
        ) {
          opacity *= 0.28;
        }
        if (edgeOnSkillPath(edge, pathSet, byId) || bothAllocated) {
          opacity = Math.max(opacity, 0.85);
          width += 0.8;
        }
        var cls =
          'vc-edge-' +
          (edge.kind || 'supports') +
          (skillLink ? ' vc-edge-skill' : '') +
          (selectedEdgeId === edge.id ? ' vc-edge-selected' : '') +
          (edgeOnSkillPath(edge, pathSet, byId) ? ' vc-edge-path' : '') +
          (bothAllocated ? ' vc-edge-allocated' : '');
        html +=
          '<path data-edge-id="' +
          edge.id +
          '" class="' +
          cls +
          '" d="' +
          edgePath(a, b, a, b) +
          '" stroke-width="' +
          width +
          '" opacity="' +
          opacity +
          '"></path>';
      });
      edgesSvg.innerHTML = html;
    }

    function trackY() {
      return timelineY() + 46;
    }

    function renderTimelineAnchors() {
      var tops = {};
      scene.nodes.forEach(function (node) {
        if (!isTimelineAnchored(node) || !node.date) {
          return;
        }
        var current = tops[node.date];
        if (!current || node.y < current.y) {
          tops[node.date] = node;
        }
      });

      var dates = Object.keys(tops);
      if (!dates.length) {
        return;
      }

      var html =
        '<defs>' +
        '<marker id="vcTimelineArrow" viewBox="0 0 10 10" refX="5" refY="5" ' +
        'markerWidth="7" markerHeight="7" orient="auto-start-reverse">' +
        '<path class="vc-timeline-arrow-head" d="M 0 0 L 10 5 L 0 10 z"></path>' +
        '</marker>' +
        '</defs>';

      dates.forEach(function (date) {
        var node = tops[date];
        var s = nodeSize(node);
        var x = node.x + s.w / 2;
        var y0 = trackY();
        var y1 = node.y - 2;
        if (y1 <= y0 + 8) {
          y1 = y0 + 8;
        }
        html +=
          '<circle class="vc-timeline-anchor-dot" cx="' +
          x +
          '" cy="' +
          y0 +
          '" r="4.5"></circle>' +
          '<path class="vc-timeline-anchor-line" marker-end="url(#vcTimelineArrow)" d="M ' +
          x +
          ' ' +
          y0 +
          ' L ' +
          x +
          ' ' +
          y1 +
          '"></path>';
      });

      edgesSvg.insertAdjacentHTML('beforeend', html);
    }

    function renderStackLinks() {
      var stacks = scene.timelineStacks || [];
      if (!stacks.length) {
        return;
      }
      var byId = {};
      scene.nodes.forEach(function (n) {
        byId[n.id] = n;
      });
      var html = '';
      stacks.forEach(function (ids) {
        for (var i = 0; i < ids.length - 1; i++) {
          var a = byId[ids[i]];
          var b = byId[ids[i + 1]];
          if (!a || !b) {
            continue;
          }
          var sa = nodeSize(a);
          var sb = nodeSize(b);
          var x1 = a.x + sa.w / 2;
          var y1 = a.y + sa.h;
          var x2 = b.x + sb.w / 2;
          var y2 = b.y;
          var midY = (y1 + y2) / 2;
          html +=
            '<path class="vc-edge-stack" d="M ' +
            x1 +
            ' ' +
            y1 +
            ' L ' +
            x1 +
            ' ' +
            midY +
            ' L ' +
            x2 +
            ' ' +
            midY +
            ' L ' +
            x2 +
            ' ' +
            y2 +
            '"></path>' +
            '<circle class="vc-stack-joint" cx="' +
            x1 +
            '" cy="' +
            y1 +
            '" r="3"></circle>' +
            '<circle class="vc-stack-joint" cx="' +
            x2 +
            '" cy="' +
            y2 +
            '" r="3"></circle>';
        }
      });
      if (html) {
        edgesSvg.insertAdjacentHTML('beforeend', html);
      }
    }

    function renderAll() {
      syncSkillAvailability();
      renderTimeline();
      layoutTimelineStacks();
      renderNodes();
      renderEdges();
      renderTimelineAnchors();
      renderStackLinks();
      renderBookmarks();
      updateInspector();
      updateMinimap();
      updateSkillPointsUi();
    }

    function findNode(id) {
      for (var i = 0; i < scene.nodes.length; i++) {
        if (scene.nodes[i].id === id) {
          return scene.nodes[i];
        }
      }
      return null;
    }

    function findEdge(id) {
      for (var i = 0; i < scene.edges.length; i++) {
        if (scene.edges[i].id === id) {
          return scene.edges[i];
        }
      }
      return null;
    }

    function isIntroLabel(node) {
      return !!(node && node.type === 'skill' && node.isRoot);
    }

    function isPlayableOrb(node) {
      return (
        !!node &&
        isTreeOrbType(node.type) &&
        !node.isRoot &&
        (node.skillState === 'learning' ||
          node.skillState === 'mastered' ||
          node.skillState === 'available' ||
          node.skillState === 'locked')
      );
    }

    function isAllocatedSkill(node) {
      return (
        !!node &&
        isTreeOrbType(node.type) &&
        !node.isRoot &&
        (node.skillState === 'learning' || node.skillState === 'mastered')
      );
    }

    function isPathSeed(node) {
      return isIntroLabel(node) || isAllocatedSkill(node);
    }

    function isGraphOrb(node) {
      return !!(node && isTreeOrbType(node.type));
    }

    function spentSkillPoints() {
      var n = 0;
      scene.nodes.forEach(function (node) {
        if (isAllocatedSkill(node)) {
          n += 1;
        }
      });
      return n;
    }

    function remainingSkillPoints() {
      return Math.max(0, (scene.skillPoints || 0) - spentSkillPoints());
    }

    function updateSkillPointsUi() {
      var wrap = document.getElementById('vcSkillPoints');
      var label = document.getElementById('vcSkillPointsLabel');
      if (!wrap || !label) {
        return;
      }
      wrap.hidden = !trainMode;
      label.textContent = remainingSkillPoints() + ' / ' + (scene.skillPoints || 0);
    }

    function buildSkillGraph() {
      var byId = {};
      var adj = {};
      scene.nodes.forEach(function (n) {
        byId[n.id] = n;
        if (isTreeOrbType(n.type)) {
          adj[n.id] = {};
        }
      });

      function addSkillLink(a, b) {
        if (!adj[a] || !adj[b] || a === b) {
          return;
        }
        adj[a][b] = true;
        adj[b][a] = true;
      }

      // Direct orb ↔ orb edges
      scene.edges.forEach(function (e) {
        var a = byId[e.from];
        var b = byId[e.to];
        if (!a || !b) {
          return;
        }
        if (isTreeOrbType(a.type) && isTreeOrbType(b.type)) {
          addSkillLink(a.id, b.id);
        }
      });

      // Sections: path bridge only prerequisite ↔ children (not child↔child)
      scene.nodes.forEach(function (sec) {
        if (sec.type !== 'section') {
          return;
        }
        var links = getSectionLinks(sec, byId);
        links.prereqs.forEach(function (p) {
          links.children.forEach(function (c) {
            addSkillLink(p.id, c.id);
          });
        });
      });

      var neighbors = {};
      Object.keys(adj).forEach(function (id) {
        neighbors[id] = Object.keys(adj[id]);
      });
      return { byId: byId, neighbors: neighbors };
    }

    function getSectionLinks(section, byId) {
      byId = byId || {};
      if (!Object.keys(byId).length) {
        scene.nodes.forEach(function (n) {
          byId[n.id] = n;
        });
      }
      var prereqs = [];
      var children = [];
      var seenP = {};
      var seenC = {};
      scene.edges.forEach(function (e) {
        var a = byId[e.from];
        var b = byId[e.to];
        if (!a || !b) {
          return;
        }
        if (a.id === section.id && isTreeOrbType(b.type)) {
          if (!seenC[b.id]) {
            seenC[b.id] = true;
            children.push(b);
          }
        } else if (b.id === section.id && isTreeOrbType(a.type)) {
          if (!seenP[a.id]) {
            seenP[a.id] = true;
            prereqs.push(a);
          }
        }
      });
      children.sort(function (a, b) {
        if (a.y !== b.y) {
          return a.y - b.y;
        }
        return a.x - b.x;
      });
      return {
        prereqs: prereqs,
        children: children,
        entry: children.length ? children[0] : null
      };
    }

    function syncSkillAvailability() {
      var byId = {};
      scene.nodes.forEach(function (n) {
        byId[n.id] = n;
      });

      var orbs = scene.nodes.filter(function (n) {
        return isTreeOrbType(n.type);
      });

      orbs.forEach(function (s) {
        if (s.isRoot && s.type === 'skill') {
          s.skillState = 'intro';
          s.progress = 0;
        }
      });

      // Direct adjacency only (no section mesh) for unlock spread
      var direct = {};
      orbs.forEach(function (o) {
        direct[o.id] = {};
      });
      scene.edges.forEach(function (e) {
        var a = byId[e.from];
        var b = byId[e.to];
        if (!a || !b) {
          return;
        }
        if (isTreeOrbType(a.type) && isTreeOrbType(b.type)) {
          direct[a.id][b.id] = true;
          direct[b.id][a.id] = true;
        }
      });

      var touchable = {};
      orbs.forEach(function (s) {
        if (isIntroLabel(s)) {
          Object.keys(direct[s.id] || {}).forEach(function (nid) {
            touchable[nid] = true;
          });
        }
        if (isAllocatedSkill(s)) {
          Object.keys(direct[s.id] || {}).forEach(function (nid) {
            touchable[nid] = true;
          });
        }
      });

      // Section gate: prereq mastered (or Intro) → unlock section + first child only.
      // Further children unlock only when the previous child in the section is allocated,
      // or via direct orb↔orb edges.
      scene.nodes.forEach(function (sec) {
        if (sec.type !== 'section') {
          return;
        }
        var links = getSectionLinks(sec, byId);
        var open = links.prereqs.some(function (p) {
          return isIntroLabel(p) || (p && p.skillState === 'mastered');
        });
        sec.sectionOpen = open;
        if (!open) {
          return;
        }
        if (links.entry && !isIntroLabel(links.entry)) {
          touchable[links.entry.id] = true;
        }
        for (var i = 0; i < links.children.length - 1; i++) {
          if (isAllocatedSkill(links.children[i])) {
            touchable[links.children[i + 1].id] = true;
          }
        }
      });

      orbs.forEach(function (s) {
        if ((s.isRoot && s.type === 'skill') || isAllocatedSkill(s)) {
          return;
        }
        if (touchable[s.id]) {
          s.skillState = 'available';
        } else {
          s.skillState = 'locked';
          if (!s.progress || s.progress < 0.05) {
            s.progress = 0;
          }
        }
      });
    }

    function shortestSkillPath(fromIds, toId) {
      var graph = buildSkillGraph();
      var queue = [];
      var prev = {};
      var seen = {};
      fromIds.forEach(function (id) {
        queue.push(id);
        seen[id] = true;
        prev[id] = null;
      });
      var found = false;
      while (queue.length) {
        var cur = queue.shift();
        if (cur === toId) {
          found = true;
          break;
        }
        (graph.neighbors[cur] || []).forEach(function (nid) {
          if (!seen[nid]) {
            seen[nid] = true;
            prev[nid] = cur;
            queue.push(nid);
          }
        });
      }
      if (!found) {
        return [];
      }
      var path = [];
      var walk = toId;
      while (walk) {
        path.push(walk);
        walk = prev[walk];
      }
      path.reverse();
      return path;
    }

    function pathHighlightSet() {
      if (!trainMode || !hoverSkillId) {
        return null;
      }
      var hover = findNode(hoverSkillId);
      if (!hover || !isTreeOrbType(hover.type) || hover.isRoot) {
        return null;
      }
      var starts = [];
      scene.nodes.forEach(function (n) {
        if (isPathSeed(n)) {
          starts.push(n.id);
        }
      });
      if (!starts.length) {
        return null;
      }
      var path = shortestSkillPath(starts, hoverSkillId);
      if (!path.length) {
        return null;
      }
      var set = {};
      path.forEach(function (id) {
        set[id] = true;
      });
      return set;
    }

    function edgeOnSkillPath(edge, pathSet, byId) {
      if (!pathSet) {
        return false;
      }
      var a = byId[edge.from];
      var b = byId[edge.to];
      if (!a || !b) {
        return false;
      }
      if (isTreeOrbType(a.type) && isTreeOrbType(b.type)) {
        return pathSet[a.id] && pathSet[b.id];
      }
      if (isTreeOrbType(a.type) && b.type === 'section') {
        return pathSet[a.id];
      }
      if (a.type === 'section' && isTreeOrbType(b.type)) {
        return pathSet[b.id];
      }
      return false;
    }

    function canRefundSkill(node) {
      if (!isAllocatedSkill(node)) {
        return false;
      }
      var saved = node.skillState;
      var savedProgress = node.progress;
      node.skillState = 'available';
      node.progress = 0;

      var roots = [];
      scene.nodes.forEach(function (n) {
        if (n.type === 'skill' && n.isRoot) {
          roots.push(n.id);
        }
      });
      if (!roots.length) {
        scene.nodes.forEach(function (n) {
          if (isAllocatedSkill(n)) {
            roots.push(n.id);
          }
        });
      }

      var stranded = false;
      scene.nodes.forEach(function (other) {
        if (stranded || !isAllocatedSkill(other)) {
          return;
        }
        var path = shortestSkillPath(roots, other.id);
        if (!path.length) {
          stranded = true;
          return;
        }
        for (var i = 0; i < path.length - 1; i++) {
          var step = findNode(path[i]);
          if (!step || !isPathSeed(step)) {
            stranded = true;
            break;
          }
        }
      });

      node.skillState = saved;
      node.progress = savedProgress;
      return !stranded;
    }

    function clearPendingTrainAction() {
      if (pendingTrainAction) {
        clearTimeout(pendingTrainAction);
        pendingTrainAction = null;
      }
    }

    function resetTrainTap() {
      clearPendingTrainAction();
      lastTrainTap = { id: null, time: 0 };
    }

    function setSkillStateDirect(node, state, opts) {
      opts = opts || {};
      syncSkillAvailability();
      if (!isTreeOrbType(node.type)) {
        return false;
      }
      if (isIntroLabel(node)) {
        showHint('Intro es solo la etiqueta de inicio — no se aprende.');
        selectNode(node.id);
        return false;
      }
      var allowed = {
        locked: true,
        available: true,
        learning: true,
        mastered: true
      };
      if (!allowed[state]) {
        return false;
      }

      var wasAllocated = isAllocatedSkill(node);
      var willAllocate = state === 'learning' || state === 'mastered';
      if (willAllocate && !wasAllocated && remainingSkillPoints() <= 0) {
        showHint('No skill points left. Refund a node (Shift+click) or raise the point pool.');
        return false;
      }
      if (
        wasAllocated &&
        !willAllocate &&
        !opts.force &&
        !canRefundSkill(node)
      ) {
        showHint('Cannot change — other allocated skills depend on this path.');
        return false;
      }

      node.skillState = state;
      if (state === 'mastered') {
        node.progress = 1;
      } else if (state === 'learning') {
        node.progress = Math.max(node.progress || 0, 0.4);
      } else {
        node.progress = 0;
      }
      syncSkillAvailability();
      selectNode(node.id);
      scheduleSave();
      return true;
    }

    function completeSkill(node) {
      if (!isTreeOrbType(node.type)) {
        return;
      }
      if (isIntroLabel(node)) {
        showHint('Intro es solo la etiqueta de inicio — no se aprende.');
        selectNode(node.id);
        return;
      }
      syncSkillAvailability();
      if (node.skillState === 'locked') {
        showHint('Locked — desbloquéalo antes (vecino asignado / Section abierta) o usa clic derecho.');
        selectNode(node.id);
        return;
      }
      if (node.skillState === 'mastered') {
        showHint('Ya está completado.');
        selectNode(node.id);
        return;
      }
      if (setSkillStateDirect(node, 'mastered')) {
        showHint('Completado: “' + (node.title || 'skill') + '”.');
      }
    }

    function allocateOrProgressSkill(node, refund) {
      syncSkillAvailability();
      if (!isTreeOrbType(node.type)) {
        return;
      }
      if (isIntroLabel(node)) {
        showHint('Intro es solo la etiqueta de inicio — no se aprende. Asigna un Node o Project conectado.');
        selectNode(node.id);
        return;
      }
      if (refund) {
        if (!isAllocatedSkill(node)) {
          showHint('Nothing to refund.');
          return;
        }
        if (!canRefundSkill(node)) {
          showHint('Cannot refund — other allocated skills depend on this path.');
          return;
        }
        node.skillState = 'available';
        node.progress = 0;
        syncSkillAvailability();
        showHint('Refunded: ' + (node.title || 'skill') + ' (+1 point)');
        renderAll();
        scheduleSave();
        return;
      }

      if (node.skillState === 'locked') {
        showHint('Locked — asigna un Node vecino (o conecta este a la Intro) para abrirlo.');
        return;
      }
      if (node.skillState === 'available') {
        if (remainingSkillPoints() <= 0) {
          showHint('No skill points left. Refund a node (Shift+click) or raise the point pool.');
          return;
        }
        node.skillState = 'learning';
        node.progress = Math.max(node.progress || 0, 0.4);
        showHint('Allocated “' + (node.title || 'skill') + '”. Doble clic = completado.');
      } else if (node.skillState === 'learning') {
        node.skillState = 'mastered';
        node.progress = 1;
        showHint('Mastered “' + (node.title || 'skill') + '”.');
      } else if (node.skillState === 'mastered') {
        showHint('Already mastered. Shift+click to refund.');
        selectNode(node.id);
        return;
      }
      syncSkillAvailability();
      selectNode(node.id);
      scheduleSave();
    }

    function hideSkillStateMenu() {
      var menu = document.getElementById('vcSkillMenu');
      if (menu) {
        menu.hidden = true;
      }
      skillMenuNodeId = null;
    }

    function openSkillStateMenu(node, clientX, clientY) {
      var menu = document.getElementById('vcSkillMenu');
      if (!menu || !node) {
        return;
      }
      skillMenuNodeId = node.id;
      selectNode(node.id);
      var title = document.getElementById('vcSkillMenuTitle');
      if (title) {
        title.textContent = node.title || 'Estado';
      }
      Array.prototype.forEach.call(menu.querySelectorAll('[data-skill-state]'), function (btn) {
        btn.classList.toggle('is-current', btn.dataset.skillState === node.skillState);
      });
      menu.hidden = false;
      var pad = 8;
      var w = menu.offsetWidth || 168;
      var h = menu.offsetHeight || 180;
      var x = Math.min(clientX, window.innerWidth - w - pad);
      var y = Math.min(clientY, window.innerHeight - h - pad);
      menu.style.left = Math.max(pad, x) + 'px';
      menu.style.top = Math.max(pad, y) + 'px';
    }

    function setCanvasMode(mode) {
      trainMode = mode === 'train';
      connectMode = false;
      connectFrom = null;
      resetTrainTap();
      hideSkillStateMenu();
      var connectBtn = document.getElementById('vcConnectBtn');
      if (connectBtn) {
        connectBtn.classList.remove('active');
      }
      host.classList.toggle('is-training', trainMode);
      host.classList.remove('is-connecting');
      var editBtn = document.getElementById('vcModeEdit');
      var trainBtn = document.getElementById('vcModeTrain');
      if (editBtn) {
        editBtn.classList.toggle('active', !trainMode);
      }
      if (trainBtn) {
        trainBtn.classList.toggle('active', trainMode);
      }
      var rail = document.querySelector('.vc-rail');
      if (rail) {
        rail.classList.toggle('is-train-locked', trainMode);
      }
      if (trainMode) {
        syncSkillAvailability();
        showHint('Train: clic = progreso · doble clic = completado · clic derecho = estado · Shift+clic = refund.');
      } else {
        hideHint();
      }
      renderAll();
    }

    function selectNode(id) {
      selectedId = id;
      selectedEdgeId = null;
      renderAll();
    }

    function updateInspector() {
      if (!inspector) {
        return;
      }
      var node = selectedId ? findNode(selectedId) : null;
      if (!node) {
        inspector.hidden = true;
        return;
      }
      inspector.hidden = false;
      var typeLabel =
        node.type === 'skill'
          ? node.isRoot
            ? 'Intro — inicio del árbol'
            : 'Node — bola asignable'
          : node.type === 'project'
            ? 'Project — nodo asignable'
            : node.type === 'section'
              ? 'Section — hub de ramas'
              : node.type;
      document.getElementById('vcInspectorType').textContent = typeLabel;
      document.getElementById('vcInspectorTitle').value = node.title || '';
      document.getElementById('vcInspectorNote').value = node.note || '';
      var strategyFields = document.getElementById('vcStrategyFields');
      var annotationFields = document.getElementById('vcAnnotationFields');
      var zoneSizeWrap = document.getElementById('vcZoneSizeWrap');
      var collapseBtn = document.getElementById('vcCollapseBtn');
      if (staysOffTimeline(node.type)) {
        if (strategyFields) {
          strategyFields.hidden = true;
        }
        if (annotationFields) {
          annotationFields.hidden = false;
        }
        var colorEl = document.getElementById('vcInspectorColor');
        if (colorEl) {
          colorEl.value = normalizeColor(node.color);
        }
        if (zoneSizeWrap) {
          zoneSizeWrap.hidden = node.type !== 'zone';
        }
        if (node.type === 'zone') {
          var wEl = document.getElementById('vcInspectorWidth');
          var hEl = document.getElementById('vcInspectorHeight');
          var dims = nodeSize(node);
          if (wEl) {
            wEl.value = dims.w;
          }
          if (hEl) {
            hEl.value = dims.h;
          }
        }
        if (collapseBtn) {
          collapseBtn.hidden = !(
            node.type === 'section' ||
            node.type === 'zone' ||
            node.type === 'skill'
          );
          collapseBtn.textContent =
            node.type === 'zone'
              ? node.collapsed
                ? 'Expand zone'
                : 'Collapse zone'
              : node.collapsed
                ? 'Expand section'
                : 'Collapse section';
        }
      } else {
        if (strategyFields) {
          strategyFields.hidden = false;
        }
        if (annotationFields) {
          annotationFields.hidden = true;
        }
        if (collapseBtn) {
          collapseBtn.hidden = false;
          collapseBtn.textContent = 'Collapse branch';
        }
        document.getElementById('vcInspectorDate').value = node.date || '';
        document.getElementById('vcInspectorProgress').value = Math.round(
          (node.progress || 0) * 100
        );
        var skillWrap = document.getElementById('vcSkillStateWrap');
        if (node.type === 'skill' || node.type === 'project') {
          skillWrap.hidden = false;
          var rootEl = document.getElementById('vcInspectorIsRoot');
          if (rootEl) {
            rootEl.checked = !!node.isRoot;
            rootEl.disabled = node.type === 'project';
            var rootLabel = rootEl.closest('label');
            if (rootLabel) {
              rootLabel.hidden = node.type === 'project';
            }
          }
          var stateEl = document.getElementById('vcInspectorSkillState');
          var progressEl = document.getElementById('vcInspectorProgress');
          if (node.isRoot && node.type === 'skill') {
            if (stateEl) {
              stateEl.disabled = true;
              stateEl.value = 'available';
            }
            if (progressEl) {
              progressEl.disabled = true;
            }
          } else {
            if (stateEl) {
              stateEl.disabled = false;
              stateEl.value = node.skillState || 'available';
            }
            if (progressEl) {
              progressEl.disabled = false;
            }
          }
        } else {
          skillWrap.hidden = true;
        }
      }
    }

    function clientToWorld(clientX, clientY) {
      var rect = host.getBoundingClientRect();
      var z = scene.viewport.zoom;
      return {
        x: (clientX - rect.left - scene.viewport.x) / z,
        y: (clientY - rect.top - scene.viewport.y) / z
      };
    }

    function linkTreeNodes(fromId, toId) {
      if (!fromId || !toId || fromId === toId) {
        return false;
      }
      var exists = scene.edges.some(function (ed) {
        return ed.from === fromId && ed.to === toId;
      });
      if (exists) {
        return false;
      }
      var fromN = findNode(fromId);
      var toN = findNode(toId);
      scene.edges.push({
        id: uid('e'),
        from: fromId,
        to: toId,
        kind:
          fromN &&
          toN &&
          ((isTreeOrbType(fromN.type) && isTreeOrbType(toN.type)) ||
            fromN.type === 'section' ||
            toN.type === 'section')
            ? 'unlocks'
            : 'supports',
        strength: 2
      });
      return true;
    }

    function showHint(text) {
      var bar = document.getElementById('vcHintBar');
      var textEl = document.getElementById('vcHintText');
      if (!bar || !textEl) {
        return;
      }
      textEl.textContent = text;
      bar.hidden = false;
    }

    function hideHint() {
      var bar = document.getElementById('vcHintBar');
      if (bar) {
        bar.hidden = true;
      }
    }

    function updateConnectHint() {
      if (!connectMode) {
        hideHint();
        return;
      }
      if (connectFrom) {
        var n = findNode(connectFrom);
        showHint(
          'Connect: now click the child (from “' +
            (n && n.title ? n.title : 'parent') +
            '”). Esc cancels.'
        );
      } else {
        showHint('Connect mode: click the parent first, then the child.');
      }
    }

    function addNode(type, worldPos, opts) {
      opts = opts || {};
      var treeRole = opts.treeRole || null;
      if (type === 'skill' && treeRole === 'intro') {
        opts.isRoot = true;
        if (!opts.title) {
          opts.title = 'Introduccion';
        }
        if (!opts.skillState) {
          opts.skillState = 'available';
        }
      } else if (type === 'skill' && treeRole === 'node') {
        opts.isRoot = false;
        if (!opts.title) {
          opts.title = 'Node';
        }
      } else if (type === 'section' && !opts.title) {
        opts.title = 'Section';
      }
      var titles = {
        vision: 'New vision',
        milestone: 'New milestone',
        skill: opts.isRoot ? 'Introduccion' : 'Node',
        section: 'Section',
        project: 'New project',
        habit: 'New habit',
        knowledge: 'New knowledge',
        label: 'Section title',
        note: 'Sticky note',
        zone: 'Area'
      };
      var size = nodeSize(type);
      var x = worldPos ? worldPos.x - size.w / 2 : 200 - scene.viewport.x / scene.viewport.zoom;
      var y = worldPos ? worldPos.y - size.h / 2 : 200 - scene.viewport.y / scene.viewport.zoom;
      var offTimeline = staysOffTimeline(type);
      var date = offTimeline ? null : opts.date || xToMonth(x + size.w / 2);
      if (!offTimeline && (livesOnTimeline(type) || opts.snapTimeline)) {
        var tx = monthToX(date);
        x = tx - size.w / 2;
        var minY = timelineY() + 68;
        y = Math.max(worldPos ? worldPos.y - size.h / 2 : minY, minY);
      }

      var parentForLink = null;
      if (
        opts.autoLink !== false &&
        selectedId &&
        (type === 'skill' || type === 'section' || type === 'project')
      ) {
        var selected = findNode(selectedId);
        if (
          selected &&
          (selected.type === 'skill' ||
            selected.type === 'section' ||
            selected.type === 'project') &&
          !worldPos
        ) {
          parentForLink = selected;
        } else if (
          selected &&
          (selected.type === 'skill' ||
            selected.type === 'section' ||
            selected.type === 'project') &&
          opts.fromRail
        ) {
          parentForLink = selected;
        }
      }
      if (parentForLink && opts.fromRail) {
        var ps = nodeSize(parentForLink);
        x = parentForLink.x + ps.w + 48;
        y = parentForLink.y + (type === 'section' ? -8 : 20);
      }

      var node = {
        id: uid(type.slice(0, 1)),
        type: type,
        title: opts.title || titles[type] || 'New',
        note: '',
        x: x,
        y: y,
        date: date,
        collapsed: false,
        progress: isTreeOrbType(type) ? 0 : offTimeline ? 0 : 0.1,
        anchoredToTimeline: !!(!offTimeline && (livesOnTimeline(type) || opts.snapTimeline))
      };
      if (type === 'skill') {
        var anySkill = scene.nodes.some(function (n) {
          return n.type === 'skill';
        });
        var forceRoot = opts.isRoot === true || treeRole === 'intro';
        var forceNode = treeRole === 'node' || opts.isRoot === false;
        node.isRoot = forceRoot || (!forceNode && !anySkill);
        if (node.isRoot) {
          node.skillState = 'intro';
          node.progress = 0;
        } else {
          node.skillState =
            opts.skillState || (anySkill ? 'locked' : 'available');
          if (opts.progress != null) {
            node.progress = opts.progress;
          }
        }
      }
      if (type === 'project') {
        node.isRoot = false;
        node.skillState = opts.skillState || 'locked';
        node.progress = opts.progress != null ? opts.progress : 0;
        node.note = 'Project node — se asigna en Train como un Node.';
      }
      if (hasAccentColor(type)) {
        node.color = normalizeColor(opts.color || (type === 'section' ? 'sky' : 'teal'));
        node.w = size.w;
        node.h = size.h;
        if (type === 'note') {
          node.note = 'Write a reminder…';
        }
        if (type === 'section') {
          node.note = 'Hub que junta ramas del árbol';
        }
      }
      scene.nodes.push(node);
      if (node.type === 'skill' && node.isRoot) {
        scene.nodes.forEach(function (n) {
          if (n.type === 'skill' && n.id !== node.id) {
            n.isRoot = false;
          }
        });
      }
      if (parentForLink && opts.fromRail) {
        linkTreeNodes(parentForLink.id, node.id);
      }
      selectNode(node.id);
      scheduleSave();
      return node;
    }

    function maybeSnapDate(node) {
      if (staysOffTimeline(node.type)) {
        node.anchoredToTimeline = false;
        return;
      }
      var size = nodeSize(node);
      var cy = node.y + size.h / 2;
      var band = timelineY() + 28;
      var force = livesOnTimeline(node.type);
      if (force || Math.abs(cy - band) < 140 || node.anchoredToTimeline) {
        // Still anchored if dragging within the stack column below the band
        var stillInStackZone = node.anchoredToTimeline && cy < timelineY() + 900;
        if (force || Math.abs(cy - band) < 140 || stillInStackZone) {
          var clampedX = clampTimelineX(node.x + size.w / 2);
          node.date = xToMonth(clampedX);
          node.anchoredToTimeline = true;
          var tx = monthToX(node.date);
          if (tx != null) {
            node.x = tx - size.w / 2;
          }
          var minY = timelineY() + 68;
          if (node.y < minY) {
            node.y = minY;
          }
          return;
        }
      }
      if (!livesOnTimeline(node.type)) {
        node.anchoredToTimeline = false;
      }
    }

    function openCreateModal(worldPos, clientPos, fromTimeline) {
      createDraft = {
        x: worldPos.x,
        y: worldPos.y,
        date: xToMonth(worldPos.x),
        fromTimeline: !!fromTimeline
      };
      if (!createModal) {
        addNode('milestone', worldPos, {
          date: createDraft.date,
          snapTimeline: fromTimeline
        });
        return;
      }
      createModal.hidden = false;
      if (createDateHint) {
        createDateHint.textContent = fromTimeline
          ? 'On the timeline · ' + prettyMonth(createDraft.date)
          : 'At this point · ' + prettyMonth(createDraft.date);
      }
      if (createTitle) {
        createTitle.value = '';
        createTitle.focus();
      }
      createModal.querySelectorAll('[data-create-type]').forEach(function (btn) {
        btn.classList.toggle('active', btn.dataset.createType === (fromTimeline ? 'milestone' : 'vision'));
      });
      createModal.style.setProperty('--vc-modal-x', Math.min(clientPos.x, window.innerWidth - 320) + 'px');
      createModal.style.setProperty('--vc-modal-y', Math.min(clientPos.y, window.innerHeight - 480) + 'px');
    }

    function closeCreateModal() {
      if (createModal) {
        createModal.hidden = true;
      }
      createDraft = null;
    }

    function confirmCreate(type, treeRole) {
      if (!createDraft) {
        return;
      }
      var title = createTitle && createTitle.value.trim() ? createTitle.value.trim() : null;
      addNode(type, { x: createDraft.x, y: createDraft.y }, {
        title: title,
        date: createDraft.date,
        treeRole: treeRole || null,
        snapTimeline: !staysOffTimeline(type) && (createDraft.fromTimeline || livesOnTimeline(type))
      });
      closeCreateModal();
    }

    function updateMinimap() {
      if (!minimapCanvas || !minimapViewport) {
        return;
      }
      var ctx = minimapCanvas.getContext('2d');
      var w = minimapCanvas.width;
      var h = minimapCanvas.height;
      ctx.clearRect(0, 0, w, h);
      ctx.fillStyle =
        document.documentElement.getAttribute('data-bs-theme') === 'dark'
          ? '#121826'
          : '#f8fafc';
      ctx.fillRect(0, 0, w, h);

      if (!scene.nodes.length) {
        minimapViewport.style.display = 'none';
        return;
      }

      var minX = Infinity;
      var minY = Infinity;
      var maxX = -Infinity;
      var maxY = -Infinity;
      scene.nodes.forEach(function (n) {
        var s = nodeSize(n);
        minX = Math.min(minX, n.x);
        minY = Math.min(minY, n.y);
        maxX = Math.max(maxX, n.x + s.w);
        maxY = Math.max(maxY, n.y + s.h);
      });
      var pad = 80;
      minX -= pad;
      minY -= pad;
      maxX += pad;
      maxY += pad;
      var bw = Math.max(maxX - minX, 1);
      var bh = Math.max(maxY - minY, 1);
      var scale = Math.min(w / bw, h / bh);

      // timeline hint
      ctx.fillStyle = 'rgba(15, 118, 110, 0.12)';
      ctx.fillRect(0, (timelineY() - minY) * scale, w, Math.max(2, 40 * scale));

      scene.nodes.forEach(function (n) {
        var s = nodeSize(n);
        if (n.type === 'zone') {
          ctx.fillStyle = 'rgba(100, 116, 139, 0.18)';
          ctx.fillRect(
            (n.x - minX) * scale,
            (n.y - minY) * scale,
            Math.max(4, s.w * scale),
            Math.max(3, s.h * scale)
          );
          return;
        }
        if (n.type === 'section') {
          ctx.fillStyle = '#0369a1';
          ctx.fillRect(
            (n.x - minX) * scale,
            (n.y - minY) * scale,
            Math.max(6, s.w * scale),
            Math.max(2, s.h * scale)
          );
          return;
        }
        if (n.type === 'label' || n.type === 'note') {
          ctx.fillStyle = n.type === 'note' ? '#f59e0b' : '#64748b';
          ctx.beginPath();
          ctx.arc(
            (n.x + s.w / 2 - minX) * scale,
            (n.y + s.h / 2 - minY) * scale,
            Math.max(2, 3.5),
            0,
            Math.PI * 2
          );
          ctx.fill();
          return;
        }
        if (n.type === 'skill') {
          ctx.fillStyle = '#0369a1';
          ctx.beginPath();
          ctx.arc(
            (n.x + s.w / 2 - minX) * scale,
            (n.y + 36 - minY) * scale,
            Math.max(2, 8 * scale),
            0,
            Math.PI * 2
          );
          ctx.fill();
          return;
        }
        if (n.type === 'project') {
          ctx.fillStyle = '#7c3aed';
          var pw = Math.max(4, s.w * scale * 0.55);
          var ph = Math.max(4, s.h * scale * 0.4);
          ctx.fillRect(
            (n.x + s.w / 2 - minX) * scale - pw / 2,
            (n.y + 36 - minY) * scale - ph / 2,
            pw,
            ph
          );
          return;
        }
        ctx.fillStyle =
          n.type === 'vision'
            ? '#0f766e'
            : n.type === 'milestone'
              ? '#0ea5e9'
              : '#64748b';
        ctx.fillRect(
          (n.x - minX) * scale,
          (n.y - minY) * scale,
          Math.max(3, s.w * scale),
          Math.max(2, s.h * scale)
        );
      });

      var rect = host.getBoundingClientRect();
      var z = scene.viewport.zoom;
      var viewWorldX = -scene.viewport.x / z;
      var viewWorldY = -scene.viewport.y / z;
      var viewWorldW = rect.width / z;
      var viewWorldH = rect.height / z;
      minimapViewport.style.display = 'block';
      minimapViewport.style.left = (viewWorldX - minX) * scale + 'px';
      minimapViewport.style.top = (viewWorldY - minY) * scale + 'px';
      minimapViewport.style.width = Math.max(12, viewWorldW * scale) + 'px';
      minimapViewport.style.height = Math.max(10, viewWorldH * scale) + 'px';

      minimapCanvas._bounds = { minX: minX, minY: minY, scale: scale };
    }

    function fitToContent() {
      if (!scene.nodes.length) {
        scene.viewport = { x: 40, y: 40, zoom: 1 };
        scheduleViewport();
        return;
      }
      var minX = Infinity;
      var minY = Infinity;
      var maxX = -Infinity;
      var maxY = -Infinity;
      scene.nodes.forEach(function (n) {
        var s = nodeSize(n);
        minX = Math.min(minX, n.x);
        minY = Math.min(minY, n.y);
        maxX = Math.max(maxX, n.x + s.w);
        maxY = Math.max(maxY, n.y + s.h);
      });
      var rect = host.getBoundingClientRect();
      var pad = 80;
      var bw = maxX - minX + pad * 2;
      var bh = maxY - minY + pad * 2;
      var zoom = clamp(Math.min(rect.width / bw, rect.height / bh), 0.35, 1.2);
      scene.viewport.zoom = zoom;
      scene.viewport.x = -minX * zoom + (rect.width - bw * zoom) / 2 + pad * zoom;
      scene.viewport.y = -minY * zoom + (rect.height - bh * zoom) / 2 + pad * zoom;
      scheduleViewport();
      scheduleSave();
    }

    function centerOnNode(node) {
      var rect = host.getBoundingClientRect();
      var s = nodeSize(node);
      var z = scene.viewport.zoom;
      scene.viewport.x = rect.width / 2 - (node.x + s.w / 2) * z;
      scene.viewport.y = rect.height / 2 - (node.y + s.h / 2) * z;
      scheduleViewport();
    }

    function hitTimeline(worldPt) {
      var y = timelineY();
      var b = timelineBounds();
      var inY = worldPt.y >= y - 28 && worldPt.y <= y + 70;
      var inX = worldPt.x >= b.minX && worldPt.x <= b.maxX;
      return inY && inX;
    }

    /* Events */
    host.addEventListener('wheel', function (e) {
      e.preventDefault();
      var rect = host.getBoundingClientRect();
      var mx = e.clientX - rect.left;
      var my = e.clientY - rect.top;
      var oldZ = scene.viewport.zoom;
      var factor = e.deltaY > 0 ? 0.92 : 1.08;
      var newZ = clamp(oldZ * factor, 0.25, 2.5);
      var wx = (mx - scene.viewport.x) / oldZ;
      var wy = (my - scene.viewport.y) / oldZ;
      scene.viewport.zoom = newZ;
      scene.viewport.x = mx - wx * newZ;
      scene.viewport.y = my - wy * newZ;
      scheduleViewport();
      scheduleSave();
    }, { passive: false });

    host.addEventListener('contextmenu', function (e) {
      e.preventDefault();
      if (e.target.closest && e.target.closest('.vc-create-modal, .vc-skill-menu')) {
        return;
      }
      var nodeEl = e.target.closest ? e.target.closest('.vc-node') : null;
      if (nodeEl) {
        var ctxNode = findNode(nodeEl.dataset.id);
        if (ctxNode && isTreeOrbType(ctxNode.type) && !isIntroLabel(ctxNode)) {
          openSkillStateMenu(ctxNode, e.clientX, e.clientY);
          return;
        }
        hideSkillStateMenu();
        return;
      }
      hideSkillStateMenu();
      if (e.target.closest && e.target.closest('path[data-edge-id]')) {
        return;
      }
      var worldPt = clientToWorld(e.clientX, e.clientY);
      var onTimeline = hitTimeline(worldPt);
      openCreateModal(worldPt, { x: e.clientX, y: e.clientY }, onTimeline);
    });

    host.addEventListener('mousemove', function (e) {
      if (isPanning || dragNode || resizeNode || timelineDragging) {
        return;
      }
      if (trainMode) {
        var over = e.target.closest
          ? e.target.closest('.vc-node-skill, .vc-node-project')
          : null;
        var nextHover = over ? over.dataset.id : null;
        if (nextHover !== hoverSkillId) {
          hoverSkillId = nextHover;
          renderNodes();
          renderEdges();
        }
      }
      var worldPt = clientToWorld(e.clientX, e.clientY);
      if (hitTimeline(worldPt)) {
        host.classList.add('is-on-timeline');
        updateScrubber(worldPt.x, true);
      } else {
        host.classList.remove('is-on-timeline');
        updateScrubber(0, false);
      }
    });

    host.addEventListener('mouseleave', function () {
      host.classList.remove('is-on-timeline');
      updateScrubber(0, false);
    });

    host.addEventListener('mousedown', function (e) {
      if (e.button === 1 || (e.button === 0 && (spaceDown || e.altKey))) {
        isPanning = true;
        host.classList.add('is-panning');
        panLast = { x: e.clientX, y: e.clientY };
        e.preventDefault();
        return;
      }
      if (e.button !== 0) {
        return;
      }

      var tickBtn = e.target.closest ? e.target.closest('.vc-timeline-tick') : null;
      if (tickBtn && (tickBtn.dataset.date || tickBtn.dataset.month)) {
        e.stopPropagation();
        centerOnMonth(tickBtn.dataset.date || tickBtn.dataset.month);
        return;
      }

      if (e.target.closest && e.target.closest('path[data-edge-id]')) {
        return;
      }

      var worldPt = clientToWorld(e.clientX, e.clientY);
      if (!e.target.closest('.vc-node') && hitTimeline(worldPt)) {
        timelineDragging = true;
        host.classList.add('is-panning');
        panLast = { x: e.clientX, y: e.clientY };
        updateScrubber(worldPt.x, true);
        e.preventDefault();
        return;
      }

      var nodeEl = e.target.closest ? e.target.closest('.vc-node') : null;
      if (!nodeEl) {
        hideSkillStateMenu();
        if (!connectMode) {
          selectedId = null;
          selectedEdgeId = null;
          renderAll();
        }
        isPanning = true;
        host.classList.add('is-panning');
        panLast = { x: e.clientX, y: e.clientY };
        return;
      }

      var id = nodeEl.dataset.id;
      if (trainMode) {
        hideSkillStateMenu();
        var trainNode = findNode(id);
        if (trainNode && isTreeOrbType(trainNode.type)) {
          // Detect double-tap on mousedown: click/dblclick never fire after
          // preventDefault + DOM re-render from selectNode/renderAll.
          if (e.shiftKey) {
            clearPendingTrainAction();
            lastTrainTap = { id: null, time: 0 };
            allocateOrProgressSkill(trainNode, true);
          } else {
            var now = Date.now();
            if (lastTrainTap.id === id && now - lastTrainTap.time < 420) {
              clearPendingTrainAction();
              lastTrainTap = { id: null, time: 0 };
              completeSkill(trainNode);
            } else {
              lastTrainTap = { id: id, time: now };
              clearPendingTrainAction();
              pendingTrainAction = setTimeout(function () {
                pendingTrainAction = null;
                var n = findNode(id);
                if (n) {
                  allocateOrProgressSkill(n, false);
                }
              }, 300);
            }
          }
          e.preventDefault();
          e.stopPropagation();
          return;
        }
        selectNode(id);
        return;
      }

      if (e.target.closest('[data-collapse]')) {
        var n = findNode(id);
        if (n) {
          n.collapsed = !n.collapsed;
          selectNode(id);
          scheduleSave();
        }
        e.stopPropagation();
        return;
      }

      if (e.target.closest('[data-resize]')) {
        var resizeTarget = findNode(id);
        if (resizeTarget && resizeTarget.type === 'zone') {
          selectNode(id);
          var rw = clientToWorld(e.clientX, e.clientY);
          resizeNode = resizeTarget;
          resizeStart = {
            x: rw.x,
            y: rw.y,
            w: resizeTarget.w || NODE_SIZES.zone.w,
            h: resizeTarget.h || NODE_SIZES.zone.h
          };
          e.preventDefault();
          e.stopPropagation();
        }
        return;
      }

      if (connectMode) {
        if (!connectFrom) {
          connectFrom = id;
          selectNode(id);
          updateConnectHint();
        } else if (connectFrom !== id) {
          linkTreeNodes(connectFrom, id);
          connectFrom = null;
          selectNode(id);
          updateConnectHint();
          scheduleSave();
        }
        return;
      }

      if (e.detail === 2 && e.target.closest('[data-title]')) {
        var titleEl = e.target.closest('[data-title]');
        titleEl.contentEditable = 'true';
        titleEl.focus();
        document.execCommand('selectAll', false, null);
        titleEl.addEventListener(
          'blur',
          function () {
            titleEl.contentEditable = 'false';
            var node = findNode(id);
            if (node) {
              node.title = titleEl.textContent.trim() || node.title;
              scheduleSave();
              updateInspector();
            }
          },
          { once: true }
        );
        titleEl.addEventListener('keydown', function (ev) {
          if (ev.key === 'Enter') {
            ev.preventDefault();
            titleEl.blur();
          }
        });
        return;
      }

      selectNode(id);
      var node = findNode(id);
      var wp = clientToWorld(e.clientX, e.clientY);
      dragNode = node;
      dragOffset = { x: wp.x - node.x, y: wp.y - node.y };
    });

    edgesSvg.addEventListener('mousedown', function (e) {
      var path = e.target.closest ? e.target.closest('path[data-edge-id]') : null;
      if (!path) {
        return;
      }
      e.stopPropagation();
      var edge = findEdge(path.getAttribute('data-edge-id'));
      if (!edge) {
        return;
      }
      selectedEdgeId = edge.id;
      selectedId = null;
      if (e.shiftKey || e.detail === 2) {
        scene.edges = scene.edges.filter(function (ed) {
          return ed.id !== edge.id;
        });
        selectedEdgeId = null;
      } else {
        edge.strength = edge.strength >= 3 ? 1 : (edge.strength || 1) + 1;
        var ki = EDGE_KINDS.indexOf(edge.kind || 'supports');
        if (edge.strength === 1) {
          edge.kind = EDGE_KINDS[(ki + 1) % EDGE_KINDS.length];
        }
      }
      renderAll();
      scheduleSave();
    });

    window.addEventListener('mousemove', function (e) {
      if ((isPanning || timelineDragging) && panLast) {
        scene.viewport.x += e.clientX - panLast.x;
        if (!timelineDragging) {
          scene.viewport.y += e.clientY - panLast.y;
        }
        panLast = { x: e.clientX, y: e.clientY };
        if (timelineDragging) {
          var wp = clientToWorld(e.clientX, e.clientY);
          updateScrubber(wp.x, true);
        }
        scheduleViewport();
        return;
      }
      if (resizeNode && resizeStart) {
        var rPt = clientToWorld(e.clientX, e.clientY);
        resizeNode.w = Math.max(120, Math.round(resizeStart.w + (rPt.x - resizeStart.x)));
        resizeNode.h = Math.max(80, Math.round(resizeStart.h + (rPt.y - resizeStart.y)));
        renderNodes();
        renderEdges();
        updateMinimap();
        updateInspector();
        return;
      }
      if (dragNode && dragOffset) {
        var worldPt = clientToWorld(e.clientX, e.clientY);
        dragNode.x = worldPt.x - dragOffset.x;
        dragNode.y = worldPt.y - dragOffset.y;
        maybeSnapDate(dragNode);
        layoutTimelineStacks();
        renderNodes();
        renderEdges();
        renderTimelineAnchors();
        renderStackLinks();
        updateMinimap();
        updateInspector();
      }
    });

    window.addEventListener('mouseup', function () {
      if (resizeNode) {
        renderAll();
        scheduleSave();
      }
      if (dragNode) {
        maybeSnapDate(dragNode);
        layoutTimelineStacks();
        renderAll();
        scheduleSave();
      }
      dragNode = null;
      dragOffset = null;
      resizeNode = null;
      resizeStart = null;
      if (isPanning || timelineDragging) {
        isPanning = false;
        timelineDragging = false;
        host.classList.remove('is-panning');
        scheduleSave();
      }
      panLast = null;
    });

    window.addEventListener('keydown', function (e) {
      if (e.code === 'Space' && !isTyping(e)) {
        spaceDown = true;
        e.preventDefault();
      }
      if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') {
        e.preventDefault();
        openSearch();
      }
      if (e.key === 'Escape') {
        closeSearch();
        closeCreateModal();
        connectMode = false;
        connectFrom = null;
        hideHint();
        var cBtn = document.getElementById('vcConnectBtn');
        if (cBtn) {
          cBtn.classList.remove('active');
        }
        host.classList.remove('is-connecting');
        var helpEl = document.getElementById('vcTreeHelp');
        if (helpEl) {
          helpEl.hidden = true;
        }
        renderAll();
      }
      if ((e.key === 'Delete' || e.key === 'Backspace') && selectedId && !isTyping(e)) {
        scene.edges = scene.edges.filter(function (ed) {
          return ed.from !== selectedId && ed.to !== selectedId;
        });
        scene.nodes = scene.nodes.filter(function (n) {
          return n.id !== selectedId;
        });
        selectedId = null;
        renderAll();
        scheduleSave();
      }
    });

    window.addEventListener('keyup', function (e) {
      if (e.code === 'Space') {
        spaceDown = false;
      }
    });

    function isTyping(e) {
      var t = e.target;
      return (
        t &&
        (t.tagName === 'INPUT' ||
          t.tagName === 'TEXTAREA' ||
          t.tagName === 'SELECT' ||
          t.isContentEditable)
      );
    }

    document.querySelectorAll('.vc-tool[data-add]').forEach(function (btn) {
      btn.addEventListener('click', function () {
        if (trainMode) {
          showHint('Pasa a Edit para construir. Train es para asignar bolas.');
          return;
        }
        var rect = host.getBoundingClientRect();
        var type = btn.dataset.add;
        var treeRole = btn.dataset.treeRole || null;
        addNode(type, clientToWorld(rect.left + rect.width / 2, rect.top + rect.height / 2), {
          snapTimeline: livesOnTimeline(type),
          fromRail: true,
          treeRole: treeRole
        });
        if (treeRole === 'intro') {
          showHint('Intro colocada (inicio). Conecta Nodes debajo, luego una Section.');
        } else if (type === 'project') {
          showHint('Project colocado — mismo path que un Node, otra apariencia.');
        } else if (treeRole === 'node' || type === 'skill') {
          showHint('Node colocado (bola). Conéctalo desde Intro u otro Node.');
        } else if (type === 'section') {
          showHint('Section colocada (hub). Conecta varios Nodes hacia ella.');
        }
      });
    });

    var connectBtn = document.getElementById('vcConnectBtn');
    if (connectBtn) {
      connectBtn.addEventListener('click', function () {
        if (trainMode) {
          showHint('Connect is only available in Edit mode.');
          return;
        }
        connectMode = !connectMode;
        connectFrom = null;
        connectBtn.classList.toggle('active', connectMode);
        host.classList.toggle('is-connecting', connectMode);
        updateConnectHint();
        renderAll();
      });
    }

    var modeEditBtn = document.getElementById('vcModeEdit');
    var modeTrainBtn = document.getElementById('vcModeTrain');
    if (modeEditBtn) {
      modeEditBtn.addEventListener('click', function () {
        setCanvasMode('edit');
      });
    }
    if (modeTrainBtn) {
      modeTrainBtn.addEventListener('click', function () {
        setCanvasMode('train');
      });
    }

    var treeHelpBtn = document.getElementById('vcTreeHelpBtn');
    var treeHelp = document.getElementById('vcTreeHelp');
    var treeHelpClose = document.getElementById('vcTreeHelpClose');
    var plantRootBtn = document.getElementById('vcPlantRootBtn');
    var hintClose = document.getElementById('vcHintClose');

    function openTreeHelp() {
      if (treeHelp) {
        treeHelp.hidden = false;
      }
    }
    function closeTreeHelp() {
      if (treeHelp) {
        treeHelp.hidden = true;
      }
    }
    if (treeHelpBtn) {
      treeHelpBtn.addEventListener('click', openTreeHelp);
    }
    if (treeHelpClose) {
      treeHelpClose.addEventListener('click', closeTreeHelp);
    }
    if (treeHelp) {
      treeHelp.addEventListener('click', function (e) {
        if (e.target === treeHelp) {
          closeTreeHelp();
        }
      });
    }
    if (plantRootBtn) {
      plantRootBtn.addEventListener('click', function () {
        var rect = host.getBoundingClientRect();
        addNode('skill', clientToWorld(rect.left + rect.width * 0.28, rect.top + rect.height * 0.55), {
          title: 'Introduccion',
          skillState: 'intro',
          progress: 0,
          isRoot: true,
          treeRole: 'intro',
          fromRail: false,
          autoLink: false
        });
        closeTreeHelp();
        showHint('Intro lista. Añade Nodes, conéctalos, y cierra con una Section.');
        setCanvasMode('edit');
      });
    }
    if (hintClose) {
      hintClose.addEventListener('click', hideHint);
    }

    var skillMenu = document.getElementById('vcSkillMenu');
    if (skillMenu) {
      skillMenu.addEventListener('click', function (e) {
        var btn = e.target.closest ? e.target.closest('[data-skill-state]') : null;
        if (!btn || !skillMenuNodeId) {
          return;
        }
        e.preventDefault();
        e.stopPropagation();
        var target = findNode(skillMenuNodeId);
        var state = btn.dataset.skillState;
        hideSkillStateMenu();
        if (!target) {
          return;
        }
        var labels = {
          locked: 'bloqueado',
          available: 'disponible',
          learning: 'en progreso',
          mastered: 'completado'
        };
        if (setSkillStateDirect(target, state)) {
          showHint(
            '“' + (target.title || 'skill') + '” → ' + (labels[state] || state)
          );
        }
      });
    }
    document.addEventListener('keydown', function (e) {
      if (e.key === 'Escape') {
        hideSkillStateMenu();
        resetTrainTap();
      }
    });
    document.addEventListener('mousedown', function (e) {
      var menu = document.getElementById('vcSkillMenu');
      if (!menu || menu.hidden) {
        return;
      }
      if (e.target.closest && e.target.closest('#vcSkillMenu')) {
        return;
      }
      hideSkillStateMenu();
    });

    document.querySelectorAll('.vc-range-btn').forEach(function (btn) {
      btn.addEventListener('click', function () {
        document.querySelectorAll('.vc-range-btn').forEach(function (b) {
          b.classList.remove('active');
        });
        btn.classList.add('active');
        rangeMonths = parseInt(btn.dataset.range, 10) || 36;
        var start = timelineStart();
        var end = addMonths(start, rangeMonths);
        scene.timeline.end = formatDate(end.y, end.m, 1);
        syncTimelineBoundNodes();
        renderAll();
        scheduleSave();
      });
    });

    var focusBtn = document.getElementById('vcFocusBtn');
    if (focusBtn) {
      focusBtn.addEventListener('click', function () {
        focusMode = !focusMode;
        focusBtn.classList.toggle('active', focusMode);
        renderAll();
      });
    }

    var fitBtn = document.getElementById('vcFitBtn');
    if (fitBtn) {
      fitBtn.addEventListener('click', fitToContent);
    }

    var searchBtn = document.getElementById('vcSearchBtn');
    if (searchBtn) {
      searchBtn.addEventListener('click', openSearch);
    }

    function openSearch() {
      if (!searchEl) {
        return;
      }
      searchEl.hidden = false;
      searchInput.value = '';
      searchResults.innerHTML = '';
      searchInput.focus();
    }

    function closeSearch() {
      if (searchEl) {
        searchEl.hidden = true;
      }
    }

    if (searchInput) {
      searchInput.addEventListener('input', function () {
        var q = searchInput.value.trim().toLowerCase();
        searchResults.innerHTML = '';
        if (!q) {
          return;
        }
        scene.nodes
          .filter(function (n) {
            return (
              (n.title || '').toLowerCase().indexOf(q) >= 0 ||
              (n.note || '').toLowerCase().indexOf(q) >= 0
            );
          })
          .slice(0, 12)
          .forEach(function (n) {
            var li = document.createElement('li');
            var btn = document.createElement('button');
            btn.type = 'button';
            btn.innerHTML =
              '<span>' +
              escapeHtml(n.title) +
              '</span><span class="vc-search-kind">' +
              escapeHtml(n.type) +
              '</span>';
            btn.addEventListener('click', function () {
              selectNode(n.id);
              centerOnNode(n);
              closeSearch();
            });
            li.appendChild(btn);
            searchResults.appendChild(li);
          });
      });
      searchInput.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
          closeSearch();
        }
      });
    }

    if (createModal) {
      createModal.querySelectorAll('[data-create-type]').forEach(function (btn) {
        btn.addEventListener('click', function () {
          createModal.querySelectorAll('[data-create-type]').forEach(function (b) {
            b.classList.remove('active');
          });
          btn.classList.add('active');
        });
      });
      var createConfirm = document.getElementById('vcCreateConfirm');
      var createCancel = document.getElementById('vcCreateCancel');
      if (createConfirm) {
        createConfirm.addEventListener('click', function () {
          var active = createModal.querySelector('[data-create-type].active');
          confirmCreate(
            active ? active.dataset.createType : 'milestone',
            active ? active.dataset.treeRole : null
          );
        });
      }
      if (createCancel) {
        createCancel.addEventListener('click', closeCreateModal);
      }
      createModal.addEventListener('click', function (e) {
        if (e.target === createModal) {
          closeCreateModal();
        }
      });
      if (createTitle) {
        createTitle.addEventListener('keydown', function (e) {
          if (e.key === 'Enter') {
            e.preventDefault();
            var active = createModal.querySelector('[data-create-type].active');
            confirmCreate(
              active ? active.dataset.createType : 'milestone',
              active ? active.dataset.treeRole : null
            );
          }
        });
      }
    }

    function bindInspector() {
      var title = document.getElementById('vcInspectorTitle');
      var note = document.getElementById('vcInspectorNote');
      var date = document.getElementById('vcInspectorDate');
      var progress = document.getElementById('vcInspectorProgress');
      var skillState = document.getElementById('vcInspectorSkillState');
      var isRootEl = document.getElementById('vcInspectorIsRoot');
      var color = document.getElementById('vcInspectorColor');
      var widthEl = document.getElementById('vcInspectorWidth');
      var heightEl = document.getElementById('vcInspectorHeight');
      var closeBtn = document.getElementById('vcInspectorClose');
      var collapseBtn = document.getElementById('vcCollapseBtn');
      var bookmarkBtn = document.getElementById('vcBookmarkBtn');
      var deleteBtn = document.getElementById('vcDeleteNodeBtn');

      function current() {
        return selectedId ? findNode(selectedId) : null;
      }

      function onField() {
        var n = current();
        if (!n) {
          return;
        }
        n.title = title.value;
        n.note = note.value;
        if (staysOffTimeline(n.type)) {
          n.color = normalizeColor(color ? color.value : n.color);
          n.anchoredToTimeline = false;
          if (n.type === 'zone') {
            if (widthEl) {
              n.w = clamp(parseInt(widthEl.value, 10) || n.w || NODE_SIZES.zone.w, 120, 2000);
            }
            if (heightEl) {
              n.h = clamp(parseInt(heightEl.value, 10) || n.h || NODE_SIZES.zone.h, 80, 2000);
            }
          }
        } else {
          n.date = date.value || n.date;
          if (n.date && (livesOnTimeline(n.type) || isNearTimeline(n) || n.anchoredToTimeline)) {
            var tx = monthToX(n.date);
            if (tx != null) {
              var s = nodeSize(n);
              n.x = tx - s.w / 2;
              n.anchoredToTimeline = true;
              var minY = timelineY() + 68;
              if (n.y < minY) {
                n.y = minY;
              }
            }
          }
          n.progress = clamp(parseInt(progress.value, 10) / 100, 0, 1);
          if (n.type === 'skill' || n.type === 'project') {
            if (n.type === 'skill' && isRootEl) {
              n.isRoot = !!isRootEl.checked;
            }
            if (n.type === 'skill' && n.isRoot) {
              n.skillState = 'intro';
              n.progress = 0;
              scene.nodes.forEach(function (other) {
                if (other.type === 'skill' && other.id !== n.id) {
                  other.isRoot = false;
                }
              });
            } else {
              n.skillState = skillState.value;
            }
          }
        }
        layoutTimelineStacks();
        renderAll();
        scheduleSave();
      }

      [title, note, date, progress, skillState, isRootEl, color, widthEl, heightEl].forEach(function (el) {
        if (el) {
          el.addEventListener('change', onField);
          el.addEventListener('input', onField);
        }
      });

      if (closeBtn) {
        closeBtn.addEventListener('click', function () {
          selectedId = null;
          renderAll();
        });
      }
      if (collapseBtn) {
        collapseBtn.addEventListener('click', function () {
          var n = current();
          if (n) {
            n.collapsed = !n.collapsed;
            renderAll();
            scheduleSave();
          }
        });
      }
      if (bookmarkBtn) {
        bookmarkBtn.addEventListener('click', function () {
          var n = current();
          scene.bookmarks.push({
            id: uid('bm'),
            label: n ? n.title : 'View',
            x: scene.viewport.x,
            y: scene.viewport.y,
            zoom: scene.viewport.zoom
          });
          renderBookmarks();
          scheduleSave();
        });
      }
      if (deleteBtn) {
        deleteBtn.addEventListener('click', function () {
          if (!selectedId) {
            return;
          }
          scene.edges = scene.edges.filter(function (ed) {
            return ed.from !== selectedId && ed.to !== selectedId;
          });
          scene.nodes = scene.nodes.filter(function (n) {
            return n.id !== selectedId;
          });
          selectedId = null;
          renderAll();
          scheduleSave();
        });
      }
    }

    bindInspector();

    if (minimapCanvas) {
      minimapCanvas.addEventListener('mousedown', function (e) {
        var b = minimapCanvas._bounds;
        if (!b) {
          return;
        }
        var rect = minimapCanvas.getBoundingClientRect();
        var mx = e.clientX - rect.left;
        var my = e.clientY - rect.top;
        var worldX = b.minX + mx / b.scale;
        var worldY = b.minY + my / b.scale;
        var hostRect = host.getBoundingClientRect();
        var z = scene.viewport.zoom;
        scene.viewport.x = hostRect.width / 2 - worldX * z;
        scene.viewport.y = hostRect.height / 2 - worldY * z;
        scheduleViewport();
        scheduleSave();
      });
    }

    // Align strategy nodes to current scale once on load
    syncTimelineBoundNodes();
    applyViewport();
    renderAll();
    setStatus('Ready');

    return {
      scene: scene,
      fitToContent: fitToContent,
      save: persist
    };
  }

  return { mount: mount };
})();
