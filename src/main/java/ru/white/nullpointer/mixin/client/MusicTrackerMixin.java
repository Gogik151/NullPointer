package ru.white.nullpointer.mixin.client;

import net.minecraft.client.sound.MusicTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.white.nullpointer.client.render.RenderGlitchManager;

@Mixin(MusicTracker.class)
public abstract class MusicTrackerMixin {

    @Shadow
    public abstract void stop();

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (RenderGlitchManager.getInstance().isGlitching()) {
            this.stop();
        }
    }
}
