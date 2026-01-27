package pelagic_prehistory.client.entity;

import pelagic_prehistory.entity.Eurhinosaurus;

public class EurhinosaurusModel<T extends Eurhinosaurus> extends SimplePitchGeoModel<T> {

    public EurhinosaurusModel(final String name) {
        super(name);
    }

    @Override
    protected float getPitchMultiplier() {
        return -1.0F;
    }
}
