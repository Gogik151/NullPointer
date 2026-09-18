package ru.white.nullpointer.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final RegistryKey<EntityType<?>> STATIC_WALKER_KEY = RegistryKey.of(
            RegistryKeys.ENTITY_TYPE, Identifier.of("nullpointer", "static_walker")
    );

    public static final RegistryKey<EntityType<?>> MIMIC_KEY = RegistryKey.of(
            RegistryKeys.ENTITY_TYPE, Identifier.of("nullpointer", "mimic")
    );

    public static final EntityType<StaticWalkerEntity> STATIC_WALKER = Registry.register(
            Registries.ENTITY_TYPE,
            STATIC_WALKER_KEY,
            EntityType.Builder.create(StaticWalkerEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6f, 2.9f)
                    .build(STATIC_WALKER_KEY)
    );

    public static final EntityType<MimicEntity> MIMIC = Registry.register(
            Registries.ENTITY_TYPE,
            MIMIC_KEY,
            EntityType.Builder.create(MimicEntity::new, SpawnGroup.MONSTER)
                    .dimensions(0.6f, 1.95f)
                    .build(MIMIC_KEY)
    );

    public static void registerAll() {
        FabricDefaultAttributeRegistry.register(STATIC_WALKER, StaticWalkerEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(MIMIC, MimicEntity.createAttributes());
        System.out.println("[NullPointer] Entities registered successfully.");
    }
}
