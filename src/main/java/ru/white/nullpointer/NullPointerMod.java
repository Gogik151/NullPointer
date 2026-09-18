package ru.white.nullpointer;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import ru.white.nullpointer.config.NullPointerConfig;
import ru.white.nullpointer.entity.ModEntities;
import ru.white.nullpointer.network.ModNetworking;
import ru.white.nullpointer.story.NightmareEngine;

public class NullPointerMod implements ModInitializer {
    public static final String MOD_ID = "nullpointer";

    @Override
    public void onInitialize() {
        System.out.println("[NullPointer] Initializing Project: NullPointer (Psychological Horror Engine)...");

        // Load config
        NullPointerConfig.load();

        // Register Network Payloads
        ModNetworking.registerCommon();

        // Register Entities
        ModEntities.registerAll();

        // Register Sound Events
        ru.white.nullpointer.sound.ModSounds.registerAll();

        // Server tick listener for NightmareEngine
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getRegistryKey() == net.minecraft.world.World.OVERWORLD) {
                NightmareEngine.getOrCreate(world.getServer()).tick(world);
            }
        });

        // Server command registration
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("np")
                    // /np day <1..7>
                    .then(CommandManager.literal("day")
                            .then(CommandManager.argument("days", IntegerArgumentType.integer(1, 100))
                                    .executes(ctx -> {
                                        int days = IntegerArgumentType.getInteger(ctx, "days");
                                        NightmareEngine engine = NightmareEngine.getOrCreate(ctx.getSource().getServer());
                                        engine.setDaysSurvived(days, ctx.getSource().getServer());
                                        ctx.getSource().sendFeedback(() -> Text.literal("§c[NullPointer] Set progression to Day " + days + " (" + engine.getPhase().getDisplayName() + ")"), true);
                                        return 1;
                                    })
                            )
                    )
                    // /np phase
                    .then(CommandManager.literal("phase")
                            .executes(ctx -> {
                                NightmareEngine engine = NightmareEngine.getOrCreate(ctx.getSource().getServer());
                                ctx.getSource().sendFeedback(() -> Text.literal("§c[NullPointer] Current: Day " + engine.getDaysSurvived() + " | " + engine.getPhase().getDisplayName()), false);
                                return 1;
                            })
                    )
                    // /np note <phase3|phase4>
                    .then(CommandManager.literal("note")
                            .then(CommandManager.literal("phase3")
                                    .executes(ctx -> {
                                        ru.white.nullpointer.meta.LoreManager.spawnPhase3Note();
                                        ctx.getSource().sendFeedback(() -> Text.literal("§a[NullPointer] Spawned Phase 3 note (LOG_0x77.txt)."), false);
                                        return 1;
                                    })
                            )
                            .then(CommandManager.literal("phase4")
                                    .executes(ctx -> {
                                        ru.white.nullpointer.meta.LoreManager.spawnPhase4Note(ctx.getSource().getServer().getName());
                                        ctx.getSource().sendFeedback(() -> Text.literal("§a[NullPointer] Spawned Phase 4 note (DO_NOT_OPEN.txt)."), false);
                                        return 1;
                                    })
                            )
                    )
                    // /np wallpaper
                    .then(CommandManager.literal("wallpaper")
                            .executes(ctx -> {
                                ru.white.nullpointer.meta.MetaHorrorManager.triggerHorrorWallpaper();
                                ctx.getSource().sendFeedback(() -> Text.literal("§c[NullPointer] Wallpaper change triggered."), false);
                                return 1;
                            })
                    )
                    // /np spawn <static_walker|mimic>
                    .then(CommandManager.literal("spawn")
                            .then(CommandManager.argument("type", StringArgumentType.word())
                                    .suggests((c, b) -> {
                                        b.suggest("static_walker");
                                        b.suggest("mimic");
                                        return b.buildFuture();
                                    })
                                    .executes(ctx -> {
                                        String type = StringArgumentType.getString(ctx, "type");
                                        ServerPlayerEntity player = ctx.getSource().getPlayer();
                                        if (player == null) return 0;

                                        BlockPos pos = player.getBlockPos().offset(player.getHorizontalFacing(), 5);
                                        if ("static_walker".equalsIgnoreCase(type)) {
                                            var e = ModEntities.STATIC_WALKER.create(ctx.getSource().getWorld(), SpawnReason.COMMAND);
                                            if (e != null) {
                                                e.refreshPositionAndAngles(pos.getX() + 0.5, (double) pos.getY(), pos.getZ() + 0.5, player.getYaw() + 180.0f, 0.0f);
                                                ctx.getSource().getWorld().spawnEntity(e);
                                                ctx.getSource().sendFeedback(() -> Text.literal("§4[NullPointer] Spawned The Static Walker."), false);
                                            }
                                        } else if ("mimic".equalsIgnoreCase(type)) {
                                            var e = ModEntities.MIMIC.create(ctx.getSource().getWorld(), SpawnReason.COMMAND);
                                            if (e != null) {
                                                e.refreshPositionAndAngles(pos.getX() + 0.5, (double) pos.getY(), pos.getZ() + 0.5, player.getYaw() + 180.0f, 0.0f);
                                                ctx.getSource().getWorld().spawnEntity(e);
                                                ctx.getSource().sendFeedback(() -> Text.literal("§c[NullPointer] Spawned The Mimic."), false);
                                            }
                                        }
                                        return 1;
                                    })
                            )
                    )
            );
        });
    }
}

