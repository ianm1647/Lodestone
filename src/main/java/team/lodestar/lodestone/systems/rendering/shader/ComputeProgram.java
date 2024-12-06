package team.lodestar.lodestone.systems.rendering.shader;

import com.mojang.blaze3d.shaders.ProgramManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.apache.commons.io.IOUtils;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.systems.rendering.IBufferObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL43.*;

public class ComputeProgram implements IBufferObject {
    private int programId;
    private Shader shader;
    private ResourceLocation shaderLocation;

    public ComputeProgram(ResourceLocation shaderLocation) {
        this.shaderLocation = shaderLocation;
        this.registerBufferObject();
    }

    public void register(RegisterShadersEvent event) {
        ResourceProvider provider = event.getResourceProvider();
        LodestoneLib.LOGGER.info("Registering compute shader: " + this.shaderLocation);
        loadShader(provider);
    }

    private void loadShader(ResourceProvider provider) {
        this.destroy();
        try {
            this.programId = ProgramManager.createProgram();
            LodestoneLib.LOGGER.info("Created compute shader program: " + this.programId);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create compute shader program");
        }
        this.shader = new Shader(provider, this.shaderLocation);
        glAttachShader(this.programId, this.shader.getId());
        glLinkProgram(this.programId);
    }
    public void bind() {
        ProgramManager.glUseProgram(this.programId);
    }

    @Override
    public void destroy() {
        if (this.programId != 0)
            glDeleteProgram(this.programId);
        if (this.shader != null && this.shader.getId() != 0)
            glDeleteShader(this.shader.getId());
        this.programId = 0;
        this.shader = null;
    }

    @Override
    public void registerBufferObject() {
        IBufferObject.super.registerBufferObject();
    }

    public static class Shader {
        private int shaderId;
        private String source;

        public Shader(ResourceProvider provider, ResourceLocation shaderLocation) {
            ResourceLocation shaderLocation1 = ResourceLocation.fromNamespaceAndPath(shaderLocation.getNamespace(), "shaders/compute/" + shaderLocation.getPath());
            try {
                this.source = openResource(provider.getResourceOrThrow(shaderLocation1));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to open resource: " + shaderLocation1);
            }
            LodestoneLib.LOGGER.info("Shader source: " + this.source);
            compile();
        }

        private String openResource(Resource resource) {
            String result;
            try (InputStream stream = resource.open()) {
                result = IOUtils.toString(stream, StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException("Failed to open resource: " + resource, e);
            }
            return result;
        }

        public void compile() {
            this.shaderId = glCreateShader(GL_COMPUTE_SHADER);
            glShaderSource(this.shaderId, this.source);
            glCompileShader(this.shaderId);
            if (glGetShaderi(this.shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
                throw new RuntimeException("Failed to compile shader: " + glGetShaderInfoLog(this.shaderId, 1024));
            }
        }

        public int getId() {
            return this.shaderId;
        }
    }
}
