#version 150

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    fragColor = vec4(1.0, 1.0, 1.0, 1.0);
}