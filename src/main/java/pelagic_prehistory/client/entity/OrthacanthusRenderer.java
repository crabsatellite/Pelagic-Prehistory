package pelagic_prehistory.client.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import pelagic_prehistory.entity.Orthacanthus;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OrthacanthusRenderer<T extends Orthacanthus> extends GeoEntityRenderer<T> {

    public OrthacanthusRenderer(EntityRendererProvider.Context context) {
        super(context, new OrthacanthusModel<>("orthacanthus"));
    }
}
