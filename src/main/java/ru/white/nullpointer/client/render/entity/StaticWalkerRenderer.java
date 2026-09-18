package ru.white.nullpointer.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;
import ru.white.nullpointer.client.render.model.ModModelLayers;
import ru.white.nullpointer.client.render.model.VoidWalkerModel;
import ru.white.nullpointer.entity.StaticWalkerEntity;

@Environment(EnvType.CLIENT)
public class StaticWalkerRenderer extends MobEntityRenderer<StaticWalkerEntity, LivingEntityRenderState, VoidWalkerModel> {
    private static final Identifier TEXTURE = Identifier.of("nullpointer", "textures/entity/void_walker.png");

    public StaticWalkerRenderer(EntityRendererFactory.Context context) {
        super(context, new VoidWalkerModel(context.getPart(ModModelLayers.VOID_WALKER)), 0.0f);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public void updateRenderState(StaticWalkerEntity entity, LivingEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
