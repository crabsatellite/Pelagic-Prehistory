package pelagic_prehistory.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import pelagic_prehistory.PPRegistry;
import pelagic_prehistory.client.entity.BawitiusRenderer;
import pelagic_prehistory.client.entity.CladoselacheRenderer;
import pelagic_prehistory.client.entity.CuttlefishRenderer;
import pelagic_prehistory.client.entity.DugongRenderer;
import pelagic_prehistory.client.entity.DunkleosteusRenderer;
import pelagic_prehistory.client.entity.HenodusRenderer;
import pelagic_prehistory.client.entity.IrritatorRenderer;
import pelagic_prehistory.client.entity.LepidotesRenderer;
import pelagic_prehistory.client.entity.PlesiosaurusRenderer;
import pelagic_prehistory.client.entity.PliosaurusRenderer;
import pelagic_prehistory.client.entity.PrognathodonRenderer;
import pelagic_prehistory.client.entity.ShonisaurusRenderer;
import pelagic_prehistory.client.entity.OrthacanthusRenderer;
import pelagic_prehistory.client.entity.EurhinosaurusRenderer;
import pelagic_prehistory.client.entity.SpinosaurusRenderer;
import pelagic_prehistory.client.menu.AnalyzerScreen;
import pelagic_prehistory.client.menu.InfuserScreen;
import pelagic_prehistory.item.VialItem;

public final class ClientEvents {

    public static void register() {
        FMLJavaModLoadingContext.get().getModEventBus().register(ModHandler.class);
        MinecraftForge.EVENT_BUS.register(ForgeHandler.class);
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
            event.enqueueWork(ModHandler::onRegisterScreens);
        }

        @SubscribeEvent
        public static void onRegisterItemColors(final RegisterColorHandlersEvent.Item event) {
            event.register((itemStack, tintIndex) -> {
                if(tintIndex == 1 && itemStack.getItem() instanceof VialItem vial) {
                    return vial.getColor();
                }
                return -1;
            }, PPRegistry.ItemReg.getVialItems().stream().map(RegistryObject::get).toList().toArray(new Item[0]));
        }

        private static void onRegisterScreens() {
            MenuScreens.register(PPRegistry.MenuReg.ANALYZER.get(), AnalyzerScreen::new);
            MenuScreens.register(PPRegistry.MenuReg.INFUSER.get(), InfuserScreen::new);
        }
    }

    public static final class ForgeHandler {

    }
}
