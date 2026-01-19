package pelagic_prehistory.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.Nullable;
import pelagic_prehistory.PPRegistry;

public class InfuserRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final Ingredient base;
    private final ItemStack result;

    public InfuserRecipe(final ResourceLocation id, Ingredient ingredient, Ingredient base, ItemStack result) {
        this.id = id;
        this.ingredient = ingredient;
        this.base = base;
        this.result = result;
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        // validate recipe has input
        if(ingredient.isEmpty()) {
            return false;
        }
        // validate container has items
        if(pContainer.getContainerSize() < 2) {
            return false;
        }
        // check if items are the same (order does not matter)
        final ItemStack itemA = pContainer.getItem(0);
        final ItemStack itemB = pContainer.getItem(1);
        if(ingredient.test(itemA) && base.test(itemB)) {
            return true;
        }
        if(ingredient.test(itemB) && base.test(itemA)) {
            return true;
        }
        return false;
    }

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess registryAccess) {
        return getResultItem(registryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
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

        private static final String INGREDIENT = "ingredient";
        private static final String BASE = "base";
        private static final String RESULT = "result";

        @Override
        public InfuserRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            // parse input item
            final Ingredient ingredient = /* TODO: Use Ingredient.CODEC.parse(JsonOps.INSTANCE, json).result().orElse(Ingredient.EMPTY) instead */ Ingredient.fromJson(pSerializedRecipe.get(INGREDIENT));
            final Ingredient base = pSerializedRecipe.has(BASE) ? /* TODO: Use Ingredient.CODEC.parse(JsonOps.INSTANCE, json).result().orElse(Ingredient.EMPTY) instead */ Ingredient.fromJson(pSerializedRecipe.get(BASE)) : Ingredient.of(Items.EGG);
            final ItemStack result = CraftingHelper.getItemStack(pSerializedRecipe.getAsJsonObject(RESULT), true, true);
            // create recipe
            return new InfuserRecipe(pRecipeId, ingredient, base, result);
        }

        @Override
        public @Nullable InfuserRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            final Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            final Ingredient base = Ingredient.fromNetwork(pBuffer);
            final ItemStack result = pBuffer.readItem();
            return new InfuserRecipe(pRecipeId, ingredient, base, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, InfuserRecipe pRecipe) {
            pRecipe.ingredient.toNetwork(pBuffer);
            pRecipe.base.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.result);
        }
    }
}
