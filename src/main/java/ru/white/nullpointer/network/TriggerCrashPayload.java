package ru.white.nullpointer.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record TriggerCrashPayload(String style) implements CustomPayload {
    public static final CustomPayload.Id<TriggerCrashPayload> ID = new CustomPayload.Id<>(Identifier.of("nullpointer", "trigger_crash"));
    public static final PacketCodec<RegistryByteBuf, TriggerCrashPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, TriggerCrashPayload::style,
            TriggerCrashPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
