vec4 sampleVolumeAtlas(in sampler2D volume, in ivec2 spriteDims, in vec3 worldPos) {
    float numSlices   = float(spriteDims.x * spriteDims.y);
    float sliceFloat  = clamp(worldPos.z, 0.0, 1.0 - 1e-6) * numSlices;
    int   sliceIndex  = int(floor(sliceFloat));

    int row = sliceIndex / spriteDims.x;
    int col = sliceIndex % spriteDims.x;

    float tileSizeX = 1.0 / float(spriteDims.x);
    float tileSizeY = 1.0 / float(spriteDims.y);

    float u = (float(col) + worldPos.x) * tileSizeX;
    float v = (float(row) + worldPos.y) * tileSizeY;

    return texture(volume, vec2(u, v));
}