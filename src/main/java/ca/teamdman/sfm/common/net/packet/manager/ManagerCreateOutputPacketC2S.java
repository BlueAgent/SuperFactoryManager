package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.flowdata.core.FlowData;
import ca.teamdman.sfm.common.flowdata.core.Position;
import ca.teamdman.sfm.common.flowdata.impl.FlowOutputData;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerCreateOutputPacketC2S extends C2SManagerPacket {

	private final Position ELEMENT_POSITION;

	public ManagerCreateOutputPacketC2S(int windowId, BlockPos pos, Position elementPosition) {
		super(windowId, pos);
		this.ELEMENT_POSITION = elementPosition;
	}

	public static class Handler extends C2SHandler<ManagerCreateOutputPacketC2S> {

		@Override
		public void finishEncode(
			ManagerCreateOutputPacketC2S msg,
			PacketBuffer buf
		) {
			buf.writeLong(msg.ELEMENT_POSITION.toLong());
		}

		@Override
		public ManagerCreateOutputPacketC2S finishDecode(
			int windowId, BlockPos tilePos,
			PacketBuffer buf
		) {
			return new ManagerCreateOutputPacketC2S(
				windowId,
				tilePos,
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void handleDetailed(ManagerCreateOutputPacketC2S msg, ManagerTileEntity manager) {
			FlowData data = new FlowOutputData(
				UUID.randomUUID(),
				msg.ELEMENT_POSITION
			);

			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"C2S received, creating output at position {} with id {}",
				msg.ELEMENT_POSITION,
				data.getId()
			);

			manager.addData(data);
			manager.markAndNotify();
			manager.sendPacketToListeners(new ManagerCreateOutputPacketS2C(
				msg.WINDOW_ID,
				data.getId(),
				msg.ELEMENT_POSITION
			));
		}
	}
}
