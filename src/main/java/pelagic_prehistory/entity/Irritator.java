package pelagic_prehistory.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.BreathAirGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import pelagic_prehistory.PPRegistry;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.UUID;
import javax.annotation.Nullable;

public class Irritator extends PathfinderMob implements GeoEntity, NeutralMob, Enemy {

    // NEUTRAL MOB //
    private static final UniformInt ANGER_RANGE = TimeUtil.rangeOfSeconds(20, 39);
    private int angerTime;
    private UUID angerTarget;

    // GECKOLIB //
    protected AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    protected static final RawAnimation ANIM_IDLE = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation ANIM_WALK = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ANIM_SWIM = RawAnimation.begin().thenLoop("swim");

    // OTHER //
    private boolean isBodyInWater;
    private final EntityDimensions swimmingSize;

    public Irritator(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.moveControl = new Irritator.IrritatorMoveControl(this);
        this.lookControl = new SmoothSwimmingLookControl(this, 20);
        this.swimmingSize = EntityDimensions.scalable(type.getDimensions().width, type.getDimensions().height * 0.62F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(NeoForgeMod.STEP_HEIGHT_ADDITION.get(), 0.8D);
    }

    public static boolean checkIrritatorSpawnRules(EntityType<? extends PathfinderMob> entity, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return Animal.checkMobSpawnRules(entity, pLevel, pSpawnType, pPos, pRandom) && isShallowWater(pLevel, pPos);
    }

    private static boolean isShallowWater(final LevelReader level, final BlockPos pos) {
        // check water block
        if(!level.isWaterAt(pos)) {
            return false;
        }
        // check air above
        if(!level.getBlockState(pos.above()).isAir()) {
            return false;
        }
        // check solid below
        final BlockPos posBelow = pos.below();
        if(!level.getBlockState(posBelow).isSolidRender(level, posBelow)) {
            return false;
        }
        // all checks passed
        return true;
    }

    //// METHODS ////

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new BreathAirGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0F, false));
        this.goalSelector.addGoal(4, new Irritator.MoveToShallowWaterGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new Irritator.IrritatorSwimmingGoal(this, 0.9D, 40));
        this.goalSelector.addGoal(5, new Irritator.IrritatorWanderGoal(this, 0.9D, 40));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true, false));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Drowned.class, true, false));
        this.targetSelector.addGoal(7, new ResetUniversalAngerTargetGoal<>(this, false));
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
        updateFluidOnBody();
        if(this.tickCount % 4 == 0 && isShallowWater(level(), blockPosition())) {
            this.isBodyInWater = false;
            refreshDimensions();
        }
        if(this.getAirSupply() < 60 && isBodyInWater() && level().getBlockState(BlockPos.containing(position().add(0, getDimensions(getPose()).height, 0))).isAir()) {
            setAirSupply(getMaxAirSupply());
        }
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AmphibiousPathNavigation(this, level);
    }

    @Override
    public float getWalkTargetValue(BlockPos pPos, LevelReader pLevel) {
        final BlockPos posBelow = pPos.below();
        final BlockState blockState = pLevel.getBlockState(posBelow);
        final PathType pathType = blockState.getBlockPathType(pLevel, posBelow, this);
        if(pathType == PathType.WATER_BORDER || pathType == PathType.WATER
                || blockState.is(Blocks.GRASS_BLOCK) || isShallowWater(pLevel, pPos)) {
            return 8.0F;
        }
        return super.getWalkTargetValue(pPos, pLevel);
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return dimensions.height * (isBodyInWater() ? 0.72F : 0.95F);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(0.5F, 0.0F, 0.5F);
    }

    @Override
    public int getMaxHeadXRot() {
        return 30;
    }

    @Override
    public int getMaxHeadYRot() {
        return 30;
    }

    //// NEUTRAL MOB ////

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

    //// SWIMMING ////

    @Override
    public double getFluidJumpThreshold() {
        return 1.1D;
    }

    @Override
    protected boolean isAffectedByFluids() {
        return !this.getEyeInFluidType().isAir();
    }

    @Override
    protected float getWaterSlowDown() {
        return super.getWaterSlowDown();
    }

    @Override
    public int getMaxAirSupply() {
        return 2400;
    }

    @Override
    protected int decreaseAirSupply(int pCurrentAir) {
        return super.decreaseAirSupply(pCurrentAir); //Math.max(1, super.decreaseAirSupply(pCurrentAir));
    }

    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        final EntityDimensions dimensions = isBodyInWater() ? swimmingSize : super.getDimensions(pPose);
        return dimensions.scale(this.getScale());
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isEffectiveAi() && this.isBodyInWater) {
            this.moveRelative(this.getSpeed(), pTravelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(pTravelVector);
        }
    }

    public boolean isBodyInWater() {
        return isBodyInWater;
    }

    private void updateFluidOnBody() {
        double bodyY = this.getY() + getDimensions(getPose()).height * 0.5D;
        BlockPos blockpos = BlockPos.containing(this.getX(), bodyY, this.getZ());
        FluidState fluidstate = this.level().getFluidState(blockpos);
        double fluidHeight = (float)blockpos.getY() + fluidstate.getHeight(this.level(), blockpos);
        final boolean isBodyInWater = !fluidstate.isEmpty() && fluidHeight > bodyY;
        if(isBodyInWater != this.isBodyInWater()) {
            this.isBodyInWater = isBodyInWater;
            refreshDimensions();
        }
    }

    //// SOUNDS ////

    @Override
    public int getAmbientSoundInterval() {
        return 160;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return PPRegistry.SoundReg.IRRITATOR_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return PPRegistry.SoundReg.IRRITATOR_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return PPRegistry.SoundReg.IRRITATOR_DEATH.get();
    }

    //// NBT ////

    @Override
    public void readAdditionalSaveData(final CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        readPersistentAngerSaveData(this.level(), tag);
    }

    @Override
    public void addAdditionalSaveData(final CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        addPersistentAngerSaveData(tag);
    }

    //// GECKOLIB ////

    private PlayState handleAnimation(AnimationState<Irritator> state) {
        final boolean inWater = isBodyInWater();
        final boolean isWalking = getDeltaMovement().horizontalDistanceSqr() > 2.5000003E-7F;
        if(inWater) {
            state.getController().setAnimation(ANIM_SWIM);
        } else if(isWalking) {
            state.getController().setAnimation(ANIM_WALK);
        } else {
            state.getController().setAnimation(ANIM_IDLE);
        }
        state.getController().setTransitionLength(4);
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

    //// MOVE CONTROL ////

    static class IrritatorMoveControl extends SmoothSwimmingMoveControl {
        private final Irritator entity;

        public IrritatorMoveControl(Irritator entity) {
            super(entity, 85, 10, 0.9F, 0.8F, false);
            this.entity = entity;
        }

        @Override
        public void tick() {
            super.tick();
            if(!entity.getEyeInFluidType().isAir()) {
                this.entity.setSpeed(this.entity.getSpeed() * 0.5F);
            }
        }
    }

    //// GOALS ////

    private static class MoveToShallowWaterGoal extends MoveToBlockGoal {

        public MoveToShallowWaterGoal(PathfinderMob pMob, final double speedModifier) {
            super(pMob, speedModifier, 8, 3);
            this.verticalSearchStart = -2;
        }

        @Override
        public boolean canUse() {
            return this.mob.onGround() && !this.mob.level().isWaterAt(this.mob.blockPosition()) && super.canUse();
        }

        @Override
        public void start() {
            super.start();
        }

        @Override
        protected boolean isValidTarget(final LevelReader level, final BlockPos pos) {
            return isShallowWater(level, pos.above(1));
        }
    }

    private static class IrritatorSwimmingGoal extends RandomSwimmingGoal {
        private final Irritator entity;

        public IrritatorSwimmingGoal(Irritator entity, double moveSpeed, int interval) {
            super(entity, moveSpeed, interval);
            this.entity = entity;
        }

        @Override
        public boolean canUse() {
            return !this.entity.getEyeInFluidType().isAir() && super.canUse();
        }
    }

    private static class IrritatorWanderGoal extends RandomStrollGoal {
        private final Irritator entity;

        public IrritatorWanderGoal(Irritator entity, double moveSpeed, int interval) {
            super(entity, moveSpeed, interval);
            this.entity = entity;
        }

        @Override
        public boolean canUse() {
            return this.entity.getEyeInFluidType().isAir() && super.canUse();
        }
    }
}
