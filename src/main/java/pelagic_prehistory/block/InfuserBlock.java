package pelagic_prehistory.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import pelagic_prehistory.PPRegistry;

public class InfuserBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public InfuserBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            // drop items from inventory
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (!level.isClientSide() && tileentity instanceof InfuserBlockEntity blockEntity) {
                blockEntity.dropAllItems();
                level.updateNeighbourForOutputSignal(pos, this);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide() && pLevel.getBlockEntity(pPos) instanceof InfuserBlockEntity blockEntity) {
            NetworkHooks.openScreen((ServerPlayer) pPlayer, blockEntity, data -> data.writeBlockPos(pPos));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    // BLOCK ENTITY //

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return PPRegistry.BlockEntityReg.INFUSER.get().create(pPos, pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide() || !pState.is(this) ? null : (BlockEntityTicker<T>) (BlockEntityTicker<InfuserBlockEntity>) (InfuserBlockEntity::tick);
    }


    // REDSTONE //

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level worldIn, BlockPos pos) {
        final BlockEntity blockEntity = worldIn.getBlockEntity(pos);
        if (blockEntity instanceof Container container) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer(container);
        }
        return 0;
    }
}
