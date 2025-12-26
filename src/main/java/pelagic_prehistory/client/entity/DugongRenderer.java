package pelagic_prehistory.client.entity;

import pelagic_prehistory.entity.Dugong;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DugongRenderer<T extends Dugong> extends GeoEntityRenderer<T> {

    public DugongRenderer(EntityRendererProvider.Context context) {
        super(context, new SimplePitchGeoModel<T>("dugong"));
    }
}
