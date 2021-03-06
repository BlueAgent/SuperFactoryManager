package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.net.packet.IContainerTilePacket;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public abstract class C2SManagerPacket implements IContainerTilePacket {

	final BlockPos TILE_POSITION;
	final int WINDOW_ID;

	public C2SManagerPacket(int WINDOW_ID, BlockPos TILE_POSITION) {
		this.WINDOW_ID = WINDOW_ID;
		this.TILE_POSITION = TILE_POSITION;
	}

	@Override
	public BlockPos getTilePosition() {
		return TILE_POSITION;
	}

	@Override
	public int getWindowId() {
		return WINDOW_ID;
	}

	public abstract static class C2SHandler<MSG extends C2SManagerPacket> {

		public void encode(MSG msg, PacketBuffer buf) {
			buf.writeInt(msg.WINDOW_ID);
			buf.writeBlockPos(msg.TILE_POSITION);
			finishEncode(msg, buf);
		}

		public abstract void finishEncode(MSG msg, PacketBuffer buf);

		public MSG decode(PacketBuffer buf) {
			return finishDecode(
				buf.readInt(),
				buf.readBlockPos(),
				buf
			);
		}

		public abstract MSG finishDecode(int windowId, BlockPos tilePos, PacketBuffer buf);

		public void handle(MSG msg, Supplier<Context> ctx) {
			ctx.get().enqueueWork(() ->
				SFMUtil.getTileFromContainerPacket(
					msg,
					ctx,
					ManagerContainer.class,
					ManagerTileEntity.class
				).ifPresent(manager -> handleDetailed(msg, manager)));
			ctx.get().setPacketHandled(true);
		}

		public abstract void handleDetailed(MSG msg, ManagerTileEntity manager);
	}
}
