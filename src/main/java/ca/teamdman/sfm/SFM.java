package ca.teamdman.sfm;


import ca.teamdman.sfm.client.ClientProxy;
import ca.teamdman.sfm.common.CommonProxy;
import ca.teamdman.sfm.common.config.ConfigHolder;
import ca.teamdman.sfm.common.net.PacketHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(SFM.MOD_ID)
public class SFM {
	public static final Logger LOGGER   = LogManager.getLogger();
	public static final String MOD_ID   = "sfm";
	public static final String MOD_NAME = "Super Factory Manager";
	public static final CommonProxy PROXY = DistExecutor.runForDist(()-> ClientProxy::new, ()->CommonProxy::new);

	public SFM() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);

		bus.addListener(this::onSetup);
	}

	private void onSetup(final FMLCommonSetupEvent e) {
		PacketHandler.setup();
	}
}
