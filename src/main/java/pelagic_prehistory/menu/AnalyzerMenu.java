package pelagic_prehistory.menu;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import pelagic_prehistory.PPRegistry;
import pelagic_prehistory.PelagicPrehistory;
import pelagic_prehistory.block.AnalyzerBlockEntity;

public class AnalyzerMenu extends AbstractContainerMenu {

    public static final int INPUT_SIZE = 1;
    public static final int OUTPUT_SIZE = 5;
    public static final int CONTAINER_SIZE = INPUT_SIZE + OUTPUT_SIZE;
    public static final int CONTAINER_DATA_SIZE = 2;

    private static final int PLAYER_INV_Y = 84;

    private final Inventory inventory;
    private final AnalyzerBlockEntity blockEntity;
    private final Container container;
    private final ContainerData data;

    public AnalyzerMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory pPlayerInventory, AnalyzerBlockEntity blockEntity) {
        this(pMenuType, pContainerId, pPlayerInventory, new SimpleContainer(CONTAINER_SIZE), new SimpleContainerData(CONTAINER_DATA_SIZE), blockEntity);
    }

    public AnalyzerMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory pPlayerInventory, Container container, ContainerData data, AnalyzerBlockEntity blockEntity) {
        super(pMenuType, pContainerId);
        checkContainerSize(blockEntity, CONTAINER_SIZE);
        this.inventory = pPlayerInventory;
        this.container = container;
        this.data = data;
        this.blockEntity = blockEntity;

        // add container slots
        addSlot(new FossilSlot(this.container, 0, 80, 16));
        for(int i = 0, x0 = 44, y0 = 54; i < OUTPUT_SIZE; i++) {
            addSlot(new VialSlot(this.container, i + 1, x0 + i * 18, y0));
        }

        // add player inventory
        for(int playerRows = 0; playerRows < 3; ++playerRows) {
            for(int playerCols = 0; playerCols < 9; ++playerCols) {
                this.addSlot(new Slot(pPlayerInventory, playerCols + playerRows * 9 + 9, 8 + playerCols * 18, PLAYER_INV_Y + playerRows * 18));
            }
        }
        // add hotbar
        for(int hotbarCols = 0; hotbarCols < 9; ++hotbarCols) {
            this.addSlot(new Slot(pPlayerInventory, hotbarCols, 8 + hotbarCols * 18, PLAYER_INV_Y + 3 * 18 + 4));
        }

        // add data
        addDataSlots(data);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < CONTAINER_SIZE) {
                if (!this.moveItemStackTo(itemstack1, CONTAINER_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, INPUT_SIZE, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return this.container.stillValid(pPlayer);
    }

    // DATA //

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress(int scale) {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        if(progress == 0 || maxProgress == 0) {
            return 0;
        }
        return Mth.ceil((float) scale * (float) progress / (float) maxProgress);
    }

    // SLOTS //

    private static class FossilSlot extends Slot {
        private static final TagKey<Item> FOSSIL = BuiltInRegistries.ITEM.tags().createTagKey(ResourceLocation.fromNamespaceAndPath(PelagicPrehistory.MODID, "fossil"));

        public FossilSlot(Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            return pStack.is(FOSSIL);
        }
    }

    private static class VialSlot extends Slot {
        public VialSlot(Container pContainer, int pSlot, int pX, int pY) {
            super(pContainer, pSlot, pX, pY);
        }

        @Override
        public boolean mayPlace(ItemStack pStack) {
            return false;
        }
    }
}
