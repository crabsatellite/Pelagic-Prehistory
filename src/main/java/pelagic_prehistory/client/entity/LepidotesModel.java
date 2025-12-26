package pelagic_prehistory.client.entity;

import net.minecraft.util.Mth;
import pelagic_prehistory.entity.Lepidotes;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.Optional;

public class LepidotesModel<T extends Lepidotes> extends SimplePitchGeoModel<T> {

    public LepidotesModel(final String name) {
        super(name);
    }

    @Override
    protected Optional<GeoBone> getHeadBone() {
        return getBone("Head");
    }

    @Override
    protected Optional<GeoBone> getBodyBone() {
        return getBone("Lepidotes");
    }
}
