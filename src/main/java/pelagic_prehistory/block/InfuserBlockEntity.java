package pelagic_prehistory.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pelagic_prehistory.PPRegistry;
import pelagic_prehistory.menu.AnalyzerMenu;
import pelagic_prehistory.menu.InfuserMenu;
import pelagic_prehistory.recipe.InfuserRecipe;
import pelagic_prehistory.recipe.InfuserRecipe;

import java.util.Optional;

public class InfuserBlockEntity extends PPBlockEntityBase<InfuserRecipe> {

    public InfuserBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
        this.maxProgress = 200;
    }

    // TICKING //

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, InfuserBlockEntity blockEntity) {
        // only run logic on server side
        if(level.isClientSide()) {
            return;
        }
        if(blockEntity.hasRecipe(level)) {
            // update progress
            blockEntity.progress = Math.min(blockEntity.progress + 1, blockEntity.maxProgress);
            if(blockEntity.progress >= blockEntity.maxProgress) {
                blockEntity.assembleRecipe(level);
            }
        } else {
            blockEntity.resetProgress();
        }
        // sound
        blockEntity.tickSound(level, blockPos);
    }

    @Override
    protected SoundEvent getSound() {
        // TODO infuser sound
        return SoundEvents.CAMPFIRE_CRACKLE;
    }

    // BLOCK ENTITY BASE //

    @Override
    protected Container createInputContainer() {
        return new SimpleContainer(getItem(0), getItem(1));
    }

    @Override
    protected Optional<InfuserRecipe> getRecipeFor(Level level, Container input) {
        return level.getRecipeManager().getRecipeFor(PPRegistry.RecipeReg.INFUSING_TYPE.get(), input, level);
    }

    @Override
    protected void assembleRecipe(Level level, Container input, InfuserRecipe recipe) {
        final ItemStack output = recipe.assemble(input, level.registryAccess());
        if(output.isEmpty()) {
            return;
        }
        final IItemHandler itemHandler = this.itemHandler.orElse(EmptyHandler.INSTANCE);
        if(itemHandler.insertItem(2, output.copy(), false).isEmpty()) {
            // remove input
            this.removeItem(0, 1);
            this.removeItem(1, 1);
            this.resetProgress();
            this.setChanged();
        }
    }

    @Override
    public int getContainerSize() {
        return 3;
    }

    @Override
    protected IItemHandler createUnSidedHandler() {
        return new InvWrapper(this) {
            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return slot < 2 ? ItemStack.EMPTY : super.extractItem(slot, amount, simulate);
            }

            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return /*slot > 1 ? stack : */super.insertItem(slot, stack, simulate);
            }
        };
    }

    // MENU PROVIDER //

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new InfuserMenu(PPRegistry.MenuReg.INFUSER.get(), pContainerId, pPlayerInventory, this, this.data, this);
    }
}
