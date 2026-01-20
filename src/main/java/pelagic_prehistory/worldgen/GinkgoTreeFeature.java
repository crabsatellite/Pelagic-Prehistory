package pelagic_prehistory.worldgen;


import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import com.mojang.serialization.Codec;
import pelagic_prehistory.PelagicPrehistory;

public class GinkgoTreeFeature extends Feature<TreeConfiguration> {

    private static final TagKey<Block> GINKGO_TREE_REPLACEABLE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(PelagicPrehistory.MODID, "ginkgo_tree_replaceable"));
    private static final LocStructureProcessor HAS_REPLACEABLE = new LocStructureProcessor(new TagMatchTest(GINKGO_TREE_REPLACEABLE));


    private static final ResourceLocation[] TREES = {
            ResourceLocation.fromNamespaceAndPath(PelagicPrehistory.MODID, "ginkgo_tree/tree_0"),
            ResourceLocation.fromNamespaceAndPath(PelagicPrehistory.MODID, "ginkgo_tree/tree_1"),
            ResourceLocation.fromNamespaceAndPath(PelagicPrehistory.MODID, "ginkgo_tree/tree_2"),
    };

    public GinkgoTreeFeature(final Codec<TreeConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<TreeConfiguration> context) {
        // rotation / mirror
        Mirror mirror = Mirror.NONE;
        Rotation rotation = Rotation.getRandom(context.random());

        // template for tree
        final StructureTemplateManager manager = context.level().getLevel().getStructureManager();
        final StructureTemplate template = manager.getOrCreate(Util.getRandom(TREES, context.random()));

        // position for tree
        final BlockPos offset = new BlockPos(-3, 0, -3);
        BlockPos pos = context.origin().offset(offset.rotate(rotation));

        // placement settings
        BoundingBox mbb = new BoundingBox(pos.getX() - 8, pos.getY() - 16, pos.getZ() - 8, pos.getX() + 8, pos.getY() + 16, pos.getZ() + 8);
        StructurePlaceSettings placement = new StructurePlaceSettings()
                .setRotation(rotation).setMirror(mirror).setRandom(context.random()).setBoundingBox(mbb)
                .addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR)
                .addProcessor(new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE))
                .addProcessor(new LocStructureProcessor(new TagMatchTest(GINKGO_TREE_REPLACEABLE)))
                /*.addProcessor(HAS_REPLACEABLE)*/;
        // actually build using the template
        template.placeInWorld(context.level(), pos, pos, placement, context.random(), 2);
        return true;
    }
}
