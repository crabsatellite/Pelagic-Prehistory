package pelagic_prehistory.client.entity;

import pelagic_prehistory.entity.Orthacanthus;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.Optional;

public class OrthacanthusModel<T extends Orthacanthus> extends SimplePitchGeoModel<T> {

    public OrthacanthusModel(final String name) {
        super(name);
    }

    @Override
    protected Optional<GeoBone> getHeadBone() {
        return Optional.empty();
    }
}
