package team.lodestar.lodestone.registry.client;

import team.lodestar.lodestone.systems.rendering.instancing.InstancedData;
import team.lodestar.lodestone.systems.rendering.instancing.TransformInstanceData;

import java.util.ArrayList;
import java.util.List;

public class LodestoneInstancedDataTypes {
    private static final List<InstancedData<?>> INSTANCED_DATA_TYPES = new ArrayList<>();

    //public static final InstancedData<TransformInstanceData>

    public static <T> InstancedData<T> register(String name, InstancedData<T> instancedData) {
        INSTANCED_DATA_TYPES.add(instancedData);
        return instancedData;
    }
}
