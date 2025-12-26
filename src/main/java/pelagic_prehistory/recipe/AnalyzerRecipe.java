package pelagic_prehistory.recipe;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import pelagic_prehistory.PPRegistry;
import pelagic_prehistory.PelagicPrehistory;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class AnalyzerRecipe implements Recipe<Container> {

    private static final WeightedEntry.Wrapper<ItemStack> EMPTY_WRAPPER = WeightedEntry.wrap(ItemStack.EMPTY, 1);

    private static final Codec<ItemStack> ITEM_OR_STACK_CODEC = Codec.either(ForgeRegistries.ITEMS.getCodec(), ItemStack.CODEC)
            .xmap(either -> either.map(ItemStack::new, Function.identity()),
                    stack -> stack.getCount() == 1 && !stack.hasTag() ? Either.left(stack.getItem()) : Either.right(stack));

    private static final Codec<WeightedEntry.Wrapper<ItemStack>> WEIGHTED_ENTRY_CODEC = WeightedEntry.Wrapper.codec(ITEM_OR_STACK_CODEC);
    private static final Codec<List<WeightedEntry.Wrapper<ItemStack>>> WEIGHTED_ENTRY_LIST_CODEC = WEIGHTED_ENTRY_CODEC.listOf().fieldOf("pool").codec();
    private static final Codec<List<WeightedEntry.Wrapper<ItemStack>>> WEIGHTED_ENTRY_OR_LIST_CODEC = Codec.either(WEIGHTED_ENTRY_CODEC, WEIGHTED_ENTRY_LIST_CODEC)
            .xmap(either -> either.map(ImmutableList::of, Function.identity()),
                    list -> list.size() == 1 ? Either.left(list.get(0)) : Either.right(list));

    private final ResourceLocation id;
    private final Ingredient input;
    private final List<WeightedEntry.Wrapper<ItemStack>> results;

    public AnalyzerRecipe(final ResourceLocation id, final Ingredient input, final List<WeightedEntry.Wrapper<ItemStack>> results) {
        this.id = id;
        this.input = input;
        this.results = ImmutableList.copyOf(results);
    }

    @Override
    public boolean matches(Container pContainer, Level pLevel) {
        // validate recipe has input
        if(input.isEmpty()) {
            return false;
        }
        // validate container has item
        if(pContainer.getContainerSize() < 1) {
            return false;
        }
        // check if items are the same
        return input.test(pContainer.getItem(0));
    }

    /**
     * @param pContainer the input container
     * @param registryAccess the registry access
     * @return the output item
     * @deprecated use {@link #assemble(Container, RandomSource)}
     */
    @Deprecated
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
        return PPRegistry.ItemReg.UNKNOWN_VIAL.get().getDefaultInstance();
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PPRegistry.RecipeReg.ANALYZING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return PPRegistry.RecipeReg.ANALYZING_TYPE.get();
    }

    /**
     * @param container the container
     * @param random a random source
     * @return a randomly sampled item stack from the results list, may be empty
     */
    public ItemStack assemble(final Container container, final RandomSource random) {
        return Optional.ofNullable(WeightedUtil.sample(results, random)).orElse(EMPTY_WRAPPER).getData();
    }

    public static class Serializer implements RecipeSerializer<AnalyzerRecipe> {

        private static final String INPUT = "input";
        private static final String OUTPUT = "output";

        @Override
        public AnalyzerRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            // parse input item
            final Ingredient input = Ingredient.fromJson(pSerializedRecipe.get(INPUT));
            // parse result items
            final List<WeightedEntry.Wrapper<ItemStack>> results = WEIGHTED_ENTRY_OR_LIST_CODEC.parse(JsonOps.INSTANCE, pSerializedRecipe.get(OUTPUT))
                    .resultOrPartial(s -> PelagicPrehistory.LOGGER.error("[AnalyzerRecipe] Failed to parse recipe results \"" + pRecipeId + "\":\n" + s))
                    .orElse(List.of());
            // create recipe
            return new AnalyzerRecipe(pRecipeId, input, results);
        }

        @Override
        public @Nullable AnalyzerRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            final Ingredient input = Ingredient.fromNetwork(pBuffer);
            final List<WeightedEntry.Wrapper<ItemStack>> results = pBuffer.readWithCodec(net.minecraft.nbt.NbtOps.INSTANCE, WEIGHTED_ENTRY_OR_LIST_CODEC);
            return new AnalyzerRecipe(pRecipeId, input, results);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, AnalyzerRecipe pRecipe) {
            pRecipe.input.toNetwork(pBuffer);
            pBuffer.writeWithCodec(net.minecraft.nbt.NbtOps.INSTANCE, WEIGHTED_ENTRY_OR_LIST_CODEC, pRecipe.results);
        }
    }
}
