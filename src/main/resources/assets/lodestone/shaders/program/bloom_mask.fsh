#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D BloomMaskSampler;

in vec2 texCoord;

out vec4 fragColor;

/*
Since we are avoiding floating point buffers we should use some sort of way to express very bright values,
something basic could just be a function that divides by 1000 in the core shader and then that value is multiplied by 1000 in post.
making it so people can map higher values of bloom at the cost of precision.
*/
void main() {
    vec4 bloomMask = texture(BloomMaskSampler, texCoord);
    vec4 diffuse = texture(DiffuseSampler, texCoord);
    fragColor = bloomMask * diffuse;
}