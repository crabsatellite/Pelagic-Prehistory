package pelagic_prehistory.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AABB;
import pelagic_prehistory.PPRegistry;
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

public class Spinosaurus extends PathfinderMob implements NeutralMob, GeoEntity {

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

    public Spinosaurus(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 30, 20, 0.2F, 0.8F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 15);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.15D)
                .add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.STEP_HEIGHT, 1.6D);
    }

    public static boolean checkSpinosaurusSpawnRules(EntityType<? extends PathfinderMob> entity, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return Animal.checkMobSpawnRules(entity, pLevel, pSpawnType, pPos, pRandom) && isShallowWater(pLevel, pPos);
    }

    private static boolean isShallowWater(LevelReader level, BlockPos pos) {
        // check water block
        if (!level.isWaterAt(pos)) {
            return false;
        }
        // check air above
        if (!level.getBlockState(pos.above()).isAir()) {
            return false;
        }
        // check solid below
        BlockPos posBelow = pos.below();
        return level.getBlockState(posBelow).isSolidRender(level, posBelow);
    }

    //// METHODS ////

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.5D, false));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D, 10) {
            @Override
            public boolean canUse() {
                return !Spinosaurus.this.isInWater() && super.canUse();
            }
        });
        this.goalSelector.addGoal(3, new RandomSwimmingGoal(this, 2.5D, 10) {
            @Override
            public boolean canUse() {
                return super.canUse() && Spinosaurus.this.isInWater();
            }
        });
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Drowned.class, true, false));
        this.targetSelector.addGoal(7, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide()) {
            this.updatePersistentAnger((ServerLevel) this.level(), true);
        }
    }

    @Override
    public void tick() {
        super.tick();
        updateFluidOnBody();
    }

    @Override
    public boolean isPushedByFluid() {
        return true;
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
        BlockPos posBelow = pPos.below();
        BlockState blockState = pLevel.getBlockState(posBelow);
        PathType pathType = blockState.getBlockPathType(pLevel, posBelow, this);
        if (pathType == PathType.WATER_BORDER || pathType == PathType.WATER
                || blockState.is(Blocks.GRASS_BLOCK) || isShallowWater(pLevel, pPos)) {
            return 8.0F;
        }
        return super.getWalkTargetValue(pPos, pLevel);
    }

    protected boolean shouldSwim() {
        return !getEyeInFluidType().isAir();
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(0.5F, 0.0F, 0.5F);
    }

    private void updateFluidOnBody() {
        double bodyY = this.getY() + (double) this.getDimensions(this.getPose()).height() * 0.5D;
        BlockPos blockpos = BlockPos.containing(this.getX(), bodyY, this.getZ());
        FluidState fluidstate = this.level().getFluidState(blockpos);
        double fluidHeight = (float) blockpos.getY() + fluidstate.getHeight(this.level(), blockpos);
        this.isBodyInWater = !fluidstate.isEmpty() && fluidHeight > bodyY;
    }

    public boolean isBodyInWater() {
        return this.isBodyInWater;
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

    //// SOUNDS ////

    @Override
    public int getMaxAirSupply() {
        return 160;
    }

    @Override
    protected float getSoundVolume() {
        return 0.9F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return PPRegistry.SoundReg.SPINOSAURUS_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return PPRegistry.SoundReg.SPINOSAURUS_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return PPRegistry.SoundReg.SPINOSAURUS_DEATH.get();
    }

    //// NBT ////

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addPersistentAngerSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readPersistentAngerSaveData(this.level(), tag);
    }

    //// GECKOLIB ////

    private PlayState handleAnimation(AnimationState<Spinosaurus> state) {
        boolean isWalking = this.getDeltaMovement().horizontalDistanceSqr() > 1.5000003372733772E-7;
        boolean inWater = this.isBodyInWater;

        if (inWater) {
            state.getController().setAnimation(ANIM_SWIM);
        } else if (isWalking) {
            state.getController().setAnimation(ANIM_WALK);
        } else {
            state.getController().setAnimation(ANIM_IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 6, this::handleAnimation));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
