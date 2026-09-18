package ru.white.nullpointer.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.white.nullpointer.client.render.RenderGlitchManager;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RenderGlitchManager glitch = RenderGlitchManager.getInstance();
        if (glitch.isGlitching()) {
            context.getMatrices().pushMatrix();
            context.getMatrices().translate((float) glitch.getHudJitterX(), (float) glitch.getHudJitterY());
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderTail(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RenderGlitchManager glitch = RenderGlitchManager.getInstance();
        if (glitch.isGlitching()) {
            context.getMatrices().popMatrix();
        }
    }
}
