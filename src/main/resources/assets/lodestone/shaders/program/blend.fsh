#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D BlendSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

vec4 additive(vec4 base, vec4 blend) {
    return base + blend;
}

void main() {
    vec4 diffuse = texture(DiffuseSampler, texCoord);
    vec4 blend = texture(BlendSampler, texCoord);
    fragColor = additive(diffuse, blend);
}
