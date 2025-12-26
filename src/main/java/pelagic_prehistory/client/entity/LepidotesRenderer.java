package pelagic_prehistory.client.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import pelagic_prehistory.entity.Lepidotes;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LepidotesRenderer<T extends Lepidotes> extends GeoEntityRenderer<T> {

    public LepidotesRenderer(EntityRendererProvider.Context context) {
        super(context, new LepidotesModel<>("lepidotes"));
    }
}
