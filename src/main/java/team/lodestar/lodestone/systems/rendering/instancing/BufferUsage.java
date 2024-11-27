package team.lodestar.lodestone.systems.rendering.instancing;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

public enum BufferUsage {
    STATIC_DRAW(GL_STATIC_DRAW),
    DYNAMIC_DRAW(GL_DYNAMIC_DRAW);

    private final int usage;

    BufferUsage(int usage) {
        this.usage = usage;
    }

    public int getUsage() {
        return usage;
    }
}
