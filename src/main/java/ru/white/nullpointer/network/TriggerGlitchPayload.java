package ru.white.nullpointer.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record TriggerGlitchPayload(int ticks, float intensity) implements CustomPayload {
    public static final CustomPayload.Id<TriggerGlitchPayload> ID = new CustomPayload.Id<>(Identifier.of("nullpointer", "trigger_glitch"));
    public static final PacketCodec<RegistryByteBuf, TriggerGlitchPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, TriggerGlitchPayload::ticks,
            PacketCodecs.FLOAT, TriggerGlitchPayload::intensity,
            TriggerGlitchPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
