package pelagic_prehistory.client.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import pelagic_prehistory.entity.Bawitius;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BawitiusRenderer<T extends Bawitius> extends GeoEntityRenderer<T> {

    public BawitiusRenderer(EntityRendererProvider.Context context) {
        super(context, new SimplePitchGeoModel<T>("bawitius"));
    }
}
