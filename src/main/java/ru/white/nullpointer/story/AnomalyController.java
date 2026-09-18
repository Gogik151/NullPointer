package ru.white.nullpointer.story;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import ru.white.nullpointer.entity.MimicEntity;
import ru.white.nullpointer.entity.ModEntities;
import ru.white.nullpointer.entity.StaticWalkerEntity;
import ru.white.nullpointer.meta.LoreManager;
import ru.white.nullpointer.meta.MetaHorrorManager;
import ru.white.nullpointer.network.ModNetworking;
import ru.white.nullpointer.sound.ModSounds;

import java.util.List;
import java.util.Random;

public class AnomalyController {
    private static final Random RANDOM = new Random();
    private static int tickCounter = 0;
    private static int targetInterval = 45; // 2.25s dynamic burst

    public static void tick(ServerWorld world, NightmareEngine engine) {
        if (++tickCounter < targetInterval) {
            return;
        }
        tickCounter = 0;
        // Unpredictable dynamic interval between 35 and 65 ticks (1.75s .. 3.25s)
        targetInterval = 35 + RANDOM.nextInt(31);

        NightmareEngine.Phase phase = engine.getPhase();
        long timeOfDay = world.getTimeOfDay() % 24000L;
        boolean isNight = (timeOfDay >= 13000 && timeOfDay <= 23000);

        for (ServerPlayerEntity player : world.getPlayers()) {
            if (player.isCreative() || player.isSpectator()) continue;

            // --- NIGHT 1 (PHASE 1): Whispers & Shadows ---
            if (phase.ordinal() >= NightmareEngine.Phase.PHASE_1_WHISPERS.ordinal()) {
                if (RANDOM.nextFloat() < 0.60f) {
                    extinguishNearbyTorch(world, player);
                }
                if (RANDOM.nextFloat() < 0.40f) {
                    playPhantomFootstep(world, player);
                }
                if (RANDOM.nextFloat() < 0.35f) {
                    playBinauralWhisper(world, player);
                }
                // 10% chance of fleeting distant Mimic appearance on horizon
                if (phase == NightmareEngine.Phase.PHASE_1_WHISPERS && RANDOM.nextFloat() < 0.10f) {
                    maybeSpawnDistantMimicGlimpse(world, player);
                }
            }

            // --- NIGHT 2 (PHASE 2): The Mimic & Chat ---
            if (phase.ordinal() >= NightmareEngine.Phase.PHASE_2_THE_MIMIC.ordinal()) {
                maybeSpawnMimic(world, player);
                // Interface glitch flicker
                if (RANDOM.nextFloat() < 0.30f) {
                    ModNetworking.sendGlitch(player, 25, 0.45f);
                }
            }

            // --- NIGHT 3 (PHASE 3): The Static Walker & Meta Shift ---
            if (phase.ordinal() >= NightmareEngine.Phase.PHASE_3_STATIC_WALKER.ordinal() && isNight) {
                maybeSpawnStaticWalker(world, player);
            }

            // --- NIGHT 4 (PHASE 4): Sleep Paralysis & Dual Active Hunt ---
            if (phase.ordinal() >= NightmareEngine.Phase.PHASE_4_SLEEP_PARALYSIS.ordinal() && isNight) {
                // Ensure both entities are actively hunting the player
                maybeSpawnMimic(world, player);
                maybeSpawnStaticWalker(world, player);
            }

            // --- NIGHT 5 (PHASE 5): Culmination, Blackout & BSOD ---
            if (phase == NightmareEngine.Phase.PHASE_5_CULMINATION && isNight && !engine.isFinaleTriggered()) {
                triggerFinale(world, player, engine);
            }
        }
    }

    private static void extinguishNearbyTorch(ServerWorld world, ServerPlayerEntity player) {
        BlockPos center = player.getBlockPos();
        for (BlockPos pos : BlockPos.iterate(center.add(-7, -2, -7), center.add(7, 3, 7))) {
            BlockState state = world.getBlockState(pos);
            if (state.isOf(Blocks.TORCH) || state.isOf(Blocks.WALL_TORCH)) {
                world.breakBlock(pos, true);
                world.playSound(
                        null, pos.getX(), pos.getY(), pos.getZ(),
                        SoundEvents.BLOCK_CANDLE_EXTINGUISH,
                        SoundCategory.BLOCKS,
                        1.2f, 0.85f
                );
                break;
            }
        }
    }

    private static void playPhantomFootstep(ServerWorld world, ServerPlayerEntity player) {
        Vec3d playerPos = new Vec3d(player.getX(), player.getY(), player.getZ());
        Vec3d behind = playerPos.subtract(player.getRotationVec(1.0f).multiply(2.2));
        world.playSound(
                null, behind.x, behind.y, behind.z,
                SoundEvents.BLOCK_STONE_STEP,
                SoundCategory.AMBIENT,
                0.95f, 0.75f
        );
    }

