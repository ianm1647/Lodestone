package team.lodestar.lodestone.systems.rendering.trail;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector4f;

public class TrailPoint {
    private Vec3 position;
    private int age;

    public TrailPoint(Vec3 position, int age) {
        this.position = position;
        this.age = age;
    }

    public TrailPoint(Vec3 position) {
        this(position, 0);
    }

    public Vector4f getMatrixPosition() {
        Vec3 position = getPosition();
        return new Vector4f((float) position.x, (float) position.y, (float) position.z, 1.0f);
    }

    public Vec3 getPosition() {
        return position;
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public void move(Vec3 offset) {
        setPosition(getPosition().add(offset));
    }

    public int getAge() {
        return age;
    }

    public TrailPoint lerp(TrailPoint trailPoint, float delta) {
        Vec3 position = getPosition();
        return new TrailPoint(position.lerp(trailPoint.position, delta), age);
    }

    public void tick() {
        age++;
    }
}