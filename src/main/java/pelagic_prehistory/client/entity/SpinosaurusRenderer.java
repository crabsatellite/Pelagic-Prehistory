package pelagic_prehistory.client.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import pelagic_prehistory.entity.Spinosaurus;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SpinosaurusRenderer<T extends Spinosaurus> extends GeoEntityRenderer<T> {

    public SpinosaurusRenderer(EntityRendererProvider.Context context) {
        super(context, new SpinosaurusModel<>("spinosaurus"));
    }
}
