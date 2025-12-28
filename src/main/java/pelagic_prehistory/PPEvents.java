package pelagic_prehistory;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

public final class PPEvents {

    public static void register() {
        FMLJavaModLoadingContext.get().getModEventBus().register(ModHandler.class);
        MinecraftForge.EVENT_BUS.register(ForgeHandler.class);
    }

    public static final class ForgeHandler {

        private static final TagKey<EntityType<?>> ENTITY_TYPE_PREVENTS_DROWNED = ForgeRegistries.ENTITY_TYPES.tags()
                .createTagKey(new ResourceLocation(PelagicPrehistory.MODID, "prevents_drowned"));

        /**
         * Used to add prevent drowned from spawning near certain entities
         *
         * @param event the spawn event
         **/
        @SubscribeEvent
        public static void onLivingCheckSpawn(final MobSpawnEvent.FinalizeSpawn event) {
            final int horizontalRadius = 16;
            final int verticalRadius = 32;
            if (event.getEntity().getType() == EntityType.DROWNED
                    && event.getLevel() instanceof ServerLevel level
                    && (event.getSpawnType() == MobSpawnType.NATURAL
                    || event.getSpawnType() == MobSpawnType.REINFORCEMENT
                    || event.getSpawnType() == MobSpawnType.PATROL
                    || event.getSpawnType() == MobSpawnType.SPAWNER)) {

                // determine spawn area
                final BlockPos eventPos = BlockPos.containing(event.getX(), event.getY(), event.getZ());
                final AABB aabb = new AABB(eventPos).inflate(horizontalRadius, verticalRadius, horizontalRadius);

                // search area for a matching entity
                LevelEntityGetter<Entity> entityGetter = level.getEntities();
                entityGetter.get(EntityTypeTagTest.forTag(ENTITY_TYPE_PREVENTS_DROWNED), aabb, e -> {
                    if (event.getResult() != Event.Result.DENY) {
                        event.setResult(Event.Result.DENY);
                    }
                    return net.minecraft.util.AbortableIterationConsumer.Continuation.ABORT;
                });
            }
        }

        @SubscribeEvent
        public static void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
            // remove for release
            // event.getEntity().displayClientMessage(Component.literal("You are using a beta version of Pelagic Prehistory, do not distribute").withStyle(ChatFormatting.AQUA), false);
        }
    }

    public static final class ModHandler {

    }

    private static final class EntityTypeTagTest implements EntityTypeTest<Entity, Entity> {

        private final TagKey<EntityType<?>> tag;

        private EntityTypeTagTest(TagKey<EntityType<?>> tag) {
            this.tag = tag;
        }

        public static EntityTypeTagTest forTag(final TagKey<EntityType<?>> tag) {
            return new EntityTypeTagTest(tag);
        }

        @Override
        @Nullable
        public Entity tryCast(Entity pEntity) {
            if(pEntity.getType().is(tag)) {
                return pEntity;
            }
            return null;
        }

        @Override
        public Class<? extends Entity> getBaseClass() {
            return Entity.class;
        }
    }
}
