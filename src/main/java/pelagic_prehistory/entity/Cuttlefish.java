package pelagic_prehistory.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Cuttlefish extends WaterAnimal implements GeoEntity {

    // GECKOLIB //
    protected AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation ANIM_SWIM = RawAnimation.begin().thenLoop("swim");

    public Cuttlefish(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 20, 5, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 15);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.24D)
                .add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D);
    }

    //// METHODS ////

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.9D, 80));
        this.goalSelector.addGoal(9, new AvoidEntityGoal<>(this, Player.class, 10.0F, 1.0D, 1.0D));
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    public LookControl getLookControl() {
        return super.getLookControl();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence();
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        super.travel(pTravelVector);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level) {
            @Override
            public boolean isStableDestination(BlockPos pPos) {
                return /*!level.getBlockState(pPos).isAir()
                        && !level.getBlockState(pPos.above()).isAir()
                        && */super.isStableDestination(pPos)
                        && !level.getBlockState(pPos.above()).isAir()/*
                        && super.isStableDestination(pPos.below())*/;
            }
        };
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.85F;
    }

    @Override
    public int getHeadRotSpeed() {
        return super.getHeadRotSpeed();
    }

    @Override
    public int getMaxHeadYRot() {
        return super.getMaxHeadYRot();
    }

    @Override
    public int getMaxHeadXRot() {
        return super.getMaxHeadXRot();
    }

    //// SOUNDS ////

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SQUID_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.SQUID_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.SQUID_DEATH;
    }

    protected SoundEvent getSquirtSound() {
        return SoundEvents.SQUID_SQUIRT;
    }

    //// INK ////

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (super.hurt(pSource, pAmount)) {
            if (this.getLastHurtByMob() != null) {
                this.spawnInk(getLastHurtByMob());
            }
            return true;
        }
        return false;
    }

    private void spawnInk(LivingEntity lastHurtByMob) {
        if(this.level().isClientSide()) {
            return;
        }
        // play ink sound
        this.playSound(this.getSquirtSound(), this.getSoundVolume(), this.getVoicePitch());
        // determine position and direction of ink
        final Vec3 position = this.getEyePosition();
        final Vec3 direction = this.position().vectorTo(lastHurtByMob.position()).normalize();
        final double variance = 0.25D;
        final double speed = 0.15D;
        // send ink particles to clients
        for(int i = 0, n = 30; i < n; i++) {
            Vec3 pos = position.add((this.getRandom().nextDouble() - 0.5D) * 2.0D * variance, (this.getRandom().nextDouble() - 0.5D) * 2.0D * variance, (this.getRandom().nextDouble() - 0.5D) * 2.0D * variance);
            Vec3 motion = direction;//direction.add((this.getRandom().nextDouble() - 0.5D) * 2.0D * variance, (this.getRandom().nextDouble() - 0.5D) * 2.0D * variance, (this.getRandom().nextDouble() - 0.5D) * 2.0D * variance);
            ((ServerLevel)this.level()).sendParticles(this.getInkParticle(), pos.x(), pos.y(), pos.z(), 0, motion.x(), motion.y(), motion.z(), speed);
        }
        // add blindness effect
        if(this.position().closerThan(lastHurtByMob.getEyePosition(), 2.5D)) {
            lastHurtByMob.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false));
        }
    }

    protected ParticleOptions getInkParticle() {
        return ParticleTypes.SQUID_INK;
    }

    
    //// NBT ////

    @Override
    public void readAdditionalSaveData(final CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    @Override
    public void addAdditionalSaveData(final CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }

    //// GECKOLIB ////

    private PlayState handleAnimation(AnimationState<Cuttlefish> state) {
        if(getDeltaMovement().lengthSqr() > 2.5000003E-7F) {
            state.getController().setAnimation(ANIM_SWIM);
        } else {
            state.getController().setAnimation(ANIM_IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 2, this::handleAnimation));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
