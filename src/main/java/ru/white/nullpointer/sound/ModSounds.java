package ru.white.nullpointer.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final Identifier HEARTBEAT_ID = Identifier.of("nullpointer", "ambient.heartbeat");
    public static final SoundEvent AMBIENT_HEARTBEAT = SoundEvent.of(HEARTBEAT_ID);

    public static final Identifier WALKER_DRONE_ID = Identifier.of("nullpointer", "entity.walker.drone");
    public static final SoundEvent WALKER_DRONE = SoundEvent.of(WALKER_DRONE_ID);

    public static final Identifier JUMPSCARE_SCREECH_ID = Identifier.of("nullpointer", "jumpscare.screech");
    public static final SoundEvent JUMPSCARE_SCREECH = SoundEvent.of(JUMPSCARE_SCREECH_ID);

    public static final Identifier WHISPER_ID = Identifier.of("nullpointer", "whisper");
    public static final SoundEvent WHISPER = SoundEvent.of(WHISPER_ID);

    public static void registerAll() {
        Registry.register(Registries.SOUND_EVENT, HEARTBEAT_ID, AMBIENT_HEARTBEAT);
        Registry.register(Registries.SOUND_EVENT, WALKER_DRONE_ID, WALKER_DRONE);
        Registry.register(Registries.SOUND_EVENT, JUMPSCARE_SCREECH_ID, JUMPSCARE_SCREECH);
        Registry.register(Registries.SOUND_EVENT, WHISPER_ID, WHISPER);
        System.out.println("[NullPointer] Custom SoundEvents registered.");
    }
}
