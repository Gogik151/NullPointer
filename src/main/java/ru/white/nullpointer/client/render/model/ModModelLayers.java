package ru.white.nullpointer.client.render.model;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer VOID_WALKER = new EntityModelLayer(
            Identifier.of("nullpointer", "void_walker"), "main"
    );

    public static final EntityModelLayer MIMIC = new EntityModelLayer(
            Identifier.of("nullpointer", "mimic"), "main"
    );
}
