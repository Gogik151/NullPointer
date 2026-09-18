package ru.white.nullpointer.mixin.client;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.white.nullpointer.client.render.RenderGlitchManager;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Shadow
    public abstract float getYaw();

    @Shadow
    public abstract float getPitch();

    @Inject(method = "update", at = @At("TAIL"))
    private void onUpdateTail(World area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        RenderGlitchManager glitch = RenderGlitchManager.getInstance();
        if (glitch.isGlitching()) {
            float pitchOffset = glitch.getShakePitch();
            float yawOffset = glitch.getShakeYaw();
            this.setRotation(this.getYaw() + yawOffset, this.getPitch() + pitchOffset);
        }
    }
}
