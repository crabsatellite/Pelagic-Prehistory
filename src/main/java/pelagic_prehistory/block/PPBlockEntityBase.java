package pelagic_prehistory.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class PPBlockEntityBase<I extends RecipeInput, R extends Recipe<I>> extends BlockEntity implements Container, MenuProvider {

    protected final NonNullList<ItemStack> inventory;
    protected IItemHandler itemHandler;

    protected final ContainerData data;
    protected int progress;
    protected int maxProgress;

    public PPBlockEntityBase(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
        this.inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        this.itemHandler = createUnSidedHandler();
        this.maxProgress = 120;
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                switch (pIndex) {
                    case 0: return PPBlockEntityBase.this.progress;
                    case 1: return PPBlockEntityBase.this.maxProgress;
                    default: return -1;
                }
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0: PPBlockEntityBase.this.progress = pValue; break;
                    case 1: PPBlockEntityBase.this.maxProgress = pValue; break;
                    default: break;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void inventoryChanged() {
        if (getLevel() != null && !getLevel().isClientSide()) {
            getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.inventoryChanged();
    }

    // RECIPE //

    protected abstract I createRecipeInput();

    protected abstract Optional<RecipeHolder<R>> getRecipeFor(final Level level, final I input);

    protected abstract void assembleRecipe(final Level level, final I input, R recipe);

    protected boolean hasRecipe(Level level) {
        // create recipe input
        final I input = createRecipeInput();
        // locate matching recipe
        Optional<RecipeHolder<R>> oRecipe = getRecipeFor(level, input);
        return oRecipe.isPresent();
    }

    protected void assembleRecipe(Level level) {
        // create recipe input
        final I input = createRecipeInput();
        // locate matching recipe
        Optional<RecipeHolder<R>> oRecipe = getRecipeFor(level, input);
        if(oRecipe.isEmpty()) {
            return;
        }
        // assemble recipe
        this.assembleRecipe(level, input, oRecipe.get().value());
    }

    protected void resetProgress() {
        this.progress = 0;
    }

    // CAPABILITY //

    protected abstract IItemHandler createUnSidedHandler();

    public IItemHandler getItemHandler(@Nullable Direction side) {
        return itemHandler;
    }

    // CONTAINER //

    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    public void dropAllItems() {
        if (this.level != null && !this.level.isClientSide()) {
            Containers.dropContents(this.level, this.getBlockPos(), this.getInventory());
        }
        this.inventoryChanged();
    }

    public boolean isFull() {
        for(ItemStack itemStack : this.inventory) {
            if(itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        for(int i = 0, n = getContainerSize(); i < n; i++) {
            if(!getItem(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int pSlot) {
        return pSlot >= 0 && pSlot < this.inventory.size() ? this.inventory.get(pSlot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int pSlot, int pAmount) {
        return ContainerHelper.removeItem(this.inventory, pSlot, pAmount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pSlot) {
        return ContainerHelper.takeItem(this.inventory, pSlot);
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
        if (pSlot >= 0 && pSlot < this.inventory.size()) {
            this.inventory.set(pSlot, pStack);
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if (pPlayer.level().getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return pPlayer.position().closerThan(Vec3.atCenterOf(worldPosition), 8.0D);
        }
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
        this.setChanged();
    }

    // MENU PROVIDER //

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    // NBT //

    private static final String KEY_PROGRESS = "Progress";
    private static final String KEY_MAX_PROGRESS = "MaxProgress";

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt(KEY_PROGRESS);
        maxProgress = tag.getInt(KEY_MAX_PROGRESS);
        ContainerHelper.loadAllItems(tag, getInventory(), registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(KEY_PROGRESS, progress);
        tag.putInt(KEY_MAX_PROGRESS, maxProgress);
        ContainerHelper.saveAllItems(tag, getInventory(), registries);
    }

    // SOUND //

    protected abstract SoundEvent getSound();

    protected void tickSound(final Level level, final BlockPos pos) {
        final SoundEvent sound = getSound();
        final int soundInterval = getSoundInterval();
        if (sound != null && soundInterval > 0 && level.getRandom().nextInt(soundInterval) == 0) {
            level.playSound(null, pos, getSound(), SoundSource.BLOCKS, 1.0F, getSoundPitch(level.getRandom()));
        }
    }

    protected int getSoundInterval() {
        if(progress > 0 && progress < maxProgress) {
            return 80;
        }
        return 0;
    }

    protected float getSoundPitch(final RandomSource random) {
        return 0.8F + random.nextFloat() * 0.4F;
    }
}
