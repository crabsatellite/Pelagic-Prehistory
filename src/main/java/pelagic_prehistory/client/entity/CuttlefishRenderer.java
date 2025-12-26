package pelagic_prehistory.client.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import pelagic_prehistory.entity.Cuttlefish;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CuttlefishRenderer<T extends Cuttlefish> extends GeoEntityRenderer<T> {

    public CuttlefishRenderer(EntityRendererProvider.Context context) {
        super(context, new CuttlefishModel<>("cuttlefish"));
    }


}
