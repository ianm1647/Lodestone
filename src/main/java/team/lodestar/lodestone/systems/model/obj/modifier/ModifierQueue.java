package team.lodestar.lodestone.systems.model.obj.modifier;


public interface ModifierQueue {
    void queueEarlyModifier(ModelModifier modifier);
    void queueModifier(ModelModifier modifier);
}
