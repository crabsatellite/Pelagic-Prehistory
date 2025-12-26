package pelagic_prehistory.client.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import pelagic_prehistory.entity.Prognathodon;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PrognathodonRenderer<T extends Prognathodon> extends GeoEntityRenderer<T> {

    public PrognathodonRenderer(EntityRendererProvider.Context context) {
        super(context, new PrognathodonModel<>("prognathodon"));
    }
}
