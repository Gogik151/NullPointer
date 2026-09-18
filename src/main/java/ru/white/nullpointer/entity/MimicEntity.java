package ru.white.nullpointer.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class MimicEntity extends ZombieEntity {
    private final Random random = new Random();
    private int chatTimer = 200;

    private static final String[] CREEPY_LINES = {
            "Why are you looking around?",
            "I'm standing right behind you.",
            "Did you hear the footsteps outside your door?",
            "Don't turn off the lights.",
            "It knows your system username.",
            "You can't wake up from this."
    };

    public MimicEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.MAX_HEALTH, 60.0)
                .add(EntityAttributes.MOVEMENT_SPEED, 0.28)
                .add(EntityAttributes.ATTACK_DAMAGE, 10.0);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getEntityWorld().isClient()) {
            return;
        }

        PlayerEntity player = this.getEntityWorld().getClosestPlayer(this, 40.0);
        if (player == null || player.isCreative() || player.isSpectator()) {
            return;
        }

        // Always stare at player
        this.lookAt(net.minecraft.command.argument.EntityAnchorArgumentType.EntityAnchor.EYES, player.getEyePos());

        double distSq = this.squaredDistanceTo(player);

        // Maintain distance: if player comes too close (< 6 blocks), vanish in smoke!
        if (distSq < 36.0) {
            if (this.getEntityWorld() instanceof ServerWorld sw) {
                sw.spawnParticles(ParticleTypes.SQUID_INK, this.getX(), this.getY() + 1.0, this.getZ(), 20, 0.4, 0.8, 0.4, 0.05);
            }
            this.discard();
            return;
        }

        // Chat mimicry
        if (--chatTimer <= 0) {
            chatTimer = 400 + random.nextInt(400); // 20 - 40 seconds
            if (player instanceof ServerPlayerEntity serverPlayer) {
                String line = CREEPY_LINES[random.nextInt(CREEPY_LINES.length)];
                String fakeMessage = "<" + serverPlayer.getName().getString() + "> " + line;
                serverPlayer.sendMessage(Text.literal(fakeMessage), false);
            }
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        // Completely silent footsteps!
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null; // Silent stalker
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public boolean burnsInDaylight() {
        return false; // Doesn't burn in sun
    }
}
