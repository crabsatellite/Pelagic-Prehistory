package pelagic_prehistory.client.entity;

import net.minecraft.world.phys.Vec2;
import pelagic_prehistory.entity.Prognathodon;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.Optional;

public class PrognathodonModel<T extends Prognathodon> extends SimplePitchGeoModel<T> {

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
            final Vec2 rotations = getHeadRotations(animatable, instanceId, animationState).scale(0.5F);
            head.setRotX(head.getRotX() + rotations.x);
            head.setRotY(head.getRotY() + rotations.y);
            neck.setRotX(neck.getRotX() + rotations.x);
            neck.setRotY(neck.getRotY() + rotations.y);
        }
    }
}
