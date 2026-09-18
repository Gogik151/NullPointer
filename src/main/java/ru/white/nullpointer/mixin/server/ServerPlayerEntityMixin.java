package ru.white.nullpointer.mixin.server;

import com.mojang.datafixers.util.Either;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.white.nullpointer.entity.ModEntities;
import ru.white.nullpointer.entity.StaticWalkerEntity;
import ru.white.nullpointer.network.ModNetworking;
import ru.white.nullpointer.story.NightmareEngine;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "trySleep", at = @At("HEAD"), cancellable = true)
    private void onTrySleep(BlockPos pos, CallbackInfoReturnable<Either<PlayerEntity.SleepFailureReason, Unit>> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ServerWorld world = (ServerWorld) player.getEntityWorld();
        NightmareEngine engine = NightmareEngine.getOrCreate(world.getServer());

        // Intercept sleep in Night 4+ (Sleep Paralysis & Dual Hunt)
        if (engine.getPhase().ordinal() >= NightmareEngine.Phase.PHASE_4_SLEEP_PARALYSIS.ordinal()) {
            // Apply darkness and paralysis
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 220, 0, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 220, 4, false, false));

            // Eerie sound
            world.playSound(
                    null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENTITY_WARDEN_HEARTBEAT,
                    SoundCategory.PLAYERS,
                    1.8f, 0.5f
            );

            // Spawn The Static Walker in doorway/room looking at bed
            Vec3d spawnPos = Vec3d.ofBottomCenter(pos.add(3, 0, 3));
            StaticWalkerEntity walker = ModEntities.STATIC_WALKER.create(world, SpawnReason.EVENT);
            if (walker != null) {
                walker.refreshPositionAndAngles(spawnPos.x, spawnPos.y, spawnPos.z, player.getYaw() + 180.0f, 0.0f);
                world.spawnEntity(walker);
            }

            // Glitch and paralysis overlay
            ModNetworking.sendGlitch(player, 160, 1.3f);
            ModNetworking.sendParalysis(player, 160);
            player.sendMessage(Text.literal("§4You feel paralyzed. It is watching you from the corner."), false);


            // Block sleeping cleanly
            cir.setReturnValue(Either.left(null));
        }
    }
}

