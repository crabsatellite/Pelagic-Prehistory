package pelagic_prehistory.client.entity;

import net.minecraft.util.Mth;
import pelagic_prehistory.entity.Cuttlefish;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.animation.AnimationState;

import java.util.Optional;

public class CuttlefishModel<T extends Cuttlefish> extends SimplePitchGeoModel<T> {

    public CuttlefishModel(final String name) {
        super(name);
    }

    @Override
    protected Optional<GeoBone> getBodyBone() {
        return this.getBone("root");
    }

    @Override
    protected Optional<GeoBone> getHeadBone() {
        return Optional.empty();
    }

    @Override
    protected float getPitchMultiplier() {
        return -1;
    }

    @Override
    protected void rotateBody(T animatable, long instanceId, AnimationState<T> animationState) {
        super.rotateBody(animatable, instanceId, animationState);
        Optional<GeoBone> bone = getBodyBone();
        bone.ifPresent(geoBone -> geoBone.setRotY(Mth.PI));
    }
}
