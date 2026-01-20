package pelagic_prehistory.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec2;
import pelagic_prehistory.PelagicPrehistory;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Optional;

public class SimplePitchGeoModel<T extends LivingEntity & GeoEntity> extends GeoModel<T> {

    private final ResourceLocation modelLocation;
    private final ResourceLocation textureLocation;
    private final ResourceLocation animationLocation;

    public SimplePitchGeoModel(final String name) {
        super();
        this.textureLocation = ResourceLocation.fromNamespaceAndPath(PelagicPrehistory.MODID, "textures/entity/" + name + ".png");
        this.modelLocation = ResourceLocation.fromNamespaceAndPath(PelagicPrehistory.MODID, "geo/entity/" + name + ".geo.json");
        this.animationLocation = ResourceLocation.fromNamespaceAndPath(PelagicPrehistory.MODID, "animations/entity/" + name + ".animation.json");
    }

    @Override
    public ResourceLocation getModelResource(T object) {
        return modelLocation;
    }

    @Override
    public ResourceLocation getTextureResource(T object) {
        return textureLocation;
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return animationLocation;
    }

    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        rotateBody(animatable, instanceId, animationState);
        rotateHead(animatable, instanceId, animationState);
    }

    protected Optional<GeoBone> getBodyBone() {
        return this.getBone("body");
    }

    protected Optional<GeoBone> getHeadBone() {
        return this.getBone("head");
    }

    protected float getPitchMultiplier() {
        return 1;
    }

    protected void rotateBody(T animatable, long instanceId, AnimationState<T> animationState) {
        if(animatable.onGround()) {
            return;
        }
        Optional<GeoBone> bone = getBodyBone();
        if(bone.isPresent()) {
            float xRot = (-1.0F) * animatable.getViewXRot(animationState.getPartialTick()) * getPitchMultiplier();
            float angle = (float) Math.toRadians(xRot);
            bone.get().setRotX(angle);
        }
    }

    protected void rotateHead(T animatable, long instanceId, AnimationState<T> animationState) {
        Optional<GeoBone> oBone = getHeadBone();
        if(oBone.isPresent()) {
            final GeoBone bone = oBone.get();
            final Vec2 rotations = getHeadRotations(animatable, instanceId, animationState);
            bone.setRotX(rotations.x * getPitchMultiplier());
            bone.setRotY(rotations.y);
        }
    }

    protected Vec2 getHeadRotations(T animatable, long instanceId, AnimationState<T> animationState) {
        EntityModelData extraData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        int unpausedMultiplier = !Minecraft.getInstance().isPaused() ? 1 : 0;
        return new Vec2(extraData.headPitch(), extraData.netHeadYaw()).scale(Mth.DEG_TO_RAD * unpausedMultiplier);
    }
}
