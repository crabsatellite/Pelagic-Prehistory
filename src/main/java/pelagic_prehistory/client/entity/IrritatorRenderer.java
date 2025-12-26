package pelagic_prehistory.client.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import pelagic_prehistory.entity.Irritator;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class IrritatorRenderer<T extends Irritator> extends GeoEntityRenderer<T> {

    private static final Vec3 SWIMMING_RENDER_OFFSET = new Vec3(0, -0.68F, 0);

    public IrritatorRenderer(EntityRendererProvider.Context context) {
        super(context, new IrritatorModel<>("irritator"));
    }

    @Override
    public Vec3 getRenderOffset(T pEntity, float pPartialTicks) {
        if(pEntity.isBodyInWater()) {
            return SWIMMING_RENDER_OFFSET;
        }
        return super.getRenderOffset(pEntity, pPartialTicks);
    }
}
