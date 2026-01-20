package pelagic_prehistory.client.entity;

import net.minecraft.world.phys.Vec2;
import pelagic_prehistory.entity.Plesiosaurus;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;

import java.util.Optional;

public class PlesiosaurusModel<T extends Plesiosaurus> extends SimplePitchGeoModel<T> {

    public PlesiosaurusModel(final String name) {
        super(name);
    }

    @Override
    protected float getPitchMultiplier() {
        return -1.0F;
    }

    @Override
    protected void rotateHead(T animatable, long instanceId, AnimationState<T> animationState) {
        Optional<GeoBone> oHead = getHeadBone();
        Optional<GeoBone> oNeck = getBone("neck");
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
