package ru.white.nullpointer.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class VoidWalkerModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart spineLower;
    private final ModelPart spineUpper;
    private final ModelPart voidCore;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart tendril1;
    private final ModelPart tendril2;
    private final ModelPart tendril3;
    private final ModelPart tendril4;
    private final ModelPart leftArmUpper;
    private final ModelPart leftArmLower;
    private final ModelPart rightArmUpper;
    private final ModelPart rightArmLower;
    private final ModelPart leftThigh;
    private final ModelPart leftShin;
    private final ModelPart rightThigh;
    private final ModelPart rightShin;

    public VoidWalkerModel(ModelPart root) {
        super(root);
        this.root = root;
        this.spineLower = root.getChild("spine_lower");
        this.spineUpper = spineLower.getChild("spine_upper");
        this.voidCore = spineUpper.getChild("void_core");

        ModelPart neck = spineUpper.getChild("neck");
        this.head = neck.getChild("head");
        this.jaw = head.getChild("jaw");

        this.tendril1 = spineUpper.getChild("tendril_1");
        this.tendril2 = spineUpper.getChild("tendril_2");
        this.tendril3 = spineUpper.getChild("tendril_3");
        this.tendril4 = spineUpper.getChild("tendril_4");

        ModelPart leftShoulder = spineUpper.getChild("left_arm");
        this.leftArmUpper = leftShoulder.getChild("left_arm_upper");
        this.leftArmLower = leftArmUpper.getChild("left_arm_lower");

        ModelPart rightShoulder = spineUpper.getChild("right_arm");
        this.rightArmUpper = rightShoulder.getChild("right_arm_upper");
        this.rightArmLower = rightArmUpper.getChild("right_arm_lower");

        this.leftThigh = spineLower.getChild("left_leg");
        this.leftShin = leftThigh.getChild("left_shin");

        this.rightThigh = spineLower.getChild("right_leg");
        this.rightShin = rightThigh.getChild("right_shin");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        // Lower Spine
        ModelPartData spineLower = root.addChild("spine_lower",
                ModelPartBuilder.create()
                        .uv(0, 32).cuboid(-2.0f, 0.0f, -1.5f, 4.0f, 8.0f, 3.0f),
                ModelTransform.origin(0.0f, -8.0f, 0.0f));

        // Upper Spine (arched forward)
        ModelPartData spineUpper = spineLower.addChild("spine_upper",
                ModelPartBuilder.create()
                        .uv(0, 16).cuboid(-2.0f, -12.0f, -2.0f, 4.0f, 12.0f, 4.0f),
                ModelTransform.origin(0.0f, 0.0f, 0.0f));

        // Pulsing Void Core
        spineUpper.addChild("void_core",
                ModelPartBuilder.create()
                        .uv(48, 0).cuboid(-2.0f, -7.0f, -2.0f, 4.0f, 4.0f, 4.0f),
                ModelTransform.origin(0.0f, 0.0f, 0.0f));

        // Ribs (4 skeletal pairs forming a cage around the core)
        spineUpper.addChild("rib_1_l", ModelPartBuilder.create().uv(32, 20).cuboid(-6.0f, 0.0f, -3.5f, 6.0f, 1.5f, 5.0f), ModelTransform.origin(-1.5f, -10.5f, 0.0f));
        spineUpper.addChild("rib_1_r", ModelPartBuilder.create().uv(32, 20).cuboid(0.0f, 0.0f, -3.5f, 6.0f, 1.5f, 5.0f), ModelTransform.origin(1.5f, -10.5f, 0.0f));

        spineUpper.addChild("rib_2_l", ModelPartBuilder.create().uv(32, 28).cuboid(-7.0f, 0.0f, -4.0f, 7.0f, 1.5f, 6.0f), ModelTransform.origin(-1.5f, -8.0f, 0.0f));
        spineUpper.addChild("rib_2_r", ModelPartBuilder.create().uv(32, 28).cuboid(0.0f, 0.0f, -4.0f, 7.0f, 1.5f, 6.0f), ModelTransform.origin(1.5f, -8.0f, 0.0f));

        spineUpper.addChild("rib_3_l", ModelPartBuilder.create().uv(32, 36).cuboid(-6.0f, 0.0f, -3.5f, 6.0f, 1.5f, 5.0f), ModelTransform.origin(-1.5f, -5.5f, 0.0f));
        spineUpper.addChild("rib_3_r", ModelPartBuilder.create().uv(32, 36).cuboid(0.0f, 0.0f, -3.5f, 6.0f, 1.5f, 5.0f), ModelTransform.origin(1.5f, -5.5f, 0.0f));

        spineUpper.addChild("rib_4_l", ModelPartBuilder.create().uv(32, 44).cuboid(-5.0f, 0.0f, -3.0f, 5.0f, 1.5f, 4.0f), ModelTransform.origin(-1.5f, -3.0f, 0.0f));
        spineUpper.addChild("rib_4_r", ModelPartBuilder.create().uv(32, 44).cuboid(0.0f, 0.0f, -3.0f, 5.0f, 1.5f, 4.0f), ModelTransform.origin(1.5f, -3.0f, 0.0f));

        // 4 Sinuous Void Tendrils protruding from upper back
        spineUpper.addChild("tendril_1", ModelPartBuilder.create().uv(56, 16).cuboid(-0.75f, -0.75f, 0.0f, 1.5f, 1.5f, 16.0f), ModelTransform.origin(-2.5f, -11.0f, 2.0f));
        spineUpper.addChild("tendril_2", ModelPartBuilder.create().uv(56, 16).cuboid(-0.75f, -0.75f, 0.0f, 1.5f, 1.5f, 16.0f), ModelTransform.origin(2.5f, -11.0f, 2.0f));
        spineUpper.addChild("tendril_3", ModelPartBuilder.create().uv(56, 36).cuboid(-0.75f, -0.75f, 0.0f, 1.5f, 1.5f, 13.0f), ModelTransform.origin(-3.0f, -7.0f, 2.0f));
        spineUpper.addChild("tendril_4", ModelPartBuilder.create().uv(56, 36).cuboid(-0.75f, -0.75f, 0.0f, 1.5f, 1.5f, 13.0f), ModelTransform.origin(3.0f, -7.0f, 2.0f));

        // Neck
        ModelPartData neck = spineUpper.addChild("neck",
                ModelPartBuilder.create()
                        .uv(16, 16).cuboid(-1.5f, -5.0f, -1.5f, 3.0f, 5.0f, 3.0f),
                ModelTransform.origin(0.0f, -12.0f, -1.0f));

        // Head (elongated skull with hollow sockets)
        ModelPartData head = neck.addChild("head",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-3.5f, -8.0f, -4.5f, 7.0f, 8.0f, 8.0f)
                        .uv(24, 0).cuboid(-3.0f, -7.5f, -5.2f, 6.0f, 4.0f, 1.0f), // Brow & sunken eye ridges
                ModelTransform.origin(0.0f, -5.0f, 0.0f));

        // Unhinged lower jaw with teeth
        head.addChild("jaw",
                ModelPartBuilder.create()
                        .uv(0, 48).cuboid(-3.0f, 0.0f, -4.5f, 6.0f, 3.0f, 7.0f),
                ModelTransform.origin(0.0f, -0.5f, 0.0f));

        // Left Arm (Long multi-jointed arm with razor claws)
        ModelPartData leftArm = spineUpper.addChild("left_arm", ModelPartBuilder.create(), ModelTransform.origin(4.5f, -11.0f, 0.0f));
        ModelPartData leftArmUpper = leftArm.addChild("left_arm_upper",
                ModelPartBuilder.create().uv(16, 32).cuboid(-1.25f, 0.0f, -1.25f, 2.5f, 16.0f, 2.5f),
                ModelTransform.origin(0.0f, 0.0f, 0.0f));
        ModelPartData leftArmLower = leftArmUpper.addChild("left_arm_lower",
                ModelPartBuilder.create().uv(26, 32).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 16.0f, 2.0f),
                ModelTransform.origin(0.0f, 16.0f, 0.0f));
        leftArmLower.addChild("left_claws",
                ModelPartBuilder.create().uv(36, 0).cuboid(-1.5f, 0.0f, -0.25f, 3.0f, 8.0f, 0.5f),
                ModelTransform.origin(0.0f, 16.0f, 0.0f));

        // Right Arm
        ModelPartData rightArm = spineUpper.addChild("right_arm", ModelPartBuilder.create(), ModelTransform.origin(-4.5f, -11.0f, 0.0f));
        ModelPartData rightArmUpper = rightArm.addChild("right_arm_upper",
                ModelPartBuilder.create().uv(16, 32).cuboid(-1.25f, 0.0f, -1.25f, 2.5f, 16.0f, 2.5f),
                ModelTransform.origin(0.0f, 0.0f, 0.0f));
        ModelPartData rightArmLower = rightArmUpper.addChild("right_arm_lower",
                ModelPartBuilder.create().uv(26, 32).cuboid(-1.0f, 0.0f, -1.0f, 2.0f, 16.0f, 2.0f),
                ModelTransform.origin(0.0f, 16.0f, 0.0f));
        rightArmLower.addChild("right_claws",
                ModelPartBuilder.create().uv(36, 0).cuboid(-1.5f, 0.0f, -0.25f, 3.0f, 8.0f, 0.5f),
                ModelTransform.origin(0.0f, 16.0f, 0.0f));

        // Left Leg (Reverse-jointed digitigrade leg)
        ModelPartData leftLeg = spineLower.addChild("left_leg",
                ModelPartBuilder.create().uv(0, 64).cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 16.0f, 3.0f),
                ModelTransform.origin(2.5f, 8.0f, 0.0f));
        ModelPartData leftShin = leftLeg.addChild("left_shin",
                ModelPartBuilder.create().uv(12, 64).cuboid(-1.25f, 0.0f, -1.25f, 2.5f, 16.0f, 2.5f),
                ModelTransform.origin(0.0f, 16.0f, 0.0f));
        leftShin.addChild("left_foot",
                ModelPartBuilder.create().uv(24, 64).cuboid(-1.5f, 0.0f, -4.0f, 3.0f, 2.0f, 6.0f),
                ModelTransform.origin(0.0f, 16.0f, 0.0f));

        // Right Leg
        ModelPartData rightLeg = spineLower.addChild("right_leg",
                ModelPartBuilder.create().uv(0, 64).cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 16.0f, 3.0f),
                ModelTransform.origin(-2.5f, 8.0f, 0.0f));
        ModelPartData rightShin = rightLeg.addChild("right_shin",
                ModelPartBuilder.create().uv(12, 64).cuboid(-1.25f, 0.0f, -1.25f, 2.5f, 16.0f, 2.5f),
                ModelTransform.origin(0.0f, 16.0f, 0.0f));
        rightShin.addChild("right_foot",
                ModelPartBuilder.create().uv(24, 64).cuboid(-1.5f, 0.0f, -4.0f, 3.0f, 2.0f, 6.0f),
                ModelTransform.origin(0.0f, 16.0f, 0.0f));

        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(LivingEntityRenderState state) {
        super.setAngles(state);

        float time = state.age;

        // 1. Sinuous Undulating Tendrils (Phase-shifted living shadow tentacles)
        tendril1.pitch = MathHelper.sin(time * 0.09f + 0.4f) * 0.35f - 0.45f;
        tendril1.yaw = MathHelper.cos(time * 0.07f) * 0.28f - 0.20f;

        tendril2.pitch = MathHelper.sin(time * 0.09f + 1.8f) * 0.35f - 0.45f;
        tendril2.yaw = MathHelper.cos(time * 0.07f + 1.2f) * 0.28f + 0.20f;

        tendril3.pitch = MathHelper.sin(time * 0.08f + 2.5f) * 0.30f - 0.25f;
        tendril3.yaw = MathHelper.cos(time * 0.06f + 2.1f) * 0.35f - 0.30f;

        tendril4.pitch = MathHelper.sin(time * 0.08f + 3.8f) * 0.30f - 0.25f;
        tendril4.yaw = MathHelper.cos(time * 0.06f + 3.4f) * 0.35f + 0.30f;

        // 2. Pulsing Void Core Breathing
        float pulse = 1.0f + MathHelper.sin(time * 0.16f) * 0.22f;
        voidCore.xScale = pulse;
        voidCore.yScale = pulse;
        voidCore.zScale = pulse;

        // 3. Unhinged Lower Jaw (Unhinges and twitches)
        jaw.pitch = 0.35f + MathHelper.sin(time * 0.10f) * 0.18f;
        if (((int) time) % 29 < 4) {
            jaw.pitch += 0.45f; // Sudden wide gaping maw!
        }

        // 4. Head Tracking with Instantaneous Jacob's Ladder Snaps
        head.yaw = state.relativeHeadYaw * ((float) Math.PI / 180.0f);
        head.pitch = state.pitch * ((float) Math.PI / 180.0f);
        if (((int) time) % 19 == 0) {
            head.roll = 0.40f;
            head.yaw += 0.25f;
        } else if (((int) time) % 23 == 0) {
            head.roll = -0.35f;
            head.pitch += 0.30f;
        } else {
            head.roll = 0.0f;
        }

        // 5. Digitigrade Walking Gait vs Freezing Stalk
        float limbPos = state.limbSwingAnimationProgress;
        float limbAmp = state.limbSwingAmplitude;

        if (limbAmp > 0.05f) {
            // Uncanny spider-like strides
            rightThigh.pitch = MathHelper.cos(limbPos * 0.6662f) * 1.4f * limbAmp;
            rightShin.pitch = MathHelper.sin(limbPos * 0.6662f) * 0.7f * limbAmp + 0.3f;

            leftThigh.pitch = MathHelper.cos(limbPos * 0.6662f + (float) Math.PI) * 1.4f * limbAmp;
            leftShin.pitch = MathHelper.sin(limbPos * 0.6662f + (float) Math.PI) * 0.7f * limbAmp + 0.3f;

            rightArmUpper.pitch = MathHelper.cos(limbPos * 0.6662f + (float) Math.PI) * 1.2f * limbAmp;
            leftArmUpper.pitch = MathHelper.cos(limbPos * 0.6662f) * 1.2f * limbAmp;
        } else {
            // Rigid frozen upright stance
            rightThigh.pitch = 0.0f;
            rightShin.pitch = 0.0f;
            leftThigh.pitch = 0.0f;
            leftShin.pitch = 0.0f;

            rightArmUpper.pitch = 0.05f;
            rightArmUpper.roll = 0.08f;
            leftArmUpper.pitch = 0.05f;
            leftArmUpper.roll = -0.08f;
        }
    }
}
