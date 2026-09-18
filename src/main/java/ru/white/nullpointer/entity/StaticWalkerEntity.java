package ru.white.nullpointer.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import ru.white.nullpointer.network.ModNetworking;

public class StaticWalkerEntity extends EndermanEntity {
    private boolean isObserved = false;
    private int unobservedTicks = 0;
    private int soundCooldown = 0;

    public StaticWalkerEntity(EntityType<? extends EndermanEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 100.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.0)
                .add(EntityAttributes.ATTACK_DAMAGE, 20.0);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getEntityWorld().isClient()) {
            return;
        }

        PlayerEntity player = this.getEntityWorld().getClosestPlayer(this, 48.0);
        if (player == null || player.isCreative() || player.isSpectator()) {
            return;
        }

        Vec3d playerEyes = player.getEyePos();
        Vec3d myEyes = this.getEyePos();
        Vec3d lookVec = player.getRotationVec(1.0f);
        Vec3d toEntity = myEyes.subtract(playerEyes).normalize();

        // Dot product: 1.0 = looking directly at entity, <0 = entity is behind player
        double dot = lookVec.dotProduct(toEntity);

        if (dot > 0.55) {
            // Check line of sight with raycast
            RaycastContext context = new RaycastContext(
                    playerEyes, myEyes,
                    RaycastContext.ShapeType.COLLIDER,
                    RaycastContext.FluidHandling.NONE,
                    player
            );
            BlockHitResult hit = this.getEntityWorld().raycast(context);
            isObserved = (hit.getType() == HitResult.Type.MISS);
        } else {
            isObserved = false;
        }

        if (isObserved) {
            // Freeze in place!
            this.setVelocity(Vec3d.ZERO);
            this.getNavigation().stop();
            unobservedTicks = 0;

            // Proportional proximity distortion
            double dist = Math.sqrt(this.squaredDistanceTo(player));
            float proximityFactor = (float) Math.clamp(0.3 + (32.0 - dist) / 14.0, 0.3, 2.5);

            // Subtle hum & glitch pulse scaling with proximity
            if (--soundCooldown <= 0) {
                soundCooldown = Math.max(8, (int) (dist * 0.9));
                float droneVol = (float) Math.clamp(0.6 + proximityFactor * 0.5, 0.6, 2.2);
                float dronePitch = (float) Math.clamp(0.8 - proximityFactor * 0.15, 0.35, 0.8);
                this.getEntityWorld().playSound(
                        null, this.getX(), this.getY(), this.getZ(),
                        ru.white.nullpointer.sound.ModSounds.WALKER_DRONE,
                        SoundCategory.HOSTILE,
                        droneVol, dronePitch
                );
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    ModNetworking.sendGlitch(serverPlayer, 16, proximityFactor);
                }
            }
        } else {
            // Player looked away! Stalker micro-teleports closer
            unobservedTicks++;
            if (unobservedTicks >= 7) {
                unobservedTicks = 0;
                performMicroTeleport(player, lookVec);
            }
        }

        // Distance check for jumpscare & fake crash
        double distance = this.squaredDistanceTo(player);
        if (distance < 3.2) { // ~1.7 blocks
            if (player instanceof ServerPlayerEntity serverPlayer) {
                ModNetworking.sendCrash(serverPlayer, "mc");
            }
            this.discard();
        }
    }

    private void performMicroTeleport(PlayerEntity player, Vec3d playerLook) {
        Vec3d myPos = new Vec3d(this.getX(), this.getY(), this.getZ());
        Vec3d playerPos = new Vec3d(player.getX(), player.getY(), player.getZ());
        // Target is directly behind player's blind spot
        Vec3d targetBehind = playerPos.subtract(playerLook.multiply(1.8));
        Vec3d stepDir = targetBehind.subtract(myPos);

        double dist = stepDir.length();
        if (dist > 1.5) {
            double stepSize = Math.min(3.2, dist * 0.4);
            Vec3d newPos = myPos.add(stepDir.normalize().multiply(stepSize));

            // Find valid floor
            int targetY = (int) newPos.y;
            while (targetY > this.getEntityWorld().getBottomY() && this.getEntityWorld().getBlockState(new BlockPos((int) newPos.x, targetY, (int) newPos.z)).isAir()) {
                targetY--;
            }
            targetY += 1;

            // Spawn shattered void particles at origin
            if (this.getEntityWorld() instanceof net.minecraft.server.world.ServerWorld sw) {
                sw.spawnParticles(net.minecraft.particle.ParticleTypes.PORTAL, myPos.x, myPos.y + 1.5, myPos.z, 22, 0.3, 0.8, 0.3, 0.1);
                sw.spawnParticles(net.minecraft.particle.ParticleTypes.SQUID_INK, myPos.x, myPos.y + 1.0, myPos.z, 14, 0.4, 0.6, 0.4, 0.05);
                sw.spawnParticles(net.minecraft.particle.ParticleTypes.SMOKE, newPos.x, (double) targetY + 1.5, newPos.z, 15, 0.2, 0.5, 0.2, 0.05);
            }

            this.teleport(newPos.x, targetY, newPos.z, true);
            this.lookAt(net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor.EYES, player.getEyePos());
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        // Silent movement
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WARDEN_HEARTBEAT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_WITHER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_WARDEN_DEATH;
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }
}
