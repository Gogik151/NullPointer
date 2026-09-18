package ru.white.nullpointer.client;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import ru.white.nullpointer.client.gui.FakeCrashScreen;
import ru.white.nullpointer.client.render.RenderGlitchManager;
import ru.white.nullpointer.client.render.entity.MimicRenderer;
import ru.white.nullpointer.client.render.entity.StaticWalkerRenderer;
import ru.white.nullpointer.client.render.model.MimicModel;
import ru.white.nullpointer.client.render.model.ModModelLayers;
import ru.white.nullpointer.client.render.model.VoidWalkerModel;
import ru.white.nullpointer.config.NullPointerConfig;
import ru.white.nullpointer.entity.ModEntities;
import ru.white.nullpointer.meta.MetaHorrorManager;
import ru.white.nullpointer.network.ModNetworking;

@Environment(EnvType.CLIENT)
public class NullPointerClient implements ClientModInitializer {
    public static final String MOD_ID = "nullpointer";
    private static KeyBinding triggerScareKey;

    @Override
    public void onInitializeClient() {
        System.out.println("[NullPointer] Initializing Client Engine...");

        // Load config
        NullPointerConfig.load();

        // Register S2C Network Handlers
        ModNetworking.registerClient();

        // Register Custom 3D Model Layers
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.VOID_WALKER, VoidWalkerModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.MIMIC, MimicModel::getTexturedModelData);

        // Register Entity Renderers
        EntityRendererRegistry.register(ModEntities.STATIC_WALKER, StaticWalkerRenderer::new);
        EntityRendererRegistry.register(ModEntities.MIMIC, MimicRenderer::new);

        // Tick Glitch and Shake animations
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            RenderGlitchManager.getInstance().tick();

            // Check hotkey trigger
            while (triggerScareKey != null && triggerScareKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new FakeCrashScreen(FakeCrashScreen.Style.MINECRAFT_CRASH));
                }
            }
        });

        // Optional debug hotkey (F8)
        triggerScareKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.nullpointer.trigger_test",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F8,
                KeyBinding.Category.MISC
        ));

        // Register client commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("nullpointer")
                    // /nullpointer crash [mc|bsod]
                    .then(ClientCommandManager.literal("crash")
                            .executes(ctx -> {
                                MinecraftClient.getInstance().send(() -> {
                                    MinecraftClient.getInstance().setScreen(new FakeCrashScreen(FakeCrashScreen.Style.MINECRAFT_CRASH));
                                });
                                return 1;
                            })
                            .then(ClientCommandManager.argument("style", StringArgumentType.word())
                                    .suggests((c, b) -> {
                                        b.suggest("mc");
                                        b.suggest("bsod");
                                        return b.buildFuture();
                                    })
                                    .executes(ctx -> {
                                        String styleStr = StringArgumentType.getString(ctx, "style");
                                        FakeCrashScreen.Style style = "bsod".equalsIgnoreCase(styleStr) ?
                                                FakeCrashScreen.Style.BSOD : FakeCrashScreen.Style.MINECRAFT_CRASH;
                                        MinecraftClient.getInstance().send(() -> {
                                            MinecraftClient.getInstance().setScreen(new FakeCrashScreen(style));
                                        });
                                        return 1;
                                    })
                            )
                    )
                    // /nullpointer glitch <ticks> <intensity>
                    .then(ClientCommandManager.literal("glitch")
                            .then(ClientCommandManager.argument("ticks", IntegerArgumentType.integer(1, 1200))
                                    .then(ClientCommandManager.argument("intensity", FloatArgumentType.floatArg(0.1f, 5.0f))
                                            .executes(ctx -> {
                                                int ticks = IntegerArgumentType.getInteger(ctx, "ticks");
                                                float intensity = FloatArgumentType.getFloat(ctx, "intensity");
                                                RenderGlitchManager.getInstance().triggerGlitch(ticks, intensity);
                                                ctx.getSource().sendFeedback(Text.literal("§c[NullPointer] Glitch triggered: " + ticks + " ticks at " + intensity + "x"));
                                                return 1;
                                            })
                                    )
                            )
                    )
                    // /nullpointer jumpscare <ticks>
                    .then(ClientCommandManager.literal("jumpscare")
                            .then(ClientCommandManager.argument("ticks", IntegerArgumentType.integer(5, 200))
                                    .executes(ctx -> {
                                        int ticks = IntegerArgumentType.getInteger(ctx, "ticks");
                                        RenderGlitchManager.getInstance().triggerJumpscare(ticks);
                                        return 1;
                                    })
                            )
                    )
                    // /nullpointer note [custom text]
                    .then(ClientCommandManager.literal("note")
                            .executes(ctx -> {
                                MetaHorrorManager.spawnDesktopNote(null);
                                ctx.getSource().sendFeedback(Text.literal("§c[NullPointer] Desktop note created. Check your Desktop."));
                                return 1;
                            })
                            .then(ClientCommandManager.argument("text", StringArgumentType.greedyString())
                                    .executes(ctx -> {
                                        String customText = StringArgumentType.getString(ctx, "text");
                                        MetaHorrorManager.spawnDesktopNote(customText);
                                        ctx.getSource().sendFeedback(Text.literal("§c[NullPointer] Desktop note created with custom text."));
                                        return 1;
                                    })
                            )
                    )
                    // /nullpointer reload
                    .then(ClientCommandManager.literal("reload")
                            .executes(ctx -> {
                                NullPointerConfig.load();
                                ctx.getSource().sendFeedback(Text.literal("§a[NullPointer] Config reloaded successfully."));
                                return 1;
                            })
                    )
            );
        });

        System.out.println("[NullPointer] Client Engine Ready!");
    }
}
