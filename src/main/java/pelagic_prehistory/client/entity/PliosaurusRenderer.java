package pelagic_prehistory.client.entity;

import pelagic_prehistory.entity.Pliosaurus;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PliosaurusRenderer<T extends Pliosaurus> extends GeoEntityRenderer<T> {

    public PliosaurusRenderer(EntityRendererProvider.Context context) {
        super(context, new SimplePitchGeoModel<T>("pliosaurus"));
    }
}
