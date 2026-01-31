package pelagic_prehistory.client.entity;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import pelagic_prehistory.entity.Prognathodon;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.Optional;

public class PrognathodonModel<T extends Prognathodon> extends SimplePitchGeoModel<T> {

    private static final float MAX_HEAD_ROT = 20.0F * Mth.DEG_TO_RAD;

    public PrognathodonModel(final String name) {
        super(name);
    }

    @Override
    protected Optional<GeoBone> getHeadBone() {
        return getBone("bone5");
    }

    @Override
    protected Optional<GeoBone> getBodyBone() {
        return getBone("bone");
    }

    protected Optional<GeoBone> getNeckBone() {
        return getBone("bone4");
    }

    @Override
    protected void rotateHead(T animatable, long instanceId, AnimationState<T> animationState) {
        Optional<GeoBone> oHead = getHeadBone();
        Optional<GeoBone> oNeck = getNeckBone();
        if(oHead.isPresent() && oNeck.isPresent()) {
            final GeoBone head = oHead.get();
            final GeoBone neck = oNeck.get();
            Vec2 rotations = getHeadRotations(animatable, instanceId, animationState).scale(0.5F);
            // Clamp rotations to prevent 360 degree spinning on land
            float clampedX = Mth.clamp(rotations.x, -MAX_HEAD_ROT, MAX_HEAD_ROT);
            float clampedY = Mth.clamp(rotations.y, -MAX_HEAD_ROT, MAX_HEAD_ROT);
            head.setRotX(head.getRotX() + clampedX);
            head.setRotY(head.getRotY() + clampedY);
            neck.setRotX(neck.getRotX() + clampedX);
            neck.setRotY(neck.getRotY() + clampedY);
        }
    }
}
