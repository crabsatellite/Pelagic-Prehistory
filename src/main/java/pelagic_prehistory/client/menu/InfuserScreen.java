package pelagic_prehistory.client.menu;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import pelagic_prehistory.PelagicPrehistory;
import pelagic_prehistory.menu.InfuserMenu;

public class InfuserScreen extends AbstractContainerScreen<InfuserMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(PelagicPrehistory.MODID, "textures/gui/infuser.png");

    private static final int IMAGE_WIDTH = 176;
    private static final int IMAGE_HEIGHT = 166;

    private static final int PROGRESS_X = 72;
    private static final int PROGRESS_Y = 18;
    private static final int PROGRESS_WIDTH = 54;
    private static final int PROGRESS_HEIGHT = 51;

    public InfuserScreen(InfuserMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        renderBackground(pGuiGraphics);
        // render background
        pGuiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        // render progress
        if(getMenu().isCrafting()) {
            final int progressHeight = getMenu().getScaledProgress(PROGRESS_HEIGHT);
            pGuiGraphics.blit(TEXTURE, this.leftPos + PROGRESS_X, this.topPos + PROGRESS_Y + (PROGRESS_HEIGHT - progressHeight), IMAGE_WIDTH, (PROGRESS_HEIGHT - progressHeight), PROGRESS_WIDTH, progressHeight);
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
