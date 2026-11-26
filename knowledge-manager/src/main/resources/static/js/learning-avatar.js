const avatarOutfits = [
  {
    name: 'Ropa de calle', jacket: '#85504d', shirt: '#b4aa90', pants: '#3c4347',
    boots: '#403530', backlight: '#324953', edgeLight: '#8e7761'
  },
  {
    name: 'Chaqueta, linterna y bolsa', jacket: '#3d6470', shirt: '#89968e', pants: '#555147',
    boots: '#393534', backlight: '#3d5250', edgeLight: '#a29662'
  },
  {
    name: 'Chaleco, radio y protecciones', jacket: '#626955', shirt: '#424d46', pants: '#3a4745',
    boots: '#343637', backlight: '#38504a', edgeLight: '#83a691'
  },
  {
    name: 'Equipo de rescate con casco', jacket: '#a56c41', shirt: '#4a504a', pants: '#40494a',
    boots: '#343737', backlight: '#4e4e4d', edgeLight: '#b0835b'
  }
];

function createAvatarMesh(stage, outfit) {
  const faces = [];
  const skin = '#bb896e';
  const skinShadow = '#936650';
  const hair = '#302729';
  const canvas = '#474842';
  const rubber = '#2a3032';
  const trim = '#a29a81';

  function polygon(vertices, material, twoSided = false) {
    faces.push({vertices, material, twoSided});
  }

  // Cross sections define the height, X/Z center and the two radii.
  function loft(rows, material, sides = 8) {
    const rings = rows.map(([y, x, z, rx, rz]) =>
      Array.from({length: sides}, (_, index) => {
        const angle = 2 * Math.PI * index / sides;
        return [x + Math.cos(angle) * rx, y, z + Math.sin(angle) * rz];
      }));
    for (let ring = 1; ring < rings.length; ring++) {
      for (let index = 0; index < sides; index++) {
        const next = (index + 1) % sides;
        polygon([rings[ring - 1][index], rings[ring][index], rings[ring][next], rings[ring - 1][next]], material);
      }
    }
    polygon(rings[0], material);
    polygon([...rings.at(-1)].reverse(), material);
  }

  function box(x, y, z, width, height, depth, material) {
    const corners = [
      [x - width / 2, y - height / 2, z - depth / 2], [x + width / 2, y - height / 2, z - depth / 2],
      [x - width / 2, y + height / 2, z - depth / 2], [x + width / 2, y + height / 2, z - depth / 2],
      [x - width / 2, y - height / 2, z + depth / 2], [x + width / 2, y - height / 2, z + depth / 2],
      [x - width / 2, y + height / 2, z + depth / 2], [x + width / 2, y + height / 2, z + depth / 2]
    ];
    for (const indices of [[0, 2, 3, 1], [4, 5, 7, 6], [0, 4, 6, 2], [1, 3, 7, 5], [0, 1, 5, 4], [2, 6, 7, 3]]) {
      polygon(indices.map(index => corners[index]), material);
    }
  }

  function patch(x, y, z, width, height, material) {
    polygon([[x - width / 2, y - height / 2, z], [x + width / 2, y - height / 2, z],
      [x + width / 2, y + height / 2, z], [x - width / 2, y + height / 2, z]], material);
  }

  for (const side of [-1, 1]) {
    const x = side * .245;
    const footZ = side < 0 ? .21 : -.135;
    const kneeZ = side < 0 ? .15 : -.065;
    const bootHeight = stage >= 2 ? .47 : .37;
    loft([[bootHeight - .09, x, footZ, .115, .11], [.63, x, footZ * .85, .133, .126],
      [.94, x, kneeZ, .15, .147], [1.21, x * .9, kneeZ * .65, .187, .164],
      [1.73, x * .77, -.004, .213, .184]], outfit.pants);
    loft([[.035, x, footZ + .082, .139, .216], [.082, x, footZ + .082, .149, .223]], rubber);
    loft([[.082, x, footZ + .08, .144, .218], [.148, x, footZ + .07, .142, .195],
      [.23, x, footZ + .025, .127, .149], [bootHeight, x, footZ, .126, .136]], outfit.boots);
    if (stage >= 2) {
      loft([[.77, x, kneeZ + .151, .102, .043], [.91, x, kneeZ + .164, .122, .068],
        [1.018, x, kneeZ + .142, .11, .045]], stage === 3 ? '#6c6d58' : '#505e56', 8);
    }
  }

  loft([[1.52, 0, -.015, .34, .20], [1.66, 0, 0, .355, .204],
    [1.80, 0, -.018, .305, .186]], outfit.pants, 10);
  loft([[1.66, 0, 0, .358, .209], [1.742, 0, -.006, .345, .208]], '#403834', 10);
  box(.018, 1.704, .206, .102, .061, .035, trim);
  loft([[1.74, 0, -.024, .348, .215], [1.90, .003, -.027, .322, .20],
    [2.14, .012, -.01, .366, .229], [2.39, .025, -.012, .437, .239],
    [2.61, .025, -.018, .457, .213], [2.73, .027, -.032, .252, .164]], outfit.jacket, 12);
  loft([[2.62, .027, -.02, .119, .111], [2.91, .036, -.006, .114, .104]], skin, 8);

  if (stage < 2) {
    polygon([[-.116, 1.80, .188], [.109, 1.80, .188], [.065, 2.13, .23], [-.045, 2.13, .224]], outfit.shirt);
    polygon([[-.045, 2.13, .224], [.065, 2.13, .23], [.13, 2.40, .218], [-.12, 2.40, .218]], outfit.shirt);
    polygon([[-.12, 2.40, .218], [.13, 2.40, .218], [.145, 2.651, .132], [-.101, 2.661, .135]], outfit.shirt);
    polygon([[-.264, 2.611, .158], [-.115, 2.47, .243], [-.086, 2.626, .178], [-.147, 2.705, .122]], '#655a51');
    polygon([[.115, 2.47, .241], [.284, 2.596, .152], [.179, 2.69, .114], [.126, 2.62, .168]], '#655a51');
    box(.236, 2.33, .188, .152, .096, .044, outfit.jacket);
    patch(.237, 2.357, .213, .114, .019, stage === 0 ? '#ac7670' : '#829b9a');
  } else {
    loft([[1.84, .02, .075, .29, .169], [2.10, .019, .071, .338, .212],
      [2.415, .027, .048, .385, .214], [2.585, .03, .064, .319, .143]], outfit.shirt, 10);
    box(.023, 2.37, .231, .40, .154, .066, stage === 2 ? '#58645a' : '#5a625e');
    patch(.016, 2.398, .268, .287, .034, stage === 2 ? '#8b9275' : '#beaa78');
    for (const x of [-.162, .012, .187]) {
      box(x, 2.02, .217, .143, .203, .138, canvas);
      box(x, 2.117, .251, .15, .045, .102, '#616253');
    }
    box(-.282, 2.51, .161, .129, .233, .108, rubber);
    box(-.308, 2.677, .158, .026, .13, .025, rubber);
    patch(-.278, 2.571, .219, .040, .018, '#8fb3a0');
    box(.318, 1.655, .073, .194, .258, .248, stage === 3 ? '#806747' : '#585b50');
    patch(.321, 1.704, .203, .11, .023, trim);
  }

  for (const side of [-1, 1]) {
    const holdingLight = side === -1 && stage === 1;
    const wristX = side * (holdingLight ? .53 : .61);
    const wristY = holdingLight ? 2.19 : side > 0 ? 1.82 : 1.89;
    const wristZ = holdingLight ? .36 : side > 0 ? .085 : .14;
    const elbow = [holdingLight ? 2.02 : 2.12, side * .595, .011, .104, .112];
    const forearm = [wristY + (holdingLight ? -.047 : .09), wristX, wristZ * .86, .091, .093];
    const shoulder = [2.638, side * .425 + .023, -.018, .107, .102];
    loft([[wristY, wristX, wristZ, .073, .079], forearm, elbow,
      [2.42, side * .50, -.008, .135, .127], shoulder].sort((first, second) => first[0] - second[0]), outfit.jacket);
    loft([[wristY - .116, wristX, wristZ + .02, .052, .061],
      [wristY - .051, wristX, wristZ + .021, .081, .079],
      [wristY + .014, wristX, wristZ, .071, .071]], stage >= 2 ? rubber : skin, 8);
    if (stage >= 2) {
      loft([[2.425, side * .494, -.015, .133, .135],
        [2.547, side * .477, -.013, .152, .156], [2.614, side * .449, -.010, .127, .136]],
      stage === 3 ? '#676e64' : '#4d5850', 8);
    }
    if (stage === 3) {
      loft([[2.22, side * .56, .002, .117, .125], [2.276, side * .549, .001, .126, .135]], '#c2ad7e', 8);
    }
    if (holdingLight) {
      box(wristX, wristY - .066, wristZ + .064, .102, .109, .236, '#797651');
      box(wristX, wristY - .066, wristZ + .185, .138, .126, .087, '#313a3b');
      patch(wristX, wristY - .066, wristZ + .231, .093, .081, '#f5db93');
    }
  }

  loft([[2.835, .036, .045, .095, .105], [2.899, .037, .037, .151, .127],
    [3.057, .043, .015, .188, .151], [3.231, .04, .008, .177, .148],
    [3.337, .04, -.006, .118, .105]], skin, 10);
  loft([[3.205, .04, -.023, .179, .147], [3.33, .035, -.022, .162, .142],
    [3.391, .034, -.024, .105, .105]], hair, 9);
  loft([[2.977, .038, -.073, .146, .091], [3.154, .04, -.078, .175, .099],
    [3.272, .04, -.08, .163, .092]], hair, 9);
  polygon([[-.133, 3.234, .088], [.025, 3.257, .148], [-.06, 3.162, .137]], hair, true);
  for (const offset of [-.07, .071]) {
    box(.043 + offset, 3.116, .142, .06, .019, .028, '#4c3934');
    patch(.043 + offset, 3.093, .156, .046, .014, '#362f2d');
  }
  loft([[2.996, .045, .159, .032, .027], [3.031, .046, .166, .036, .033],
    [3.082, .045, .148, .018, .017]], skinShadow, 6);
  patch(.042, 2.951, .156, .077, .014, '#7e5548');

  if (stage === 1) {
    polygon([[-.26, 2.695, .12], [-.171, 2.703, .138], [.31, 1.811, .157], [.238, 1.786, .195]], '#635445', true);
    loft([[1.34, .41, .015, .155, .172], [1.407, .438, .021, .18, .193],
      [1.72, .418, .02, .172, .182], [1.805, .39, .015, .153, .15]], '#7e654e');
    box(.429, 1.643, .186, .208, .129, .084, '#8d7658');
    patch(.429, 1.60, .232, .041, .048, trim);
  }
  if (stage >= 2) {
    loft([[1.74, .03, -.265, .177, .10], [1.86, .03, -.307, .241, .157],
      [2.48, .026, -.302, .255, .155], [2.608, .026, -.277, .205, .117]],
    stage === 3 ? '#615b48' : '#38433e');
    box(.029, 2.254, -.458, .31, .086, .03, stage === 3 ? '#a88a56' : '#626d5d');
  }
  if (stage === 3) {
    loft([[3.162, .04, -.018, .207, .177], [3.229, .04, -.023, .23, .204],
      [3.351, .04, -.024, .185, .185], [3.445, .04, -.034, .111, .12]], '#807455', 10);
    box(.041, 3.243, .17, .194, .078, .094, '#474d45');
    patch(.042, 3.248, .22, .109, .048, '#e7d7a7');
    loft([[2.88, .041, .118, .073, .053], [2.937, .041, .156, .144, .076],
      [3.028, .041, .158, .144, .077], [3.066, .041, .131, .084, .051]], '#505c55', 10);
    for (const x of [-.03, .118]) {
      box(x, 2.969, .21, .068, .073, .062, '#343c39');
      patch(x, 2.969, .244, .042, .026, '#777b65');
    }
  }

  return faces;
}

