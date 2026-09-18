package ru.white.nullpointer.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import ru.white.nullpointer.client.gui.FakeCrashScreen;
import ru.white.nullpointer.client.render.RenderGlitchManager;

public class ModNetworking {

    public static void registerCommon() {
        PayloadTypeRegistry.playS2C().register(TriggerCrashPayload.ID, TriggerCrashPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(TriggerGlitchPayload.ID, TriggerGlitchPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(TriggerParalysisPayload.ID, TriggerParalysisPayload.CODEC);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(TriggerCrashPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                FakeCrashScreen.Style style = "bsod".equalsIgnoreCase(payload.style()) ?
                        FakeCrashScreen.Style.BSOD : FakeCrashScreen.Style.MINECRAFT_CRASH;
                MinecraftClient.getInstance().setScreen(new FakeCrashScreen(style));
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(TriggerGlitchPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                RenderGlitchManager.getInstance().triggerGlitch(payload.ticks(), payload.intensity());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(TriggerParalysisPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                RenderGlitchManager.getInstance().setParalyzed(payload.ticks());
            });
        });
    }

    public static void sendCrash(ServerPlayerEntity player, String style) {
        ServerPlayNetworking.send(player, new TriggerCrashPayload(style));
    }

    public static void sendGlitch(ServerPlayerEntity player, int ticks, float intensity) {
        ServerPlayNetworking.send(player, new TriggerGlitchPayload(ticks, intensity));
    }

    public static void sendParalysis(ServerPlayerEntity player, int ticks) {
        ServerPlayNetworking.send(player, new TriggerParalysisPayload(ticks));
    }
}
