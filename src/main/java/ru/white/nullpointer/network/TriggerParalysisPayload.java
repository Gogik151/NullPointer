package ru.white.nullpointer.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record TriggerParalysisPayload(int ticks) implements CustomPayload {
    public static final CustomPayload.Id<TriggerParalysisPayload> ID = new CustomPayload.Id<>(Identifier.of("nullpointer", "trigger_paralysis"));
    public static final PacketCodec<RegistryByteBuf, TriggerParalysisPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, TriggerParalysisPayload::ticks,
            TriggerParalysisPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