function createAvatarBackdrop(width, height, outfit) {
  const panel = document.createElement('canvas');
  panel.width = width;
  panel.height = height;
  const scene = panel.getContext('2d');
  const wall = height * .72;
  const center = width / 2;
  const haze = scene.createRadialGradient(center, height * .41, height * .07, center, height * .5, width * .69);
  haze.addColorStop(0, outfit.backlight);
  haze.addColorStop(1, '#0d1217');
  scene.fillStyle = haze;
  scene.fillRect(0, 0, width, height);

  const unit = height / 340;
  scene.lineWidth = unit;
  for (const column of [.092, .235, .77, .91]) {
    const x = column * width;
    const pilaster = width * (column < .2 || column > .8 ? .038 : .022);
    scene.fillStyle = '#03080d56';
    scene.fillRect(x, 0, pilaster, wall);
    scene.fillStyle = '#c3b9a421';
    scene.fillRect(x + pilaster, 0, unit, wall);
  }
  scene.fillStyle = '#20282a';
  scene.fillRect(0, wall, width, height - wall);
  scene.strokeStyle = '#11191d';
  scene.beginPath();
  scene.moveTo(0, wall);
  scene.lineTo(width, wall);
  for (const line of [0.14, .37, .73]) {
    const y = wall + line * (height - wall);
    scene.moveTo(0, y);
    scene.lineTo(width, y);
  }
  for (let column = -4; column <= 4; column++) {
    scene.moveTo(center + column * width * .063, wall);
    scene.lineTo(center + column * width * .22, height);
  }
  scene.stroke();

  scene.save();
  scene.translate(center, height * .91);
  scene.scale(1, .25);
  const floorLight = scene.createRadialGradient(0, 0, height * .07, 0, 0, height * .39);
  floorLight.addColorStop(0, '#a2946350');
  floorLight.addColorStop(1, '#a2946300');
  scene.fillStyle = floorLight;
  scene.fillRect(-width / 2, -height, width, 2 * height);
  const shadow = scene.createRadialGradient(0, 0, height * .03, 0, 0, height * .22);
  shadow.addColorStop(0, '#080b10b0');
  shadow.addColorStop(1, '#080b1000');
  scene.fillStyle = shadow;
  scene.fillRect(-width / 2, -height, width, 2 * height);
  scene.restore();

  const lampX = width * .81;
  const lampY = height * .156;
  const light = scene.createRadialGradient(lampX, lampY, 0, lampX, lampY, height * .15);
  light.addColorStop(0, `${outfit.edgeLight}a8`);
  light.addColorStop(1, `${outfit.edgeLight}00`);
  scene.fillStyle = light;
  scene.fillRect(0, 0, width, height);
  scene.fillStyle = '#171719';
  scene.fillRect(lampX - width * .044, lampY - unit * 4, width * .088, unit * 8);
  scene.fillStyle = outfit.edgeLight;
  scene.fillRect(lampX - width * .035, lampY - unit * 1.5, width * .07, unit * 3);

  const vignette = scene.createRadialGradient(center, height * .49, height * .22, center, height * .49, width * .63);
  vignette.addColorStop(0, '#080d1400');
  vignette.addColorStop(1, '#080d14c2');
  scene.fillStyle = vignette;
  scene.fillRect(0, 0, width, height);

  let noise = 72391;
  for (let dot = 0; dot < 4800; dot++) {
    noise = (Math.imul(noise, 1664525) + 1013904223) >>> 0;
    const x = noise * width / 4294967296;
    noise = (Math.imul(noise, 1664525) + 1013904223) >>> 0;
    const y = noise * height / 4294967296;
    scene.fillStyle = dot % 3 ? '#070b112a' : '#c9c3a623';
    scene.fillRect(x, y, unit * (dot % 4 + 1), unit * 1.1);
  }
  return panel;
}

