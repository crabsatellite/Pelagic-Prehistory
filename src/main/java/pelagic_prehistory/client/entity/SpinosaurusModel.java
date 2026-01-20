package pelagic_prehistory.client.entity;

import pelagic_prehistory.entity.Spinosaurus;
import software.bernie.geckolib.animation.AnimationState;

public class SpinosaurusModel<T extends Spinosaurus> extends SimplePitchGeoModel<T> {

    public SpinosaurusModel(final String name) {
        super(name);
    }

    @Override
    protected float getPitchMultiplier() {
        return 1.0F;
    }

    @Override
    protected void rotateBody(T animatable, long instanceId, AnimationState<T> animationState) {
        // Spinosaurus doesn't rotate body based on pitch (it's amphibious)
    }
}
