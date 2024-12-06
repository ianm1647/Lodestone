#version 150

#moj_import <lodestone:common_math.glsl>

in vec3 Position;
in mat4 InstancedMatrix;

uniform mat4 ModelViewMat;
uniform mat3 IViewRotMat;
uniform mat4 ProjMat;
uniform int FogShape;

out vec4 vertexColor;
out float vertexDistance;

void main() {
    vec4 pos = ModelViewMat * InstancedMatrix * vec4(Position, 1.0);
    gl_Position = ProjMat * pos;

    vertexDistance = fogDistance(pos.xyz, FogShape);
}
