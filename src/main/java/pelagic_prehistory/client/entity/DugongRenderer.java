package pelagic_prehistory.client.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import pelagic_prehistory.entity.Dugong;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DugongRenderer<T extends Dugong> extends GeoEntityRenderer<T> {

    public DugongRenderer(EntityRendererProvider.Context context) {
        super(context, new SimplePitchGeoModel<T>("dugong"));
    }
}
