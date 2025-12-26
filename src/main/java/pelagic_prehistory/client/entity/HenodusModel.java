package pelagic_prehistory.client.entity;

import pelagic_prehistory.entity.Henodus;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.Optional;

public class HenodusModel<T extends Henodus> extends SimplePitchGeoModel<T> {

    public HenodusModel(final String name) {
        super(name);
    }

    @Override
    protected float getPitchMultiplier() {
        return -1.0F;
    }

    @Override
    protected Optional<GeoBone> getHeadBone() {
        return this.getBone("Head");
    }

    @Override
    protected Optional<GeoBone> getBodyBone() {
        return this.getBone("Henodus");
    }
}
