package pelagic_prehistory.client.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import pelagic_prehistory.entity.Henodus;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HenodusRenderer<T extends Henodus> extends GeoEntityRenderer<T> {

    public HenodusRenderer(EntityRendererProvider.Context context) {
        super(context, new HenodusModel<>("henodus"));
    }
}
