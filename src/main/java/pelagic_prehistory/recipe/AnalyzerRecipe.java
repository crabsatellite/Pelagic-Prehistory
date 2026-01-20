package pelagic_prehistory.recipe;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import pelagic_prehistory.PPRegistry;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class AnalyzerRecipe implements Recipe<AnalyzerRecipeInput> {

    private static final WeightedEntry.Wrapper<ItemStack> EMPTY_WRAPPER = WeightedEntry.wrap(ItemStack.EMPTY, 1);

    private static final Codec<WeightedEntry.Wrapper<ItemStack>> WEIGHTED_ENTRY_CODEC = WeightedEntry.Wrapper.codec(ItemStack.CODEC);
    private static final Codec<List<WeightedEntry.Wrapper<ItemStack>>> WEIGHTED_ENTRY_LIST_CODEC = WEIGHTED_ENTRY_CODEC.listOf().fieldOf("pool").codec();
    private static final Codec<List<WeightedEntry.Wrapper<ItemStack>>> WEIGHTED_ENTRY_OR_LIST_CODEC = Codec.either(WEIGHTED_ENTRY_CODEC, WEIGHTED_ENTRY_LIST_CODEC)
            .xmap(either -> either.map(ImmutableList::of, Function.identity()),
                    list -> list.size() == 1 ? Either.left(list.get(0)) : Either.right(list));

    public static final MapCodec<AnalyzerRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("input").forGetter(r -> r.input),
            WEIGHTED_ENTRY_OR_LIST_CODEC.fieldOf("output").forGetter(r -> r.results)
    ).apply(instance, AnalyzerRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AnalyzerRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, r -> r.input,
            ByteBufCodecs.fromCodec(WEIGHTED_ENTRY_OR_LIST_CODEC), r -> r.results,
            AnalyzerRecipe::new
    );

    private final Ingredient input;
    private final List<WeightedEntry.Wrapper<ItemStack>> results;

    public AnalyzerRecipe(final Ingredient input, final List<WeightedEntry.Wrapper<ItemStack>> results) {
        this.input = input;
        this.results = ImmutableList.copyOf(results);
    }

    @Override
    public boolean matches(AnalyzerRecipeInput recipeInput, Level level) {
        // validate recipe has input
        if (input.isEmpty()) {
            return false;
        }
        // check if items match
        return input.test(recipeInput.input());
    }

    @Override
    public ItemStack assemble(AnalyzerRecipeInput recipeInput, HolderLookup.Provider registries) {
        return getResultItem(registries);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return PPRegistry.ItemReg.UNKNOWN_VIAL.get().getDefaultInstance();
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
     * @param recipeInput the recipe input
     * @param random a random source
     * @return a randomly sampled item stack from the results list, may be empty
     */
    public ItemStack assemble(final AnalyzerRecipeInput recipeInput, final RandomSource random) {
        return Optional.ofNullable(WeightedUtil.sample(results, random)).orElse(EMPTY_WRAPPER).data();
    }

    public static class Serializer implements RecipeSerializer<AnalyzerRecipe> {

        @Override
        public MapCodec<AnalyzerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, AnalyzerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
