package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.core.Position;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.flowdata.FlowData;
import ca.teamdman.sfm.common.flowdata.IHasPosition;
import ca.teamdman.sfm.common.net.packet.IContainerTilePacket;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkEvent;

public class PositionPacketC2S implements IContainerTilePacket {

	private final BlockPos TILE_POSITION;
	private final int WINDOW_ID, X, Y;
	private final UUID ELEMENT_ID;

	public PositionPacketC2S(int windowId, BlockPos pos, UUID elementId, int x, int y) {
		this.WINDOW_ID = windowId;
		this.TILE_POSITION = pos;
		this.ELEMENT_ID = elementId;
		this.X = x;
		this.Y = y;
	}

	public static void encode(PositionPacketC2S msg, PacketBuffer buf) {
		buf.writeInt(msg.WINDOW_ID);
		buf.writeBlockPos(msg.TILE_POSITION);
		buf.writeString(msg.ELEMENT_ID.toString());
		buf.writeInt(msg.X);
		buf.writeInt(msg.Y);
	}

	public static void handle(PositionPacketC2S msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			SFMUtil.getTileFromContainerPacket(msg, ctx, ManagerContainer.class,
				ManagerTileEntity.class).ifPresent(manager -> {
				BlockState state = manager.getWorld().getBlockState(msg.TILE_POSITION);
				{
					manager.data.stream()
						.filter(x -> x instanceof IHasPosition)
						.filter(x -> x.getId().equals(msg.ELEMENT_ID))
						.forEach(x -> ((IHasPosition) x).getPosition().setXY(msg.X, msg.Y));
				}
				manager.markDirty();
				manager.getWorld().notifyBlockUpdate(msg.TILE_POSITION, state, state,
					Constants.BlockFlags.BLOCK_UPDATE & Constants.BlockFlags.NOTIFY_NEIGHBORS);
			});
		});
		ctx.get().setPacketHandled(true);
	}


	public static PositionPacketC2S decode(PacketBuffer packetBuffer) {
		int windowId = packetBuffer.readInt();
		BlockPos pos = packetBuffer.readBlockPos();
		UUID elementId = UUID.fromString(packetBuffer.readString());
		int x = packetBuffer.readInt();
		int y = packetBuffer.readInt();
		return new PositionPacketC2S(windowId, pos, elementId, x, y);
	}

	@Override
	public int getWindowId() {
		return WINDOW_ID;
	}

	@Override
	public BlockPos getTilePosition() {
		return TILE_POSITION;
	}
}
