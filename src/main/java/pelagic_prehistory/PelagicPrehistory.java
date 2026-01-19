package pelagic_prehistory;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import pelagic_prehistory.client.ClientEvents;

@Mod(PelagicPrehistory.MODID)
public class PelagicPrehistory {

    public static final String MODID = "pelagic_prehistory";

    public static final Logger LOGGER = LogUtils.getLogger();

    public PelagicPrehistory() {
        PPRegistry.register();
        PPEvents.register();
        // client events
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientEvents::register);
    }


}
