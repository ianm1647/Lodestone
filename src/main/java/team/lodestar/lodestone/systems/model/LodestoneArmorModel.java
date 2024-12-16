package team.lodestar.lodestone.systems.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

/**
 * A simple implementation of an armor tied humanoid model.
 * Designing these in blockbench is quite annoying, but from my personal experience it can be made simpler with the following steps;
 * The Root* of each individual part of your armor model should match in name to the ModelParts defined below. That way, the exported code will be really easy to connect to the LodestoneArmorModel utility.
 * The Pivot Points of each Actual Root* of your individual parts of the armor model should match to that of the player's model.
 * Export the model as a java file and paste what it gives you into {@link LodestoneArmorModel#createArmorModel}.
 * Remove the pose-offsets of any part that is directly tied to the player model and copies it's properties.
 * Hopefully you should be good to go after that? All of this is optional it's just to make the process easier. If you plan to use LodestoneArmorModel definitely join the discord and ask questions.
 */
public class LodestoneArmorModel extends HumanoidModel<LivingEntity> {
    public EquipmentSlot slot;
    public ModelPart root, head, body, leftArm, rightArm, leggings, leftLegging, rightLegging, leftFoot, rightFoot;

    public LodestoneArmorModel(ModelPart root) {
        super(root);
        this.root = root;
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.leggings = root.getChild("leggings");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.leftLegging = root.getChild("left_legging");
        this.rightLegging = root.getChild("right_legging");
        this.leftFoot = root.getChild("left_foot");
        this.rightFoot = root.getChild("right_foot");
    }

    public static PartDefinition createHumanoidAlias(MeshDefinition mesh) {
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("leggings", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("head", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("left_legging", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("left_foot", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("right_legging", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("right_foot", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("left_arm", new CubeListBuilder(), PartPose.ZERO);
        root.addOrReplaceChild("right_arm", new CubeListBuilder(), PartPose.ZERO);
        return root;
    }

    public static LayerDefinition createArmorModel(ILodestoneArmorModelBuilder modelBuilder) {
        MeshDefinition mesh = HumanoidModel.createMesh(new CubeDeformation(0), 0);
        PartDefinition root = createHumanoidAlias(mesh);
        PartDefinition body = root.getChild("body");
        PartDefinition leggings = root.getChild("leggings");
        PartDefinition right_legging = root.getChild("right_legging");
        PartDefinition left_legging = root.getChild("left_legging");
        PartDefinition right_foot = root.getChild("right_foot");
        PartDefinition left_foot = root.getChild("left_foot");
        PartDefinition right_arm = root.getChild("right_arm");
        PartDefinition left_arm = root.getChild("left_arm");
        PartDefinition head = root.getChild("head");
        return modelBuilder.createArmorLayer(mesh, root, body, leggings, right_legging, left_legging, right_foot, left_foot, right_arm, left_arm, head);
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return slot == EquipmentSlot.HEAD ? ImmutableList.of(head) : ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> bodyParts() {
        if (slot == EquipmentSlot.CHEST) {
            return ImmutableList.of(body, leftArm, rightArm);
        } else if (slot == EquipmentSlot.LEGS) {
            return ImmutableList.of(leftLegging, rightLegging, leggings);
        } else if (slot == EquipmentSlot.FEET) {
            return ImmutableList.of(leftFoot, rightFoot);
        } else return ImmutableList.of();
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int p_350361_) {
        if (slot == EquipmentSlot.LEGS) {  //I don't know why this is needed, but it is.
            this.leggings.copyFrom(this.body);
            this.leftLegging.copyFrom(this.leftLeg);
            this.rightLegging.copyFrom(this.rightLeg);
        }
        super.renderToBuffer(matrixStack, vertexConsumer, packedLight, packedOverlay, p_350361_);
    }

    public void copyFromDefault(HumanoidModel model) {
        leggings.copyFrom(model.body);
        body.copyFrom(model.body);
        head.copyFrom(model.head);
        leftArm.copyFrom(model.leftArm);
        rightArm.copyFrom(model.rightArm);
        leftLegging.copyFrom(leftLeg);
        rightLegging.copyFrom(rightLeg);
        leftFoot.copyFrom(leftLeg);
        rightFoot.copyFrom(rightLeg);
    }

    public interface ILodestoneArmorModelBuilder {
        LayerDefinition createArmorLayer(MeshDefinition mesh, PartDefinition root, PartDefinition body, PartDefinition leggings, PartDefinition right_legging, PartDefinition left_legging, PartDefinition right_foot, PartDefinition left_foot, PartDefinition right_arm, PartDefinition left_arm, PartDefinition head);
    }
}