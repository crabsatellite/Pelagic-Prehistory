package pelagic_prehistory.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.MultiItemValue;
import org.jetbrains.annotations.Nullable;
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
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        // determines the recipe base ingredient for this item and adds it to the tooltip
        if(pLevel != null && (pIsAdvanced.isAdvanced() || Screen.hasShiftDown())) {
            // locate the recipe, if any
            final Optional<InfuserRecipe> oRecipe = pLevel.getRecipeManager().getAllRecipesFor(PPRegistry.RecipeReg.INFUSING_TYPE.get()).stream()
                    .filter(p -> p.getIngredient().test(pStack)).findFirst();
            // validate recipe and base ingredient
            if(oRecipe.isPresent() && !oRecipe.get().getBase().isEmpty()) {
                // load recipe base ingredient and convert to JSON
                final Ingredient base = oRecipe.get().getBase();
                JsonElement baseJsonElement = base.toJson();
                // hardcoded support for tag ingredients (which are expanded before being sent to the client)
                if(baseJsonElement.isJsonArray() && !baseJsonElement.getAsJsonArray().isEmpty()) {
                    // parse the array
                    final JsonArray baseJsonArray = baseJsonElement.getAsJsonArray();
                    // load the n-th item based on elapsed ticks
                    baseJsonElement = baseJsonArray.get(((int)(pLevel.getGameTime() / 20) % baseJsonArray.size()));
                } else if(!baseJsonElement.isJsonObject()) {
                    return;
                }
                // hardcoded support for item ingredients
                final JsonObject baseJson = baseJsonElement.getAsJsonObject();
                // create a component for the recipe base ingredient (hardcoded support for tags and items only)
                final Component baseName;
                if(baseJson.has("item")) {
                    final ResourceLocation key = ResourceLocation.fromNamespaceAndPath(baseJson.get("item").getAsString());
                    final Item item = BuiltInRegistries.ITEM.getValue(key);
                    baseName = item.getDescription().copy().withStyle(ChatFormatting.WHITE);
                } else if(baseJson.has("tag")) {
                    baseName = Component.translatable("item.pelagic_prehistory.vial.tooltip.tag", baseJson.get("tag").getAsString()).withStyle(ChatFormatting.WHITE);;
                } else {
                    return;
                }
                // create a component for the infuser block
                final Component infuserName = PPRegistry.BlockReg.INFUSER.get().getName();
                // create a tooltip with all information
                final Component tooltip = Component.translatable("item.pelagic_prehistory.vial.tooltip", baseName, infuserName).withStyle(ChatFormatting.GRAY);
                pTooltipComponents.add(tooltip);
            }
        }
    }
}
