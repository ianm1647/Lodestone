package team.lodestar.lodestone.handlers;

import team.lodestar.lodestone.systems.model.obj.IndexedModel;
import team.lodestar.lodestone.systems.rendering.instancing.InstancedVertexBuffer;
import team.lodestar.lodestone.systems.rendering.instancing.TransformInstanceData;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstanceRenderHandler {
    public static Map<IndexedModel, InstancedDataHolder> instancedData = new HashMap<>();

    public static void addInstance(IndexedModel model, TransformInstanceData data) {
        instancedData.computeIfAbsent(model, k -> new InstancedDataHolder()).add(data);
    }

    public static InstancedDataHolder get(IndexedModel model) {
        return instancedData.get(model);
    }

    public static @Nullable ByteBuffer createBuffer(IndexedModel model) {
        InstancedDataHolder data = instancedData.get(model);
        if (data != null) {
            return data.getOrCreateBuffer();
        } else {
            return null;
        }
    }

    public static class InstancedDataHolder {
        private final List<TransformInstanceData> data = new ArrayList<>();
        private ByteBuffer buffer;
        private boolean dirty;

        public TransformInstanceData get(int index) {
            return data.get(index);
        }

        public void add(TransformInstanceData data) {
            this.data.add(data);
            markDirty();
        }

        public int size() {
            return data.size();
        }

        public ByteBuffer getOrCreateBuffer() {
            if (this.dirty) {
                this.buffer = ByteBuffer.allocate(size() * TransformInstanceData.getSizeInBytes());
                for (int i = 0; i < size(); i++) {
                    TransformInstanceData instanceData = get(i);
                    instanceData.getTransform().get(buffer);
                }
                this.dirty = false;
            }
            return this.buffer;
        }

        public void markDirty() {
            dirty = true;
        }
    }
}
