package ru.white.nullpointer.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;
import ru.white.nullpointer.client.render.model.MimicModel;
import ru.white.nullpointer.client.render.model.ModModelLayers;
import ru.white.nullpointer.entity.MimicEntity;

@Environment(EnvType.CLIENT)
public class MimicRenderer extends MobEntityRenderer<MimicEntity, LivingEntityRenderState, MimicModel> {
    private static final Identifier TEXTURE = Identifier.of("nullpointer", "textures/entity/mimic.png");

    public MimicRenderer(EntityRendererFactory.Context context) {
        super(context, new MimicModel(context.getPart(ModModelLayers.MIMIC)), 0.35f);
    }

    @Override
    public LivingEntityRenderState createRenderState() {
        return new LivingEntityRenderState();
    }

    @Override
    public void updateRenderState(MimicEntity entity, LivingEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
    }

    @Override
    public Identifier getTexture(LivingEntityRenderState state) {
        return TEXTURE;
    }
}
