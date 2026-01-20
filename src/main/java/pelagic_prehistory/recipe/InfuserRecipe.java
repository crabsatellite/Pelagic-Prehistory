package pelagic_prehistory.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import pelagic_prehistory.PPRegistry;

public class InfuserRecipe implements Recipe<InfuserRecipeInput> {

    public static final MapCodec<InfuserRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(r -> r.ingredient),
            Ingredient.CODEC.optionalFieldOf("base", Ingredient.of(Items.EGG)).forGetter(r -> r.base),
            ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result)
    ).apply(instance, InfuserRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, InfuserRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.ingredient,
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.base,
            ItemStack.STREAM_CODEC, r -> r.result,
            InfuserRecipe::new
    );

    private final Ingredient ingredient;
    private final Ingredient base;
    private final ItemStack result;

    public InfuserRecipe(Ingredient ingredient, Ingredient base, ItemStack result) {
        this.ingredient = ingredient;
        this.base = base;
        this.result = result;
    }

    @Override
    public boolean matches(InfuserRecipeInput recipeInput, Level level) {
        // validate recipe has input
        if (ingredient.isEmpty()) {
            return false;
        }
        // check if items are the same (order does not matter)
        final ItemStack itemA = recipeInput.input1();
        final ItemStack itemB = recipeInput.input2();
        if (ingredient.test(itemA) && base.test(itemB)) {
            return true;
        }
        if (ingredient.test(itemB) && base.test(itemA)) {
            return true;
        }
        return false;
    }

    @Override
    public ItemStack assemble(InfuserRecipeInput recipeInput, HolderLookup.Provider registries) {
        return getResultItem(registries);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PPRegistry.RecipeReg.INFUSING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return PPRegistry.RecipeReg.INFUSING_TYPE.get();
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public Ingredient getBase() {
        return base;
    }

    public static class Serializer implements RecipeSerializer<InfuserRecipe> {

        @Override
        public MapCodec<InfuserRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, InfuserRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
