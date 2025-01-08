package team.lodestar.lodestone.helpers;

import java.nio.ByteBuffer;

public class TextureHelper {
    /**
     * Converts a texture atlas to a 3D texture volume
     * @param textureAtlas the texture atlas
     * @param width the width of the texture atlas
     * @param height the height of the texture atlas
     * @param xSlices the number of slices in the x direction
     * @param ySlices the number of slices in the y direction
     * @return the 3D texture volume
     */
    public static ByteBuffer convertTextureAtlasToVolume(ByteBuffer textureAtlas, int width, int height, int xSlices, int ySlices, int channels, int[] resultSize) {
        validateTextureAtlasSize(width, height, xSlices, ySlices);
        int resultWidth = width / xSlices;
        int resultHeight = height / ySlices;
        int resultDepth = xSlices * ySlices;

        resultSize[0] = resultWidth;
        resultSize[1] = resultHeight;
        resultSize[2] = resultDepth;

        ByteBuffer volume = ByteBuffer.allocateDirect(resultWidth * resultHeight * resultDepth * channels);

        textureAtlas.rewind();

        for (int slice = 0; slice < resultDepth; slice++) {
            int sliceX = slice % xSlices;
            int sliceY = slice / xSlices;

            for (int y = 0; y < resultHeight; y++) {
                for (int x = 0; x < resultWidth; x++) {
                    int atlasX = sliceX * resultWidth + x;
                    int atlasY = sliceY * resultHeight + y;

                    int atlasIndex = (atlasY * width + atlasX) * channels;
                    int volumeIndex = ((slice * resultHeight + y) * resultWidth + x) * channels;

                    for (int c = 0; c < channels; c++) {
                        volume.put(volumeIndex + c, textureAtlas.get(atlasIndex + c));
                    }
                }
            }
        }
        volume.rewind();

        return volume;
    }

    public static void validateTextureAtlasSize(int width, int height, int xSlices, int ySlices) {
        if (width % xSlices != 0 || height % ySlices != 0) {
            throw new IllegalArgumentException("Image dimensions must be a multiple of the texture dimensions");
        }
    }
}
