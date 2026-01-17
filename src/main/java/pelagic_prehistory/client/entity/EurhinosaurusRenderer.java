package pelagic_prehistory.client.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import pelagic_prehistory.entity.Eurhinosaurus;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EurhinosaurusRenderer<T extends Eurhinosaurus> extends GeoEntityRenderer<T> {

    public EurhinosaurusRenderer(EntityRendererProvider.Context context) {
        super(context, new EurhinosaurusModel<>("eurhinosaurus"));
    }
}
