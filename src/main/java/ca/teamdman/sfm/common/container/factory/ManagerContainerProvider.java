package ca.teamdman.sfm.common.container.factory;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.container.CrafterContainer;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.tile.CrafterTileEntity;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class ManagerContainerProvider implements INamedContainerProvider {
	private final IWorldPosCallable ACCESS;

	public ManagerContainerProvider(IWorldPosCallable access) {
		this.ACCESS = access;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("container.sfm.manager");
	}

	@Nullable
	@Override
	public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
		return SFMUtil.getServerTile(ACCESS, ManagerTileEntity.class)
				.map(tile -> new ManagerContainer(windowId, playerInv, tile)).orElse(null);
	}

	public void openGui(PlayerEntity player) {
		if (player instanceof ServerPlayerEntity)
			SFMUtil.getServerTile(ACCESS, ManagerTileEntity.class)
					.ifPresent(tile -> NetworkHooks.openGui((ServerPlayerEntity) player, this, data -> {
						data.writeBlockPos(tile.getPos());
					}));
	}

	public static class ManagerContainerFactory implements IContainerFactory<ManagerContainer> {
		@Override
		public ManagerContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
			return SFMUtil.getClientTile(IWorldPosCallable.of(inv.player.world, data.readBlockPos()), ManagerTileEntity.class)
					.map(tile -> new ManagerContainer(windowId, inv, tile)).orElse(null);
		}
	}
}
