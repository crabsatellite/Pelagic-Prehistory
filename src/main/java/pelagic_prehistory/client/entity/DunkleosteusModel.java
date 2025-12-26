package pelagic_prehistory.client.entity;

import pelagic_prehistory.entity.Dunkleosteus;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.Optional;

public class DunkleosteusModel<T extends Dunkleosteus> extends SimplePitchGeoModel<T> {

    public DunkleosteusModel(final String name) {
        super(name);
    }

    @Override
    protected Optional<GeoBone> getHeadBone() {
        return this.getBone("bone2");
    }

    @Override
    protected Optional<GeoBone> getBodyBone() {
        return this.getBone("bone");
    }
}
