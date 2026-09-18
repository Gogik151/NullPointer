package ru.white.nullpointer.client.render.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class MimicModel extends EntityModel<LivingEntityRenderState> {
    private final ModelPart root;
    private final ModelPart torso;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public MimicModel(ModelPart root) {
        super(root);
        this.root = root;
        this.torso = root.getChild("torso");
        ModelPart neck = torso.getChild("neck");
        this.head = neck.getChild("head");
        this.leftArm = torso.getChild("left_arm");
        this.rightArm = torso.getChild("right_arm");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        // Twisted, hunched torso (asymmetric shoulders)
        ModelPartData torso = root.addChild("torso",
                ModelPartBuilder.create()
                        .uv(0, 32).cuboid(-4.5f, -14.0f, -2.5f, 9.0f, 14.0f, 5.0f)
                        .uv(32, 48).cuboid(-4.0f, -16.0f, -1.0f, 3.0f, 3.0f, 3.0f), // Raised twisted left shoulder hump
                ModelTransform.origin(0.0f, 10.0f, 0.0f));

        // Crooked Neck
        ModelPartData neck = torso.addChild("neck",
                ModelPartBuilder.create().uv(0, 16).cuboid(-1.5f, -4.0f, -1.5f, 3.0f, 4.0f, 3.0f),
                ModelTransform.origin(0.5f, -14.0f, -0.5f));

        // Head (smooth, erased, featureless face cavity)
        neck.addChild("head",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f)
                        .uv(32, 0).cuboid(-3.5f, -7.5f, -4.2f, 7.0f, 7.0f, 1.0f), // Flat static noise face plate
                ModelTransform.origin(0.0f, -4.0f, 0.0f));

        // Left Arm (Grotesquely elongated past knees, bent backward at impossible angle)
        ModelPartData leftArm = torso.addChild("left_arm",
                ModelPartBuilder.create()
                        .uv(48, 16).cuboid(-1.0f, 0.0f, -1.0f, 2.5f, 22.0f, 2.5f)
                        .uv(58, 16).cuboid(-1.5f, 22.0f, -0.5f, 3.5f, 6.0f, 1.0f), // Splayed distorted fingers
                ModelTransform.origin(5.0f, -13.0f, 0.5f));

        // Right Arm (Elongated, clawed, reaching forward in grasping twitch)
        ModelPartData rightArm = torso.addChild("right_arm",
                ModelPartBuilder.create()
                        .uv(48, 16).cuboid(-1.5f, 0.0f, -1.0f, 2.5f, 20.0f, 2.5f)
                        .uv(58, 16).cuboid(-2.0f, 20.0f, -0.5f, 3.5f, 6.0f, 1.0f), // Splayed clawed fingers
                ModelTransform.origin(-5.0f, -11.5f, -0.5f));

        // Left Leg (The dragged limp leg)
        root.addChild("left_leg",
                ModelPartBuilder.create()
                        .uv(0, 64).cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 15.0f, 3.0f)
                        .uv(16, 64).cuboid(-2.0f, 14.5f, -4.0f, 4.0f, 2.0f, 6.5f),
                ModelTransform.origin(2.2f, 10.0f, 0.0f));

        // Right Leg (The lurching leg)
        root.addChild("right_leg",
                ModelPartBuilder.create()
                        .uv(0, 64).cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 14.0f, 3.0f)
                        .uv(16, 64).cuboid(-2.0f, 13.5f, -3.5f, 4.0f, 2.0f, 6.0f),
                ModelTransform.origin(-2.2f, 10.0f, 0.0f));

        return TexturedModelData.of(modelData, 128, 128);
    }

    @Override
    public void setAngles(LivingEntityRenderState state) {
        super.setAngles(state);

        float time = state.age;

        // 1. Crooked hunched posture with subtle respiratory tremor
        torso.pitch = 0.22f + MathHelper.sin(time * 0.07f) * 0.04f;
        torso.roll = 0.10f;

        // 2. Violent Uncanny Head Snaps (Jacob's Ladder / neck snap effect)
        int snapFrame = (int) time;
        if (snapFrame % 23 < 3) {
            // Snapped 70 degrees onto shoulder!
            head.roll = 0.85f;
            head.yaw = 0.35f;
            head.pitch = 0.20f;
        } else if (snapFrame % 37 < 2) {
            // Snapped 60 degrees down!
            head.roll = -0.55f;
            head.pitch = 0.65f;
        } else {
            // Staring at player
            head.yaw = state.relativeHeadYaw * ((float) Math.PI / 180.0f) * 0.6f;
            head.pitch = state.pitch * ((float) Math.PI / 180.0f) * 0.6f;
            head.roll = MathHelper.sin(time * 0.12f) * 0.06f;
        }

        // 3. Backward Twisted Left Arm vs Grasping Right Arm
        leftArm.pitch = -0.30f + MathHelper.sin(time * 0.09f) * 0.12f;
        leftArm.roll = 0.25f;

        rightArm.pitch = 0.45f + MathHelper.sin(time * 0.14f) * 0.18f; // Reaching out toward camera
        rightArm.roll = -0.15f;

        // 4. Asymmetric Dragging Limp Gait
        float limbPos = state.limbSwingAnimationProgress;
        float limbAmp = state.limbSwingAmplitude;

        if (limbAmp > 0.05f) {
            // Right leg takes fast, irregular lurching steps
            rightLeg.pitch = MathHelper.cos(limbPos * 1.2f) * 1.4f * limbAmp;

            // Left leg is dragged behind: stays dragged back, then violently swings outward
            float dragCycle = MathHelper.sin(limbPos * 0.6f);
            leftLeg.pitch = -0.4f * limbAmp + dragCycle * 0.3f * limbAmp;
            leftLeg.roll = 0.35f * limbAmp; // Swings outward like a dislocated limp limb
            leftLeg.yaw = -0.25f * limbAmp;

            // Whole body lurches with each step
            torso.originY = 10.0f + Math.abs(MathHelper.sin(limbPos * 1.2f)) * 2.5f * limbAmp;
        } else {
            // Idle trembling stance
            rightLeg.pitch = 0.05f;
            leftLeg.pitch = -0.10f;
            leftLeg.roll = 0.18f;
            leftLeg.yaw = -0.12f;
            torso.originY = 10.0f;
        }
    }
}
