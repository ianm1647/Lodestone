package team.lodestar.lodestone.systems.texture;

import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import team.lodestar.lodestone.helpers.TextureHelper;
import team.lodestar.lodestone.systems.rendering.LodestoneRenderSystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;

import static org.lwjgl.opengl.GL30.*;

public class VolumeTexture extends AbstractTexture {
    protected final ResourceLocation location;
    private final int xSlices, ySlices;
    private int width, height, depth;

    public VolumeTexture(ResourceLocation location, int xSlices, int ySlices) {
        this.location = location;
        this.xSlices = xSlices;
        this.ySlices = ySlices;
    }

    @Override
    public void load(ResourceManager resourceManager) throws IOException {
        Optional<Resource> resource = resourceManager.getResource(location);
        if (resource.isPresent()) {
            ByteBuffer textureData = TextureUtil.readResource(resource.get().open());
            textureData.rewind();

            ByteBuffer image;
            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);

                image = STBImage.stbi_load_from_memory(textureData, width, height, channels, 4);
                if (image == null) {
                    throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
                }
                int[] resultSize = new int[3];
                ByteBuffer volume = TextureHelper.convertTextureAtlasToVolume(image, width.get(), height.get(), this.xSlices, this.ySlices, 4, resultSize);
                this.width = resultSize[0];
                this.height = resultSize[1];
                this.depth = resultSize[2];

                this.id = glGenTextures();
                glBindTexture(GL_TEXTURE_3D, this.id);
                glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
                glTexImage3D(GL_TEXTURE_3D, 0, GL_RGBA, this.width, this.height, this.depth, 0, GL_RGBA, GL_UNSIGNED_BYTE, volume);
                glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
                glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);
                glTexParameteri(GL_TEXTURE_3D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_BORDER);
                glGenerateMipmap(GL_TEXTURE_3D);
                glBindTexture(GL_TEXTURE_3D, 0);

                STBImage.stbi_image_free(image);
            }
        }
    }

    @Override
    public void bind() {
        LodestoneRenderSystem.wrap(() -> glBindTexture(GL_TEXTURE_3D, this.id));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public int getXSlices() {
        return xSlices;
    }

    public int getYSlices() {
        return ySlices;
    }
}