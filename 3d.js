const width = 600,
    height = 480;

const canvas = document.getElementById("myCanvas");
const ctx = canvas.getContext("2d");

// Camera
let gX = 0,
    gY = 0,
    gZ = 0,

    rotX = 0,
    rotY = 0,
    rotZ = 0,

    cX = 0,
    cY = 0,
    cZ = 5,

    f = 0.1,
    pX = 0.06,
    pY = 0.048,
    offsetX = width / 2,
    offsetY = height / 2,
    skew = 0;

const pix = 12;

// 3D DATA
const verts =
    [
        { x: -1, y: -1, z: -1 },
        { x: 1, y: -1, z: -1 },
        { x: -1, y: 1, z: -1 },
        { x: 1, y: 1, z: -1 },
        { x: -1, y: -1, z: 1 },
        { x: 1, y: -1, z: 1 },
        { x: -1, y: 1, z: 1 },
        { x: 1, y: 1, z: 1 }
    ];

const edges =
    [ [ [ 0 ], [ 1 ] ], [ [ 0 ], [ 2 ] ], [ [ 2 ], [ 3 ] ], [ [ 1 ], [ 3 ] ], [ [ 0 ], [ 4 ] ], [ [ 1 ], [ 5 ] ], [ [ 2 ], [ 6 ] ], [ [ 3 ], [ 7 ] ], [ [ 4 ], [ 5 ] ], [ [ 4 ], [ 6 ] ], [ [ 6 ], [ 7 ] ], [ [ 5 ], [ 7 ] ] ];

const transformForOrigin = (point) => ({ x: point.x - gX, y: point.y - gY, z: point.z - gZ });

const rotateOnZ = (point) => {
    const cosRotZ = Math.cos(rotZ);
    const sinRotZ = Math.sin(rotZ);

    const x = point.x * cosRotZ - point.y * sinRotZ;
    const y = point.x * sinRotZ + point.y * cosRotZ;
    const z = point.z;

    return { x, y, z };
};

const rotateOnY = (point) => {
    const cosRotY = Math.cos(rotY);
    const sinRotY = Math.sin(rotY);

    const x = point.x * cosRotY + point.z * sinRotY;
    const y = point.y;
    const z = point.z * cosRotY - point.x * sinRotY;

    return { x, y, z };
};

const rotateOnX = (point) => {
    const cosRotX = Math.cos(rotX);
    const sinRotX = Math.sin(rotX);

    const x = point.x;
    const y = point.y * cosRotX - point.z * sinRotX;
    const z = point.y * sinRotX + point.z * cosRotX;

    return { x, y, z };
};

const transformForCameraLocation = (point) => ({ x: point.x - cX, y: point.y - cY, z: point.z - cZ });

const transformWithCameraProperties = (point) => {
    const x = point.x * (f * width) / (2 * pX) + point.y * skew;
    const y = point.y * (f * height) / (2 * pY);
    const z = point.z * -1;

    return { x, y, z };
};

const addPerspective = (point) => ({ x: point.x / point.z, y: point.y / point.z, z: point.z });

const transformForOffset = (point) => ({ x: point.x  + offsetX, y: offsetY - point.y, z: point.z });

function cameraModel(data) {
    return data
        .map(transformForOrigin)
        .map(rotateOnZ)
        .map(rotateOnY)
        .map(rotateOnX)
        .map(transformForCameraLocation)
        .map(transformWithCameraProperties)
        .map(addPerspective)
        .map(transformForOffset);
}

function displayData(points, lines) {
    drawDots(points);
    drawLines(points, lines);
}

function drawDots(data) {
    data.filter(point => point.z > 0)
        .forEach(point => {
            const d = pix / point.z / 2;
            ctx.fillRect(point.x - d, point.y - d, 2 * d, 2 * d);
        });
}

function drawLines(points, lines) {
    for (let i = 0; i < lines.length; i++) {
        const j = lines[i][0];
        const k = lines[i][1];
        if (points[j].z > 0 && points[k].z > 0) {
            ctx.beginPath();
            ctx.moveTo(points[j].x, (points[j].y));
            ctx.lineTo(points[k].x, (points[k].y));
            ctx.stroke();
        }
    }
}

const mainLoopId = setInterval(function(){
    ctx.clearRect(0, 0, width, height);

    displayData(cameraModel(verts), edges);

    rotY += 0.02;
    rotX += 0.02;
}, 1000/60);
