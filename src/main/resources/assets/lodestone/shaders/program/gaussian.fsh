#version 150

uniform sampler2D DiffuseSampler;
uniform float Direction;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

const float weights[5] = float[](
    0.2270270270,
    0.1945945946,
    0.1216216216,
    0.0540540541,
    0.0162162162
);

void main() {
    vec2 dir = vec2(1.0-Direction, Direction);
    vec4 result = texture(DiffuseSampler, texCoord) * weights[0];
    for (int i = 1; i < 5; i++) {
        result += texture(DiffuseSampler, texCoord + (dir * oneTexel * float(i))) * weights[i];
        result += texture(DiffuseSampler, texCoord - (dir * oneTexel * float(i))) * weights[i];
    }
    fragColor = result;
}
