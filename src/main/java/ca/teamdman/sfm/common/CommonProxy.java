package ca.teamdman.sfm.common;

import ca.teamdman.sfm.common.net.packet.IWindowIdProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class CommonProxy {
	public <T extends Screen> Optional<T> getScreenFromPacket(IWindowIdProvider packet, Supplier<NetworkEvent.Context> ctx, Class<T> screenClass) {
		return Optional.empty();
	}

	public void fillItemGroup(ItemGroup group, Item[] items) {
	}
}
