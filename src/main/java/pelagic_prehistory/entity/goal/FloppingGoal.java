package pelagic_prehistory.entity.goal;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;
import java.util.function.Supplier;

public class FloppingGoal extends Goal {

    private final PathfinderMob entity;
    private final double deltaMotionY;
    private final int interval;

    public FloppingGoal(PathfinderMob entity, double deltaMotionY, int interval) {
        this.entity = entity;
        this.deltaMotionY = deltaMotionY;
        this.interval = Math.max(1, interval);
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canUse() {
        return !entity.isInWaterOrBubble();
    }

    @Override
    public void tick() {
        final double horizontalDeltaMotion = getHorizontalDeltaMotion();
        if(!entity.isInWaterOrBubble() && entity.onGround() && entity.verticalCollision && this.entity.getRandom().nextInt(getInterval()) == 0) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(
                    (entity.getRandom().nextFloat() * 2.0F - 1.0F) * horizontalDeltaMotion,
                    getVerticalDeltaMotion(),
                    (entity.getRandom().nextFloat() * 2.0F - 1.0F) * horizontalDeltaMotion));
            entity.setOnGround(false);
            entity.hasImpulse = true;
            entity.playSound(getFlopSound(), 0.5F, entity.getVoicePitch());
        }
    }

    public PathfinderMob getEntity() {
        return entity;
    }

    public double getVerticalDeltaMotion() {
        return deltaMotionY;
    }

    public double getHorizontalDeltaMotion() {
        return 0.05F;
    }

    public int getInterval() {
        return interval;
    }

    public SoundEvent getFlopSound() {
        return SoundEvents.ELDER_GUARDIAN_FLOP;
    }
}
