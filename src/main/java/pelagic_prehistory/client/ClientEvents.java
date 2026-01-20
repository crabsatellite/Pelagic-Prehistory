package pelagic_prehistory.client;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import pelagic_prehistory.PPRegistry;
import pelagic_prehistory.client.entity.BawitiusRenderer;
import pelagic_prehistory.client.entity.CladoselacheRenderer;
import pelagic_prehistory.client.entity.CuttlefishRenderer;
import pelagic_prehistory.client.entity.DugongRenderer;
import pelagic_prehistory.client.entity.DunkleosteusRenderer;
import pelagic_prehistory.client.entity.EurhinosaurusRenderer;
import pelagic_prehistory.client.entity.HenodusRenderer;
import pelagic_prehistory.client.entity.IrritatorRenderer;
import pelagic_prehistory.client.entity.LepidotesRenderer;
import pelagic_prehistory.client.entity.OrthacanthusRenderer;
import pelagic_prehistory.client.entity.PlesiosaurusRenderer;
import pelagic_prehistory.client.entity.PliosaurusRenderer;
import pelagic_prehistory.client.entity.PrognathodonRenderer;
import pelagic_prehistory.client.entity.ShonisaurusRenderer;
import pelagic_prehistory.client.entity.SpinosaurusRenderer;
import pelagic_prehistory.client.menu.AnalyzerScreen;
import pelagic_prehistory.client.menu.InfuserScreen;
import pelagic_prehistory.item.VialItem;

public final class ClientEvents {

    public static void register(IEventBus modEventBus) {
        modEventBus.register(ModHandler.class);
        // ForgeHandler is empty, no need to register it
    }

    public static final class ModHandler {
        @SubscribeEvent
        public static void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(PPRegistry.EntityReg.BAWITIUS.get(), BawitiusRenderer::new);
            event.registerEntityRenderer(PPRegistry.EntityReg.CLADOSELACHE.get(), CladoselacheRenderer::new);
            event.registerEntityRenderer(PPRegistry.EntityReg.CUTTLEFISH.get(), CuttlefishRenderer::new);
            event.registerEntityRenderer(PPRegistry.EntityReg.DUGONG.get(), DugongRenderer::new);
            event.registerEntityRenderer(PPRegistry.EntityReg.DUNKLEOSTEUS.get(), DunkleosteusRenderer::new);
            event.registerEntityRenderer(PPRegistry.EntityReg.HENODUS.get(), HenodusRenderer::new);
            event.registerEntityRenderer(PPRegistry.EntityReg.IRRITATOR.get(), IrritatorRenderer::new);
            event.registerEntityRenderer(PPRegistry.EntityReg.LEPIDOTES.get(), LepidotesRenderer::new);
            event.registerEntityRenderer(PPRegistry.EntityReg.PLESIOSAURUS.get(), PlesiosaurusRenderer::new);
            event.registerEntityRenderer(PPRegistry.EntityReg.PLIOSAURUS.get(), PliosaurusRenderer::new);
            event.registerEntityRenderer(PPRegistry.EntityReg.PROGNATHODON.get(), PrognathodonRenderer::new);
            event.registerEntityRenderer(PPRegistry.EntityReg.SHONISAURUS.get(), ShonisaurusRenderer::new);
            event.registerEntityRenderer(PPRegistry.EntityReg.ORTHACANTHUS.get(), OrthacanthusRenderer::new);
            event.registerEntityRenderer(PPRegistry.EntityReg.EURHINOSAURUS.get(), EurhinosaurusRenderer::new);
            event.registerEntityRenderer(PPRegistry.EntityReg.SPINOSAURUS.get(), SpinosaurusRenderer::new);
        }

        @SubscribeEvent
        public static void onCommonSetup(final FMLCommonSetupEvent event) {
            // No longer needed - menu screens registered via event
        }

        @SubscribeEvent
        public static void onRegisterMenuScreens(final RegisterMenuScreensEvent event) {
            event.register(PPRegistry.MenuReg.ANALYZER.get(), AnalyzerScreen::new);
            event.register(PPRegistry.MenuReg.INFUSER.get(), InfuserScreen::new);
        }

        @SubscribeEvent
        public static void onRegisterItemColors(final RegisterColorHandlersEvent.Item event) {
            event.register((itemStack, tintIndex) -> {
                if(tintIndex == 1 && itemStack.getItem() instanceof VialItem vial) {
                    return vial.getColor();
                }
                return -1;
            }, PPRegistry.ItemReg.getVialItems().stream().map(DeferredHolder::get).toList().toArray(new Item[0]));
        }
    }
}
