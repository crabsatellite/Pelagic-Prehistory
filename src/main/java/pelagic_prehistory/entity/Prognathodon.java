package pelagic_prehistory.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import pelagic_prehistory.PPRegistry;
import pelagic_prehistory.entity.goal.FloppingGoal;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;
import javax.annotation.Nullable;

public class Prognathodon extends WaterAnimal implements GeoEntity, NeutralMob, Enemy {

    // NEUTRAL MOB //
    private static final UniformInt ANGER_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    private int angerTime;
    private UUID angerTarget;

    // GECKOLIB //
    protected AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation ANIM_SWIM = RawAnimation.begin().thenLoop("swim");
    protected static final RawAnimation ANIM_SWIM_FAST = RawAnimation.begin().thenLoop("swim_fast");
    protected static final RawAnimation ANIM_DRY_OUT = RawAnimation.begin().thenLoop("dry_out");

    public Prognathodon(EntityType<? extends WaterAnimal> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 30, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 15);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.MOVEMENT_SPEED, 1.30D)
                .add(Attributes.ATTACK_DAMAGE, 9.0D);
    }

    //// METHODS ////

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(1, new FloppingGoal(this, 0.32F, 8));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(4, new RandomSwimmingGoal(this, 0.9D, 80));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Drowned.class, true, false));
        this.targetSelector.addGoal(3, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if(!level().isClientSide()) {
            this.updatePersistentAnger((ServerLevel) this.level(), true);
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level) {
            @Override
            public boolean isStableDestination(BlockPos pPos) {
                return level.isWaterAt(pPos.above()) && super.isStableDestination(pPos);
            }
        };
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(1.25F, 0.25F, 1.25F);
    }

    @Override
    public int getMaxHeadXRot() {
        return 20;
    }

    @Override
    public int getMaxHeadYRot() {
        return 20;
    }

    @Override
    protected void handleAirSupply(int pAirSupply) {
        super.handleAirSupply(pAirSupply);
        if (this.isInWaterOrBubble()) {
            this.setAirSupply(this.getMaxAirSupply());
        }
    }

    @Override
    public int getMaxAirSupply() {
        return 2400;
    }

    //// SOUNDS ////

    @Override
    public int getAmbientSoundInterval() {
        return 160;
    }

    @Override
    protected float getSoundVolume() {
        final float factor = isInWaterOrBubble() ? 0.5F : 0.32F;
        return super.getSoundVolume() * factor;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return PPRegistry.SoundReg.PROGNATHODON_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return PPRegistry.SoundReg.PROGNATHODON_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return PPRegistry.SoundReg.PROGNATHODON_DEATH.get();
    }

    //// NEUTRAL MOB ////

    @Override
    public boolean canAttack(LivingEntity pTarget) {
        return super.canAttack(pTarget) && pTarget.isInWaterOrBubble();
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(ANGER_RANGE.sample(this.random));
    }

    @Override
    public void setRemainingPersistentAngerTime(int time) {
        this.angerTime = time;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.angerTarget = target;
    }

    @Override
    public UUID getPersistentAngerTarget() {
        return this.angerTarget;
    }

    //// HOSTILE MOB ////

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    //// NBT ////

    @Override
    public void readAdditionalSaveData(final CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readPersistentAngerSaveData(this.level(), tag);
    }

    @Override
    public void addAdditionalSaveData(final CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addPersistentAngerSaveData(tag);
    }

    //// GECKOLIB ////

    private PlayState handleAnimation(AnimationState<Prognathodon> state) {
        if(!isInWaterOrBubble()) {
            state.getController().setAnimation(ANIM_DRY_OUT);
        } else if(state.isMoving()) {
            state.getController().setAnimation(ANIM_SWIM_FAST);
        } else {
            state.getController().setAnimation(ANIM_SWIM);
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
