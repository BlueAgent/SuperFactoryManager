package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.net.packet.manager.ButtonPositionPacketC2S;
import ca.teamdman.sfm.common.net.packet.NumberUpdatePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ButtonPositionPacketS2C;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
	private static final String        CHANNEL_NAME     = "main";
	private static final String        PROTOCOL_VERSION = "1";
	public static final  SimpleChannel INSTANCE         = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(SFM.MOD_ID, CHANNEL_NAME),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);


	@SuppressWarnings("UnusedAssignment")
	public static void setup() {
		int i = 0;
		INSTANCE.registerMessage(i++,
				ButtonPositionPacketC2S.class,
				ButtonPositionPacketC2S::encode,
				ButtonPositionPacketC2S::decode,
				ButtonPositionPacketC2S::handle);

		INSTANCE.registerMessage(i++,
				NumberUpdatePacketC2S.class,
				NumberUpdatePacketC2S::encode,
				NumberUpdatePacketC2S::decode,
				NumberUpdatePacketC2S::handle);


		INSTANCE.registerMessage(i++,
				ButtonPositionPacketS2C.class,
				ButtonPositionPacketS2C::encode,
				ButtonPositionPacketS2C::decode,
				ButtonPositionPacketS2C::handle);
	}
}
