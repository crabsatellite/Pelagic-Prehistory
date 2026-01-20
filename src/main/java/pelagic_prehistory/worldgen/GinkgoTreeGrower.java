package pelagic_prehistory.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import pelagic_prehistory.PelagicPrehistory;

import java.util.Optional;

public final class GinkgoTreeGrower {

    private static final ResourceKey<ConfiguredFeature<?, ?>> FEATURE_KEY = ResourceKey.create(
            Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(PelagicPrehistory.MODID, "ginkgo_tree")
    );

    public static final TreeGrower GROWER = new TreeGrower(
            PelagicPrehistory.MODID + ":ginkgo",
            Optional.empty(),
            Optional.of(FEATURE_KEY),
            Optional.empty()
    );

    private GinkgoTreeGrower() {}
}
