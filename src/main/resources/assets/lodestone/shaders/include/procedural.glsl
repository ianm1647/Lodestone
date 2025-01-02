float random2D (vec2 uv) {
    return fract(sin(dot(uv.xy,
    vec2(12.9898,78.233)))*
    43758.5453123);
}

float random3D (vec3 uv) {
    return fract(sin(dot(uv.xyz,
    vec3(12.9898,78.233, 45.543)))*
    43758.5453123);
}

float random4D(vec4 uv) {
    return fract(sin(dot(uv.xyzw,
    vec4(12.9898,78.233, 45.543, 94.654)))*
    43758.5453123);
}

// Based on Morgan McGuire @morgan3d
// https://www.shadertoy.com/view/4dS3Wd
float noise2D (vec2 st) {
    vec2 i = floor(st);
    vec2 f = fract(st);

    // Four corners in 2D of a tile
    float a = random2D(i);
    float b = random2D(i + vec2(1.0, 0.0));
    float c = random2D(i + vec2(0.0, 1.0));
    float d = random2D(i + vec2(1.0, 1.0));

    vec2 u = f * f * (3.0 - 2.0 * f);

    return mix(a, b, u.x) +
    (c - a)* u.y * (1.0 - u.x) +
    (d - b) * u.x * u.y;
}

float noise3D (vec3 st) {
    vec3 i = floor(st);
    vec3 j = fract(st);

    float a = random3D(i);
    float b = random3D(i + vec3(1.0, 0.0, 0.0));
    float c = random3D(i + vec3(0.0, 1.0, 0.0));
    float d = random3D(i + vec3(1.0, 1.0, 0.0));
    float e = random3D(i + vec3(0.0, 0.0, 1.0));
    float f = random3D(i + vec3(1.0, 0.0, 1.0));
    float g = random3D(i + vec3(0.0, 1.0, 1.0));
    float h = random3D(i + vec3(1.0, 1.0, 1.0));

    float a2 = mix(a, b, j.x);
    float b2 = mix(c, d, j.x);
    float c2 = mix(e, f, j.x);
    float d2 = mix(g, h, j.x);

    float a3 = mix(a2, b2, j.y);
    float b3 = mix(c2, d2, j.y);

    return mix(a3, b3, j.z);
}

float noise4D(vec4 st) {
    vec4 y = floor(st);
    vec4 z = fract(st);

    float a = random4D(y);
    float b = random4D(y + vec4(1.0, 0.0, 0.0, 0.0));
    float c = random4D(y + vec4(0.0, 1.0, 0.0, 0.0));
    float d = random4D(y + vec4(1.0, 1.0, 0.0, 0.0));
    float e = random4D(y + vec4(0.0, 0.0, 1.0, 0.0));
    float f = random4D(y + vec4(1.0, 0.0, 1.0, 0.0));
    float g = random4D(y + vec4(0.0, 1.0, 1.0, 0.0));
    float h = random4D(y + vec4(1.0, 1.0, 1.0, 0.0));
    float i = random4D(y + vec4(0.0, 0.0, 0.0, 1.0));
    float j = random4D(y + vec4(1.0, 0.0, 0.0, 1.0));
    float k = random4D(y + vec4(0.0, 1.0, 0.0, 1.0));
    float l = random4D(y + vec4(1.0, 1.0, 0.0, 1.0));
    float m = random4D(y + vec4(0.0, 0.0, 1.0, 1.0));
    float n = random4D(y + vec4(1.0, 0.0, 1.0, 1.0));
    float o = random4D(y + vec4(0.0, 1.0, 1.0, 1.0));
    float p = random4D(y + vec4(1.0, 1.0, 1.0, 1.0));

    float a2 = mix(a, b, z.x);
    float b2 = mix(c, d, z.x);
    float c2 = mix(e, f, z.x);
    float d2 = mix(g, h, z.x);
    float e2 = mix(i, j, z.x);
    float f2 = mix(k, l, z.x);
    float g2 = mix(m, n, z.x);
    float h2 = mix(o, p, z.x);

    float a3 = mix(a2, b2, z.y);
    float b3 = mix(c2, d2, z.y);
    float c3 = mix(e2, f2, z.y);
    float d3 = mix(g2, h2, z.y);

    float e3 = mix(a3, b3, z.z);
    float f3 = mix(c3, d3, z.z);

    return mix(e3, f3, z.w);
}

float fbm2D (vec2 uv, int OCTAVES) {
    float value = 0.0;
    float amplitude = .5;

    for (int i = 0; i < OCTAVES; i++) {
        value += amplitude * noise2D(uv);
        uv *= 2.;
        amplitude *= .5;
    }
    return value;
}

float fbm3D (vec3 uv, int OCTAVES) {
    float value = 0.0;
    float amplitude = .5;

    for (int i = 0; i < OCTAVES; i++) {
        value += amplitude * noise3D(uv);
        uv *= 2.;
        amplitude *= .5;
    }
    return value;
}

float fbm4D(vec4 uv, int OCTAVES) {
    float value = 0.0;
    float amplitude = 0.5;

    for (int i = 0; i < OCTAVES; i++) {
        value += amplitude * noise4D(uv);
        uv *= 2.0;
        amplitude *= 0.5;
    }

    return value;
}