    private static void playBinauralWhisper(ServerWorld world, ServerPlayerEntity player) {
        Vec3d playerPos = new Vec3d(player.getX(), player.getY(), player.getZ());
        Vec3d side = player.getRotationVec(1.0f).crossProduct(new Vec3d(0, 1, 0)).normalize();
        boolean left = RANDOM.nextBoolean();
        Vec3d earPos = playerPos.add(side.multiply(left ? -1.2 : 1.2)).add(0, 1.6, 0);

        world.playSound(
                null, earPos.x, earPos.y, earPos.z,
                ModSounds.WHISPER,
                SoundCategory.PLAYERS,
                0.90f, 0.70f + RANDOM.nextFloat() * 0.4f
        );
    }

    private static void maybeSpawnDistantMimicGlimpse(ServerWorld world, ServerPlayerEntity player) {
        Box box = new Box(player.getBlockPos()).expand(64.0);
        List<MimicEntity> mimics = world.getEntitiesByClass(MimicEntity.class, box, e -> true);
        if (!mimics.isEmpty()) return;

        Vec3d look = player.getRotationVec(1.0f);
        Vec3d playerPos = new Vec3d(player.getX(), player.getY(), player.getZ());
        Vec3d spawnPos = playerPos.add(look.multiply(30.0)).add(RANDOM.nextFloat() * 8 - 4, 0, RANDOM.nextFloat() * 8 - 4);
        BlockPos bp = new BlockPos((int) spawnPos.x, (int) spawnPos.y, (int) spawnPos.z);
        BlockPos surface = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, bp);

        MimicEntity mimic = ModEntities.MIMIC.create(world, SpawnReason.EVENT);
        if (mimic != null) {
            mimic.refreshPositionAndAngles(surface.getX() + 0.5, (double) surface.getY(), surface.getZ() + 0.5, player.getYaw() + 180.0f, 0.0f);
            world.spawnEntity(mimic);
        }
    }

    private static void maybeSpawnMimic(ServerWorld world, ServerPlayerEntity player) {
        Box box = new Box(player.getBlockPos()).expand(64.0);
        List<MimicEntity> mimics = world.getEntitiesByClass(MimicEntity.class, box, e -> true);
        if (!mimics.isEmpty()) return;

        // Spawn ~18-22 blocks away
        Vec3d look = player.getRotationVec(1.0f);
        Vec3d playerPos = new Vec3d(player.getX(), player.getY(), player.getZ());
        Vec3d spawnPos = playerPos.add(look.multiply(19.0)).add(RANDOM.nextFloat() * 6 - 3, 0, RANDOM.nextFloat() * 6 - 3);
        BlockPos bp = new BlockPos((int) spawnPos.x, (int) spawnPos.y, (int) spawnPos.z);
        BlockPos surface = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, bp);

        MimicEntity mimic = ModEntities.MIMIC.create(world, SpawnReason.EVENT);
        if (mimic != null) {
            mimic.refreshPositionAndAngles(surface.getX() + 0.5, (double) surface.getY(), surface.getZ() + 0.5, player.getYaw() + 180.0f, 0.0f);
            world.spawnEntity(mimic);
        }
    }

    private static void maybeSpawnStaticWalker(ServerWorld world, ServerPlayerEntity player) {
        Box box = new Box(player.getBlockPos()).expand(50.0);
        List<StaticWalkerEntity> walkers = world.getEntitiesByClass(StaticWalkerEntity.class, box, e -> true);
        if (!walkers.isEmpty()) return;

        // Spawn in darkness behind player
        Vec3d look = player.getRotationVec(1.0f);
        Vec3d playerPos = new Vec3d(player.getX(), player.getY(), player.getZ());
        Vec3d spawnPos = playerPos.subtract(look.multiply(16.0));
        BlockPos bp = new BlockPos((int) spawnPos.x, (int) spawnPos.y, (int) spawnPos.z);
        BlockPos surface = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, bp);

        StaticWalkerEntity walker = ModEntities.STATIC_WALKER.create(world, SpawnReason.EVENT);
        if (walker != null) {
            walker.refreshPositionAndAngles(surface.getX() + 0.5, (double) surface.getY(), surface.getZ() + 0.5, player.getYaw(), 0.0f);
            world.spawnEntity(walker);

            // Trigger Night 3 Meta Shift: Wallpaper change & cryptic desktop lore note
            LoreManager.spawnPhase3Note();
            MetaHorrorManager.triggerHorrorWallpaper();
        }
    }

    private static void triggerFinale(ServerWorld world, ServerPlayerEntity player, NightmareEngine engine) {
        engine.setFinaleTriggered(true, world.getServer());

        // Darkness & Blindness blackout
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 300, 0, false, false));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 300, 0, false, false));

        // Roar of breach
        world.playSound(
                null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_WARDEN_EMERGE,
                SoundCategory.HOSTILE,
                2.5f, 0.5f
        );

        // Meta Desktop Note & Wallpaper Switch
        LoreManager.spawnPhase4Note(world.getServer().getName());
        MetaHorrorManager.triggerHorrorWallpaper();

        // Deliver BSOD Crash after 2.5 seconds
        world.getServer().execute(() -> {
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    ModNetworking.sendCrash(player, "bsod");
                }
            }, 2500);
        });
    }
}
