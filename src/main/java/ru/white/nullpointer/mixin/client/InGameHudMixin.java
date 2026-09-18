package ru.white.nullpointer.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.white.nullpointer.client.render.RenderGlitchManager;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        RenderGlitchManager glitch = RenderGlitchManager.getInstance();
        if (glitch.isGlitching()) {
            context.getMatrices().pushMatrix();
            context.getMatrices().translate((float) glitch.getHudJitterX(), (float) glitch.getHudJitterY());
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderTail(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        RenderGlitchManager glitch = RenderGlitchManager.getInstance();
        if (glitch.isGlitching()) {
            context.getMatrices().popMatrix();
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.getWindow() != null) {
            glitch.renderOverlay(context, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
        }
    }
}
