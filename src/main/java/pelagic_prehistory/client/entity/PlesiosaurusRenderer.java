package pelagic_prehistory.client.entity;

import pelagic_prehistory.entity.Plesiosaurus;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PlesiosaurusRenderer<T extends Plesiosaurus> extends GeoEntityRenderer<T> {

    public PlesiosaurusRenderer(EntityRendererProvider.Context context) {
        super(context, new PlesiosaurusModel<>("plesiosaurus"));
    }
}
