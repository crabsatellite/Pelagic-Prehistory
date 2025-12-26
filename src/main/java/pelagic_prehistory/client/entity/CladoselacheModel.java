package pelagic_prehistory.client.entity;

import pelagic_prehistory.entity.Cladoselache;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.Optional;

public class CladoselacheModel<T extends Cladoselache> extends SimplePitchGeoModel<T> {

    public CladoselacheModel(final String name) {
        super(name);
    }

    @Override
    protected Optional<GeoBone> getHeadBone() {
        return Optional.empty();
    }

    @Override
    protected Optional<GeoBone> getBodyBone() {
        return getBone("bone");
    }
}
