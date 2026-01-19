package pelagic_prehistory.client.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import pelagic_prehistory.entity.Shonisaurus;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ShonisaurusRenderer<T extends Shonisaurus> extends GeoEntityRenderer<T> {

    public ShonisaurusRenderer(EntityRendererProvider.Context context) {
        super(context, new ShonisaurusModel<>("shonisaurus"));
        this.withScale(2.0F);
    }
}
