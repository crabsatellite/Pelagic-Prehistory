package pelagic_prehistory.item;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import pelagic_prehistory.PPRegistry;
import pelagic_prehistory.recipe.InfuserRecipe;

import java.util.List;
import java.util.Optional;

public class VialItem extends Item {

    private final int color;

    public VialItem(final int color, Properties pProperties) {
        super(pProperties);
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        // determines the recipe base ingredient for this item and adds it to the tooltip
        Level pLevel = pContext.level();
        if(pLevel != null && (pIsAdvanced.isAdvanced() || Screen.hasShiftDown())) {
            // locate the recipe, if any
            final Optional<RecipeHolder<InfuserRecipe>> oRecipeHolder = pLevel.getRecipeManager().getAllRecipesFor(PPRegistry.RecipeReg.INFUSING_TYPE.get()).stream()
                    .filter(holder -> holder.value().getIngredient().test(pStack)).findFirst();
            // validate recipe and base ingredient
            if(oRecipeHolder.isPresent()) {
                InfuserRecipe recipe = oRecipeHolder.get().value();
                final Ingredient base = recipe.getBase();
                if(base.isEmpty()) {
                    return;
                }
                // Get the matching items from the ingredient
                ItemStack[] items = base.getItems();
                if(items.length == 0) {
                    return;
                }
                // Pick an item based on elapsed ticks for cycling display
                int index = (int)((pLevel.getGameTime() / 20) % items.length);
                ItemStack displayItem = items[index];
                
                // create a component for the recipe base ingredient
                final Component baseName = displayItem.getHoverName().copy().withStyle(ChatFormatting.WHITE);
                
                // create a component for the infuser block
                final Component infuserName = PPRegistry.BlockReg.INFUSER.get().getName();
                // create a tooltip with all information
                final Component tooltip = Component.translatable("item.pelagic_prehistory.vial.tooltip", baseName, infuserName).withStyle(ChatFormatting.GRAY);
                pTooltipComponents.add(tooltip);
            }
        }
    }
}
