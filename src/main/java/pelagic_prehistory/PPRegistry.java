package pelagic_prehistory;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import pelagic_prehistory.block.AnalyzerBlock;
import pelagic_prehistory.block.AnalyzerBlockEntity;
import pelagic_prehistory.block.CharniaBlock;
import pelagic_prehistory.block.SeaSpongeBlock;
import pelagic_prehistory.entity.Bawitius;
import pelagic_prehistory.entity.Cladoselache;
import pelagic_prehistory.entity.Cuttlefish;
import pelagic_prehistory.entity.Dunkleosteus;
import pelagic_prehistory.entity.Henodus;
import pelagic_prehistory.entity.Irritator;
import pelagic_prehistory.entity.Lepidotes;
import pelagic_prehistory.entity.Prognathodon;
import pelagic_prehistory.entity.Shonisaurus;
import pelagic_prehistory.entity.Orthacanthus;
import pelagic_prehistory.entity.Eurhinosaurus;
import pelagic_prehistory.entity.Spinosaurus;
import pelagic_prehistory.worldgen.GinkgoTreeFeature;
import pelagic_prehistory.worldgen.GinkgoTreeGrower;
import pelagic_prehistory.block.InfuserBlock;
import pelagic_prehistory.block.InfuserBlockEntity;
import pelagic_prehistory.entity.Dugong;
import pelagic_prehistory.entity.Plesiosaurus;
import pelagic_prehistory.entity.Pliosaurus;
import pelagic_prehistory.item.VialItem;
import pelagic_prehistory.menu.AnalyzerMenu;
import pelagic_prehistory.menu.InfuserMenu;
import pelagic_prehistory.recipe.AnalyzerRecipe;
import pelagic_prehistory.recipe.InfuserRecipe;
import pelagic_prehistory.worldgen.LocStructureProcessor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;