function setupAvatar(avatar) {
  const canvas = avatar.querySelector('canvas');
  const painter = canvas.getContext('2d');
  if (!painter) return;

  const turn = avatar.querySelector('input');
  const stage = Number(avatar.dataset.avatarStage);
  const outfit = avatarOutfits[stage];
  avatar.querySelector('[data-avatar-outfit]').textContent = outfit.name;
  const mesh = createAvatarMesh(stage, outfit).map((face, index) => ({
    ...face,
    rgb: face.material.slice(1).match(/../g).map(value => parseInt(value, 16)),
    variation: ((index * 13) % 11 - 5) * .011
  }));
  let backdrop;

  function draw() {
    const angle = Number(turn.value) * Math.PI / 180;
    const cos = Math.cos(angle);
    const sin = Math.sin(angle);
    const tilt = .16;
    const width = canvas.width;
    const height = canvas.height;
    const unit = height * .233;
    const centerY = height * .49;
    const centerX = width / 2;
    painter.drawImage(backdrop, 0, 0);

    const projected = [];
    for (const face of mesh) {
      const points = face.vertices.map(([x, y, z]) => {
        const yawDepth = z * cos - x * sin;
        const deltaY = y - 1.72;
        const cameraX = x * cos + z * sin;
        const cameraY = deltaY * Math.cos(tilt) - yawDepth * Math.sin(tilt);
        const depth = deltaY * Math.sin(tilt) + yawDepth * Math.cos(tilt);
        const scale = unit * 9 / (9 - depth);
        return {x: cameraX, y: cameraY, depth,
          screenX: centerX + cameraX * scale, screenY: centerY - cameraY * scale};
      });
      const [a, b, c] = points;
      const normalX = (b.y - a.y) * (c.depth - a.depth) - (b.depth - a.depth) * (c.y - a.y);
      const normalY = (b.depth - a.depth) * (c.x - a.x) - (b.x - a.x) * (c.depth - a.depth);
      const normalZ = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
      const view = -normalX * a.x - normalY * a.y + normalZ * (9 - a.depth);
      if (view <= 0 && !face.twoSided) continue;
      const length = Math.hypot(normalX, normalY, normalZ);
      if (length < 1e-6) continue;
      const exposure = (view > 0 ? 1 : -1) / length;
      const key = Math.max(0, (-.58 * normalX + .65 * normalY + .50 * normalZ) * exposure);
      const rim = Math.max(0, (.80 * normalX + .29 * normalY - .52 * normalZ) * exposure);
      const light = .57 + .60 * key + .28 * rim + face.variation;
      const rgb = face.rgb.map((channel, index) =>
        Math.min(255, Math.round((channel * light + rim * [13, 10, 6][index]) / 3) * 3));
      projected.push({points, fill: `rgb(${rgb.join(',')})`,
        depth: points.reduce((sum, point) => sum + point.depth, 0) / points.length});
    }
    projected.sort((a, b) => a.depth - b.depth);
    for (const {points, fill} of projected) {
      painter.beginPath();
      painter.moveTo(points[0].screenX, points[0].screenY);
      for (const point of points.slice(1)) painter.lineTo(point.screenX, point.screenY);
      painter.closePath();
      painter.fillStyle = fill;
      painter.strokeStyle = fill;
      painter.lineWidth = height / 580;
      painter.fill();
      painter.stroke();
    }
  }

  function resize() {
    const bounds = canvas.getBoundingClientRect();
    const pixelRatio = Math.min(window.devicePixelRatio || 1, 2);
    canvas.width = Math.max(1, Math.round(bounds.width * pixelRatio));
    canvas.height = Math.max(1, Math.round(bounds.height * pixelRatio));
    backdrop = createAvatarBackdrop(canvas.width, canvas.height, outfit);
    draw();
  }

  let drag;
  canvas.addEventListener('pointerdown', event => {
    if (event.button !== 0 || !event.isPrimary) return;
    drag = {x: event.clientX, angle: Number(turn.value), id: event.pointerId};
    canvas.setPointerCapture(event.pointerId);
    canvas.classList.add('is-turning');
  });
  canvas.addEventListener('pointermove', event => {
    if (drag?.id !== event.pointerId) return;
    const angle = Math.round(drag.angle + (event.clientX - drag.x) * 1.05);
    turn.value = ((angle + 180) % 360 + 360) % 360 - 180;
    draw();
  });
  canvas.addEventListener('lostpointercapture', () => {
    drag = undefined;
    canvas.classList.remove('is-turning');
  });

  turn.parentElement.hidden = false;
  turn.addEventListener('input', draw);
  new ResizeObserver(resize).observe(canvas);
  resize();
}

const avatar = document.querySelector('[data-avatar-stage]');
if (avatar) setupAvatar(avatar);
