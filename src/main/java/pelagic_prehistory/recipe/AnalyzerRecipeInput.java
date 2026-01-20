package pelagic_prehistory.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * A simple RecipeInput wrapper for the Analyzer machine
 */
public record AnalyzerRecipeInput(ItemStack input) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("No item for index " + index);
        }
        return input;
    }

    @Override
    public int size() {
        return 1;
    }
}
