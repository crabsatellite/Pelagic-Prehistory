package pelagic_prehistory.client.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import pelagic_prehistory.entity.Cladoselache;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CladoselacheRenderer<T extends Cladoselache> extends GeoEntityRenderer<T> {

    public CladoselacheRenderer(EntityRendererProvider.Context context) {
        super(context, new CladoselacheModel<T>("cladoselache"));
    }
}