@SuppressWarnings("unused")
public final class PPRegistry {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PelagicPrehistory.MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PelagicPrehistory.MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PelagicPrehistory.MODID);
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PelagicPrehistory.MODID);
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, PelagicPrehistory.MODID);
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, PelagicPrehistory.MODID);
    private static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSORS = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, PelagicPrehistory.MODID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, PelagicPrehistory.MODID);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, PelagicPrehistory.MODID);
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PelagicPrehistory.MODID);
    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PelagicPrehistory.MODID);

    public static void register() {
        BlockReg.register();
        ItemReg.register();
        BlockEntityReg.register();
        EntityReg.register();
        FeatureReg.register();
        MenuReg.register();
        RecipeReg.register();
        SoundReg.register();
        CREATIVE_MODE_TABS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(PelagicPrehistory.MODID, () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + PelagicPrehistory.MODID))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> new ItemStack(ItemReg.CHARNIA_VIAL.get()))
                    .displayItems((parameters, output) -> {
                        ITEMS.getEntries().forEach(item -> output.accept(item.get()));
                    })
                    .build());

    public static final class ItemReg {

        private static final List<RegistryObject<Item>> VIAL_ITEMS = new ArrayList<>();
        private static final List<RegistryObject<Item>> SPAWN_EGGS = new ArrayList<>();

        public static void register() {
            ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
            VIAL_ITEMS.add(UNKNOWN_VIAL);
        }

        // CRAFTING MATERIALS //
        private static final FoodProperties CUTTLEFISH_FOOD = new FoodProperties.Builder().nutrition(2).saturationMod(0.1F).build();
        public static final RegistryObject<Item> RAW_CUTTLEFISH = register("raw_cuttlefish", () -> new Item(new Item.Properties().food(CUTTLEFISH_FOOD)));
        private static final FoodProperties CUTTLEFISH_STEW_FOOD = new FoodProperties.Builder().nutrition(8).saturationMod(0.3F).build();
        public static final RegistryObject<Item> CUTTLEFISH_STEW = register("cuttlefish_stew", () -> new Item(new Item.Properties().food(CUTTLEFISH_STEW_FOOD)));
        public static final RegistryObject<Item> FOSSIL = register("fossil", () -> new Item(new Item.Properties()));

        // SPAWN EGGS //
        public static final RegistryObject<Item> CUTTLEFISH_SPAWN_EGG = registerSpawnEgg("cuttlefish", EntityReg.CUTTLEFISH, 0xb16053, 0xC0C0C0);
        public static final RegistryObject<Item> DUGONG_SPAWN_EGG = registerSpawnEgg("dugong", EntityReg.DUGONG, 0xa9a47e, 0xC0C0C0);

        // VIALS, EGGS, AND SPAWN EGGS //
        public static final RegistryObject<Item> CHARNIA_VIAL = registerVial("charnia", 0xada74c);
        public static final RegistryObject<Item> GREEN_SEA_SPONGE_VIAL = registerVial("green_sea_sponge", 0x8c840a);
        public static final RegistryObject<Item> GINKGO_TREE_VIAL = registerVial("ginkgo_tree", 0x9bd367);
        public static final RegistryObject<Item> BAWITIUS_VIAL = registerVialAndEggs(EntityReg.BAWITIUS, "bawitius", "eggs",0xb75194);
        public static final RegistryObject<Item> CLADOSELACHE_VIAL = registerVialAndEggs(EntityReg.CLADOSELACHE, "cladoselache", "eggs",0xa254a9);
        public static final RegistryObject<Item> DUNKLEOSTEUS_VIAL = registerVialAndEggs(EntityReg.DUNKLEOSTEUS, "dunkleosteus", "eggs",0x3a9db3);
        public static final RegistryObject<Item> HENODUS_VIAL = registerVialAndEggs(EntityReg.HENODUS, "henodus", "egg",0x977343);
        public static final RegistryObject<Item> IRRITATOR_VIAL = registerVialAndEggs(EntityReg.IRRITATOR, "irritator", "egg",0x80872c);
        public static final RegistryObject<Item> LEPIDOTES_VIAL = registerVialAndEggs(EntityReg.LEPIDOTES, "lepidotes", "eggs",0xb7bb65);
        public static final RegistryObject<Item> PLESIOSAURUS_VIAL = registerVialAndEggs(EntityReg.PLESIOSAURUS, "plesiosaurus", "pup", 0x429389);
        public static final RegistryObject<Item> PLIOSAURUS_VIAL = registerVialAndEggs(EntityReg.PLIOSAURUS, "pliosaurus", "pup", 0x4e402c);
        public static final RegistryObject<Item> PROGNATHODON_VIAL = registerVialAndEggs(EntityReg.PROGNATHODON, "prognathodon", "pup", 0xa1ae75);
        public static final RegistryObject<Item> SHONISAURUS_VIAL = registerVialAndEggs(EntityReg.SHONISAURUS, "shonisaurus", "pup", 0x3a746b);
        public static final RegistryObject<Item> ORTHACANTHUS_VIAL = registerVialAndEggs(EntityReg.ORTHACANTHUS, "orthacanthus", "eggs", 0x717071);
        public static final RegistryObject<Item> EURHINOSAURUS_VIAL = registerVialAndEggs(EntityReg.EURHINOSAURUS, "eurhinosaurus", "pup", 0xBC8723);
        public static final RegistryObject<Item> SPINOSAURUS_VIAL = registerVialAndEggs(EntityReg.SPINOSAURUS, "spinosaurus", "egg", 0x334CBF);
        public static final RegistryObject<Item> UNKNOWN_VIAL = ITEMS.register("unknown_vial", () -> new VialItem(0x4c4c4c, new Item.Properties()));

        /**
         * Creates a registry object for a block item and adds it to the mod creative tab
         * @param block the block
         * @return the registry object
         */
        private static RegistryObject<Item> registerBlockItem(final RegistryObject<Block> block) {
            return register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
        }

        /**
         * Creates a registry object for the given vial item and adds it to the mod creative tab
         * @param name the registry name
         * @param color the vial color
         * @return the item registry object
         */
        private static RegistryObject<Item> registerVial(final String name, final int color) {
            final RegistryObject<Item> vial = register(name + "_vial", () -> new VialItem(color, new Item.Properties().stacksTo(16)));
            VIAL_ITEMS.add(vial);
            return vial;
        }

        /**
         * Creates a registry object for the given vial item and adds it to the mod creative tab
         * @param name the registry name
         * @param entityType the entity type supplier
         * @param bgColor the background color
         * @param fgColor the foreground color
         * @return the item registry object
         */
        private static <T extends Mob> RegistryObject<Item> registerSpawnEgg(final String name, final RegistryObject<EntityType<T>> entityType, final int bgColor, final int fgColor) {
            final RegistryObject<Item> spawnEgg = ITEMS.register(name + "_spawn_egg", () -> new ForgeSpawnEggItem(entityType, bgColor, fgColor, new Item.Properties()));
            SPAWN_EGGS.add(spawnEgg);
            return spawnEgg;
        }

        /**
         * Creates a registry object for the given vial item, egg item, spawn egg item,
         * and adds them to the correct creative tabs
         * @param entityType the entity type for the egg items
         * @param name the registry name
         * @param eggSuffix the suffix for the egg item
         * @param color the vial color
         * @return the item registry object
         */
        private static <T extends Mob> RegistryObject<Item> registerVialAndEggs(final RegistryObject<EntityType<T>> entityType, final String name, final String eggSuffix, final int color) {
            final RegistryObject<Item> vial = registerVial(name, color);
            final RegistryObject<Item> egg = register(name + "_" + eggSuffix, () -> new ForgeSpawnEggItem(entityType, -1, -1, new Item.Properties()));
            final RegistryObject<Item> spawnEgg = registerSpawnEgg(name, entityType, color, 0xC0C0C0);
            return vial;
        }

        /**
         * Creates a registry object for the given item and adds it to the mod creative tab
         * @param name the registry name
         * @param supplier the item supplier
         * @return the item registry object
         */
        private static RegistryObject<Item> register(final String name, final Supplier<Item> supplier) {
            return ITEMS.register(name, supplier);
        }

        public static List<RegistryObject<Item>> getVialItems() {
            return ImmutableList.copyOf(VIAL_ITEMS);
        }

        public static List<RegistryObject<Item>> getSpawnEggs() {
            return ImmutableList.copyOf(SPAWN_EGGS);
        }

    }

    public static final class BlockReg {

        public static void register() {
            BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<Block> ANALYZER = registerWithItem("analyzer", () ->
                new AnalyzerBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));
        public static final RegistryObject<Block> INFUSER = registerWithItem("infuser", () ->
                new InfuserBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).requiresCorrectToolForDrops().strength(3.5F).sound(SoundType.METAL)));
        public static final RegistryObject<Block> ANCIENT_SEDIMENT = registerBlockSlabStairsWallPlateButton("ancient_sediment", BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.DEEPSLATE));
        public static final RegistryObject<Block> ANCIENT_SEDIMENT_BRICKS = registerWithItem("ancient_sediment_bricks", () ->
                new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.DEEPSLATE)));
        public static final RegistryObject<Block> ANCIENT_SEDIMENT_FOSSIL = registerWithItem("ancient_sediment_fossil", () ->
                new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).requiresCorrectToolForDrops().strength(4.0F, 8.0F).sound(SoundType.DEEPSLATE), UniformInt.of(0, 2)));
        public static final RegistryObject<Block> ANCIENT_SEDIMENT_TABLETS = registerWithItem("ancient_sediment_tablets", () ->
                new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.DEEPSLATE)));
        public static final RegistryObject<Block> ANCIENT_SEDIMENT_COAL_ORE = registerWithItem("ancient_sediment_coal_ore", () ->
                new DropExperienceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_BROWN).requiresCorrectToolForDrops().strength(4.0F, 8.0F).sound(SoundType.DEEPSLATE), UniformInt.of(0, 2)));
        public static final RegistryObject<Block> CHARNIA = registerWithItem("charnia", () ->
                new CharniaBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).noCollission().instabreak().sound(SoundType.WET_GRASS).offsetType(BlockBehaviour.OffsetType.XZ).replaceable().pushReaction(PushReaction.DESTROY)),
                b -> ItemReg.register("charnia", () -> new DoubleHighBlockItem(b.get(), new Item.Properties())));
        public static final RegistryObject<Block> GREEN_SEA_SPONGE = registerWithItem("green_sea_sponge", () ->
                new SeaSpongeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).noCollission().instabreak().sound(SoundType.WET_GRASS).replaceable().pushReaction(PushReaction.DESTROY)));
        public static final RegistryObject<Block> GINKGO_SAPLING = registerWithItem("ginkgo_sapling", () ->
                new SaplingBlock(new GinkgoTreeGrower(), BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.GRASS).pushReaction(PushReaction.DESTROY)));
        public static final RegistryObject<Block> GINKGO_LOG = registerWoodBlocks("ginkgo", 2.0F, 3.0F, MapColor.WOOD, MapColor.SAND, 5, 5, 20);
        public static final RegistryObject<Block> GINKGO_LEAVES = registerLeaves("ginkgo", 30, 60);

        private static RegistryObject<Block> registerWithItem(final String name, final Supplier<Block> supplier) {
            return registerWithItem(name, supplier, ItemReg::registerBlockItem);
        }

        private static RegistryObject<Block> registerWithItem(final String name, final Supplier<Block> blockSupplier, final Function<RegistryObject<Block>, RegistryObject<Item>> itemSupplier) {
            final RegistryObject<Block> block = BLOCKS.register(name, blockSupplier);
            final RegistryObject<Item> item = itemSupplier.apply(block);
            return block;
        }

        private static RegistryObject<Block> registerBlockSlabStairsWallPlateButton(final String name, final BlockBehaviour.Properties properties) {
            final RegistryObject<Block> block = registerWithItem(name, () -> new Block(properties));
            final RegistryObject<Block> slab = registerWithItem(name + "_slab", () -> new SlabBlock(properties));
            final RegistryObject<Block> stairs = registerWithItem(name + "_stairs", () -> new StairBlock(() -> block.get().defaultBlockState(), properties));
            final RegistryObject<Block> walls = registerWithItem(name + "_wall", () -> new WallBlock(properties));
            final RegistryObject<Block> pressurePlate = registerWithItem(name + "_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, properties, BlockSetType.STONE));
            final RegistryObject<Block> button = registerWithItem(name + "_button", () -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).sound(SoundType.STONE).pushReaction(PushReaction.DESTROY), BlockSetType.STONE, 20, false));
            return block;
        }

        /**
         * Registers all of the following: log, stripped log, wood, stripped wood, planks, stairs, slab,
         * door, trapdoor, button
         *
         * @param name               the base registry name
         * @param strength           the destroy time
         * @param hardness           the explosion resistance
         * @param side               the material color of the side
         * @param top                the material color of the top
         * @param fireSpread         the fire spread chance. The higher the number returned, the faster fire will spread around this block.
         * @param logFlammability    Chance that fire will spread and consume the log. 300 being a 100% chance, 0, being a 0% chance.
         * @param planksFlammability Chance that fire will spread and consume the plank. 300 being a 100% chance, 0, being a 0% chance.
         * @return the log block
         */
        private static RegistryObject<Block> registerWoodBlocks(final String name, final float strength, final float hardness,
                                                                final MapColor side, final MapColor top,
                                                                final int fireSpread, final int logFlammability, final int planksFlammability) {
            // create properties
            final BlockBehaviour.Properties woodProperties = BlockBehaviour.Properties.of().mapColor(side).strength(strength, hardness).sound(SoundType.WOOD).ignitedByLava();
            final BlockBehaviour.Properties logProperties = BlockBehaviour.Properties.of().mapColor((state) -> state.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? top : side).strength(strength, hardness).sound(SoundType.WOOD).ignitedByLava();
            final Block.Properties doorProperties = BlockBehaviour.Properties.of().mapColor(side).strength(strength, hardness).sound(SoundType.WOOD).noOcclusion().isValidSpawn((b, i, p, a) -> false).ignitedByLava().pushReaction(PushReaction.DESTROY);

            // register blocks
            final RegistryObject<Block> strippedLog = BLOCKS.register("stripped_" + name + "_log", () -> new FlammableRotatedPillarBlock(woodProperties, fireSpread, logFlammability));
            final RegistryObject<Block> strippedWood = BLOCKS.register("stripped_" + name + "_wood", () -> new FlammableRotatedPillarBlock(woodProperties, fireSpread, logFlammability));
            final RegistryObject<Block> log = registerWithItem(name + "_log", () -> new FlammableRotatedPillarBlock(strippedLog, logProperties, fireSpread, logFlammability));
            final RegistryObject<Block> wood = registerWithItem(name + "_wood", () -> new FlammableRotatedPillarBlock(strippedWood, woodProperties, fireSpread, logFlammability));
            ItemReg.registerBlockItem(strippedLog);
            ItemReg.registerBlockItem(strippedWood);
            final RegistryObject<Block> planks = registerWithItem(name + "_planks", () -> new FlammableBlock(woodProperties, fireSpread, planksFlammability));
            final RegistryObject<Block> slab = registerWithItem(name + "_slab", () -> new FlammableSlabBlock(woodProperties, fireSpread, planksFlammability));
            final RegistryObject<Block> stairs = registerWithItem(name + "_stairs", () -> new FlammableStairBlock(() -> planks.get().defaultBlockState(), woodProperties, fireSpread, planksFlammability));
            final RegistryObject<Block> door = registerWithItem(name + "_door", () -> new DoorBlock(doorProperties, BlockSetType.OAK));
            final RegistryObject<Block> trapdoor = registerWithItem(name + "_trapdoor", () -> new TrapDoorBlock(doorProperties, BlockSetType.OAK));
            final RegistryObject<Block> pressurePlate = registerWithItem(name + "_pressure_plate", () -> new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, woodProperties, BlockSetType.OAK));
            final RegistryObject<Block> button = registerWithItem(name + "_button", () -> new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).sound(SoundType.WOOD).pushReaction(PushReaction.DESTROY), BlockSetType.OAK, 30, true));
            final RegistryObject<Block> fence = registerWithItem(name + "_fence", () -> new FenceBlock(woodProperties));
            final RegistryObject<Block> fenceGate = registerWithItem(name + "_fence_gate", () -> new FenceGateBlock(woodProperties, WoodType.OAK));
            return log;
        }

        /**
         * @param name the base registry name
         * @param fireSpread   the fire spread chance. The higher the number returned, the faster fire will spread around this block.
         * @param flammability Chance that fire will spread and consume the block. 300 being a 100% chance, 0, being a 0% chance.
         * @return the leaves block registry object
         */
        private static RegistryObject<Block> registerLeaves(final String name, final int fireSpread, final int flammability) {
            final BlockBehaviour.Properties properties = Block.Properties.of().mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(SoundType.GRASS)
                    .noOcclusion().isValidSpawn(Blocks::ocelotOrParrot).isSuffocating((s, r, p) -> false).isViewBlocking((s, r, p) -> false).ignitedByLava().pushReaction(PushReaction.DESTROY);
            return registerWithItem(name + "_leaves", () -> new FlammableLeavesBlock(properties, fireSpread, flammability));
        }

    }

    public static final class BlockEntityReg {

        public static void register() {
            BLOCK_ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

       public static final RegistryObject<BlockEntityType<AnalyzerBlockEntity>> ANALYZER = BLOCK_ENTITY_TYPES.register("analyzer", () ->
                BlockEntityType.Builder.of((pos, state) -> new AnalyzerBlockEntity(BlockEntityReg.ANALYZER.get(), pos, state),
                                BlockReg.ANALYZER.get())
                        .build(null));

        public static final RegistryObject<BlockEntityType<InfuserBlockEntity>> INFUSER = BLOCK_ENTITY_TYPES.register("infuser", () ->
                BlockEntityType.Builder.of((pos, state) -> new InfuserBlockEntity(BlockEntityReg.INFUSER.get(), pos, state),
                                BlockReg.INFUSER.get())
                        .build(null));
    }

    public static final class EntityReg {

        public static void register() {
            ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
            FMLJavaModLoadingContext.get().getModEventBus().addListener(EntityReg::onEntityAttributeCreation);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(EntityReg::onRegisterSpawnPlacement);
        }

        public static void onEntityAttributeCreation(final EntityAttributeCreationEvent event) {
            event.put(BAWITIUS.get(), Bawitius.createAttributes().build());
            event.put(CLADOSELACHE.get(), Cladoselache.createAttributes().build());
            event.put(CUTTLEFISH.get(), Cuttlefish.createAttributes().build());
            event.put(DUGONG.get(), Dugong.createAttributes().build());
            event.put(DUNKLEOSTEUS.get(), Dunkleosteus.createAttributes().build());
            event.put(HENODUS.get(), Henodus.createAttributes().build());
            event.put(IRRITATOR.get(), Irritator.createAttributes().build());
            event.put(LEPIDOTES.get(), Lepidotes.createAttributes().build());
            event.put(PLESIOSAURUS.get(), Plesiosaurus.createAttributes().build());
            event.put(PLIOSAURUS.get(), Pliosaurus.createAttributes().build());
            event.put(PROGNATHODON.get(), Prognathodon.createAttributes().build());
            event.put(SHONISAURUS.get(), Shonisaurus.createAttributes().build());
            event.put(ORTHACANTHUS.get(), Orthacanthus.createAttributes().build());
            event.put(EURHINOSAURUS.get(), Eurhinosaurus.createAttributes().build());
            event.put(SPINOSAURUS.get(), Spinosaurus.createAttributes().build());
        }

        public static void onRegisterSpawnPlacement(final SpawnPlacementRegisterEvent event) {
            event.register(BAWITIUS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(CLADOSELACHE.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(CUTTLEFISH.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(DUGONG.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(DUNKLEOSTEUS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(HENODUS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(IRRITATOR.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Irritator::checkIrritatorSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(LEPIDOTES.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(PLESIOSAURUS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(PLIOSAURUS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(PROGNATHODON.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(SHONISAURUS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(ORTHACANTHUS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(EURHINOSAURUS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WaterAnimal::checkSurfaceWaterAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
            event.register(SPINOSAURUS.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Spinosaurus::checkSpinosaurusSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        }

        public static final RegistryObject<EntityType<Bawitius>> BAWITIUS = ENTITY_TYPES.register("bawitius", () ->
                EntityType.Builder.of(Bawitius::new, MobCategory.WATER_CREATURE)
                        .sized(0.95F, 0.88F)
                        .build("bawitius"));

        public static final RegistryObject<EntityType<Cladoselache>> CLADOSELACHE = ENTITY_TYPES.register("cladoselache", () ->
                EntityType.Builder.of(Cladoselache::new, MobCategory.WATER_CREATURE)
                        .sized(1.46F, 0.625F)
                        .build("cladoselache"));

        public static final RegistryObject<EntityType<Cuttlefish>> CUTTLEFISH = ENTITY_TYPES.register("cuttlefish", () ->
                EntityType.Builder.of(Cuttlefish::new, MobCategory.WATER_CREATURE)
                        .sized(0.48F, 0.748F)
                        .build("cuttlefish"));

        public static final RegistryObject<EntityType<Dugong>> DUGONG = ENTITY_TYPES.register("dugong", () ->
                EntityType.Builder.of(Dugong::new, MobCategory.WATER_CREATURE)
                        .sized(0.98F, 0.746F)
                        .build("dugong"));

        public static final RegistryObject<EntityType<Dunkleosteus>> DUNKLEOSTEUS = ENTITY_TYPES.register("dunkleosteus", () ->
                EntityType.Builder.of(Dunkleosteus::new, MobCategory.WATER_CREATURE)
                        .sized(1.15F, 1.15F)
                        .build("dunkleosteus"));

        public static final RegistryObject<EntityType<Henodus>> HENODUS = ENTITY_TYPES.register("henodus", () ->
                EntityType.Builder.of(Henodus::new, MobCategory.WATER_CREATURE)
                        .sized(0.746F, 0.188F)
                        .build("henodus"));

        public static final RegistryObject<EntityType<Irritator>> IRRITATOR = ENTITY_TYPES.register("irritator", () ->
                EntityType.Builder.of(Irritator::new, MobCategory.CREATURE)
                        .sized(1.44F, 1.96F)
                        .build("irritator"));

        public static final RegistryObject<EntityType<Lepidotes>> LEPIDOTES = ENTITY_TYPES.register("lepidotes", () ->
                EntityType.Builder.of(Lepidotes::new, MobCategory.WATER_CREATURE)
                        .sized(0.875F, 0.625F)
                        .build("lepidotes"));

        public static final RegistryObject<EntityType<Plesiosaurus>> PLESIOSAURUS = ENTITY_TYPES.register("plesiosaurus", () ->
                EntityType.Builder.of(Plesiosaurus::new, MobCategory.WATER_CREATURE)
                        .sized(0.98F, 0.48F)
                        .build("plesiosaurus"));

        public static final RegistryObject<EntityType<Pliosaurus>> PLIOSAURUS = ENTITY_TYPES.register("pliosaurus", () ->
                EntityType.Builder.of(Pliosaurus::new, MobCategory.WATER_CREATURE)
                        .sized(1.875F, 0.92F)
                        .build("pliosaurus"));

        public static final RegistryObject<EntityType<Prognathodon>> PROGNATHODON = ENTITY_TYPES.register("prognathodon", () ->
                EntityType.Builder.of(Prognathodon::new, MobCategory.WATER_CREATURE)
                        .sized(1.825F, 0.98F)
                        .build("prognathodon"));

        public static final RegistryObject<EntityType<Shonisaurus>> SHONISAURUS = ENTITY_TYPES.register("shonisaurus", () ->
                EntityType.Builder.of(Shonisaurus::new, MobCategory.WATER_CREATURE)
                        .sized(5.98F, 4.24F)
                        .build("shonisaurus"));

        public static final RegistryObject<EntityType<Orthacanthus>> ORTHACANTHUS = ENTITY_TYPES.register("orthacanthus", () ->
                EntityType.Builder.of(Orthacanthus::new, MobCategory.WATER_CREATURE)
                        .sized(1.46F, 0.625F)
                        .build("orthacanthus"));

        public static final RegistryObject<EntityType<Eurhinosaurus>> EURHINOSAURUS = ENTITY_TYPES.register("eurhinosaurus", () ->
                EntityType.Builder.of(Eurhinosaurus::new, MobCategory.WATER_CREATURE)
                        .sized(1.15F, 1.15F)
                        .build("eurhinosaurus"));

        public static final RegistryObject<EntityType<Spinosaurus>> SPINOSAURUS = ENTITY_TYPES.register("spinosaurus", () ->
                EntityType.Builder.of(Spinosaurus::new, MobCategory.CREATURE)
                        .sized(2.44F, 3.96F)
                        .build("spinosaurus"));
    }

    public static final class FeatureReg {

        public static void register() {
            FEATURES.register(FMLJavaModLoadingContext.get().getModEventBus());
            STRUCTURE_PROCESSORS.register(FMLJavaModLoadingContext.get().getModEventBus());
            FMLJavaModLoadingContext.get().getModEventBus().addListener(FeatureReg::registerStructureProcessors);
        }

        public static StructureProcessorType<LocStructureProcessor> LOC_PROCESSOR;
        public static RegistryObject<GinkgoTreeFeature> GINKGO_TREE_FEATURE = FEATURES.register("ginkgo_tree", () -> new GinkgoTreeFeature(TreeConfiguration.CODEC));

        private static void registerStructureProcessors(final FMLCommonSetupEvent event) {
            event.enqueueWork(() -> {
                ResourceLocation locProcessorId = new ResourceLocation(PelagicPrehistory.MODID, "loc");
                LOC_PROCESSOR = StructureProcessorType.register(locProcessorId.toString(), LocStructureProcessor.CODEC);
            });
        }
    }

    public static final class MenuReg {

        public static void register() {
            MENU_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<MenuType<AnalyzerMenu>> ANALYZER = MENU_TYPES.register("analyzer", () ->
                IForgeMenuType.create(((windowId, inv, data) -> {
                    final BlockPos pos = data.readBlockPos();
                    return new AnalyzerMenu(MenuReg.ANALYZER.get(), windowId, inv, (AnalyzerBlockEntity) inv.player.level().getBlockEntity(pos));
                })
            )
        );

        public static final RegistryObject<MenuType<InfuserMenu>> INFUSER = MENU_TYPES.register("infuser", () ->
                IForgeMenuType.create(((windowId, inv, data) -> {
                            final BlockPos pos = data.readBlockPos();
                            return new InfuserMenu(MenuReg.INFUSER.get(), windowId, inv, (InfuserBlockEntity) inv.player.level().getBlockEntity(pos));
                        })
                )
        );
    }

    public static final class RecipeReg {

        public static void register() {
            RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
            RECIPE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        public static final RegistryObject<RecipeType<AnalyzerRecipe>> ANALYZING_TYPE = RECIPE_TYPES.register("analyzing", () -> RecipeType.simple(new ResourceLocation(PelagicPrehistory.MODID, "analyzing")));
        public static final RegistryObject<RecipeType<InfuserRecipe>> INFUSING_TYPE = RECIPE_TYPES.register("infusing", () -> RecipeType.simple(new ResourceLocation(PelagicPrehistory.MODID, "infusing")));

        public static final RegistryObject<RecipeSerializer<AnalyzerRecipe>> ANALYZING_SERIALIZER = RECIPE_SERIALIZERS.register("analyzing", () -> new AnalyzerRecipe.Serializer());
        public static final RegistryObject<RecipeSerializer<InfuserRecipe>> INFUSING_SERIALIZER = RECIPE_SERIALIZERS.register("infusing", () -> new InfuserRecipe.Serializer());
    }

    public static final class SoundReg {

        public static void register() {
            SOUND_EVENTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }

        private static RegistryObject<SoundEvent> registerSound(final String name) {
            ResourceLocation id = new ResourceLocation(PelagicPrehistory.MODID, name);
            return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
        }

        public static final RegistryObject<SoundEvent> IRRITATOR_AMBIENT = registerSound("entity.irritator.ambient");
        public static final RegistryObject<SoundEvent> IRRITATOR_HURT = registerSound("entity.irritator.hurt");
        public static final RegistryObject<SoundEvent> IRRITATOR_DEATH = registerSound("entity.irritator.death");
        public static final RegistryObject<SoundEvent> PLESIOSAURUS_AMBIENT = registerSound("entity.plesiosaurus.ambient");
        public static final RegistryObject<SoundEvent> PLESIOSAURUS_HURT = registerSound("entity.plesiosaurus.hurt");
        public static final RegistryObject<SoundEvent> PLESIOSAURUS_DEATH = registerSound("entity.plesiosaurus.death");
        public static final RegistryObject<SoundEvent> PLIOSAURUS_AMBIENT = registerSound("entity.pliosaurus.ambient");
        public static final RegistryObject<SoundEvent> PLIOSAURUS_HURT = registerSound("entity.pliosaurus.hurt");
        public static final RegistryObject<SoundEvent> PLIOSAURUS_DEATH = registerSound("entity.pliosaurus.death");
        public static final RegistryObject<SoundEvent> PROGNATHODON_AMBIENT = registerSound("entity.prognathodon.ambient");
        public static final RegistryObject<SoundEvent> PROGNATHODON_HURT = registerSound("entity.prognathodon.hurt");
        public static final RegistryObject<SoundEvent> PROGNATHODON_DEATH = registerSound("entity.prognathodon.death");
        public static final RegistryObject<SoundEvent> SHONISAURUS_AMBIENT = registerSound("entity.shonisaurus.ambient");
        public static final RegistryObject<SoundEvent> SHONISAURUS_HURT = registerSound("entity.shonisaurus.hurt");
        public static final RegistryObject<SoundEvent> SHONISAURUS_DEATH = registerSound("entity.shonisaurus.death");
        public static final RegistryObject<SoundEvent> EURHINOSAURUS_AMBIENT = registerSound("entity.eurhinosaurus.ambient");
        public static final RegistryObject<SoundEvent> EURHINOSAURUS_HURT = registerSound("entity.eurhinosaurus.hurt");
        public static final RegistryObject<SoundEvent> EURHINOSAURUS_DEATH = registerSound("entity.eurhinosaurus.death");
        public static final RegistryObject<SoundEvent> SPINOSAURUS_AMBIENT = registerSound("entity.spinosaurus.ambient");
        public static final RegistryObject<SoundEvent> SPINOSAURUS_HURT = registerSound("entity.spinosaurus.hurt");
        public static final RegistryObject<SoundEvent> SPINOSAURUS_DEATH = registerSound("entity.spinosaurus.death");
    }

    //// FLAMMABLE BLOCKS ////

    private static class FlammableBlock extends Block {

        private final int fireSpread;
        private final int flammability;

        public FlammableBlock(Properties properties, int fireSpread, int flammability) {
            super(properties);
            this.fireSpread = fireSpread;
            this.flammability = flammability;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
            return fireSpread;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
            return flammability;
        }
    }

    private static class FlammableRotatedPillarBlock extends RotatedPillarBlock {

        @Nullable
        private final Supplier<Block> strippedResult;
        private final int fireSpread;
        private final int flammability;

        public FlammableRotatedPillarBlock(@Nullable final Supplier<Block> strippedResult, Properties properties, int fireSpread, int flammability) {
            super(properties);
            this.strippedResult = strippedResult;
            this.fireSpread = fireSpread;
            this.flammability = flammability;
        }

        public FlammableRotatedPillarBlock(Properties properties, int fireSpread, int flammability) {
            this(null, properties, fireSpread, flammability);
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
            return fireSpread;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
            return flammability;
        }

        @Override
        public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction toolAction, boolean simulate) {
            if (toolAction == ToolActions.AXE_STRIP && strippedResult != null) {
                return strippedResult.get().defaultBlockState().setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
            }
            return super.getToolModifiedState(state, context, toolAction, simulate);
        }
    }

    private static class FlammableSlabBlock extends SlabBlock {

        private final int fireSpread;
        private final int flammability;

        public FlammableSlabBlock(Properties properties, int fireSpread, int flammability) {
            super(properties);
            this.fireSpread = fireSpread;
            this.flammability = flammability;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
            return fireSpread;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
            return flammability;
        }
    }

    private static class FlammableStairBlock extends StairBlock {

        private final int fireSpread;
        private final int flammability;

        public FlammableStairBlock(Supplier<BlockState> state, Properties properties, int fireSpread, int flammability) {
            super(state, properties);
            this.fireSpread = fireSpread;
            this.flammability = flammability;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
            return fireSpread;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
            return flammability;
        }
    }

    private static class FlammableLeavesBlock extends LeavesBlock {

        private final int fireSpread;
        private final int flammability;

        public FlammableLeavesBlock(Properties properties, int fireSpread, int flammability) {
            super(properties);
            this.fireSpread = fireSpread;
            this.flammability = flammability;
        }

        @Override
        public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
            return fireSpread;
        }

        @Override
        public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
            return flammability;
        }
    }
}
