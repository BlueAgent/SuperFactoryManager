package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.manager.ManagerScreen;
import ca.teamdman.sfm.common.flowdata.Position;
import ca.teamdman.sfm.common.flowdata.PositionProvider;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public class ManagerPositionPacketS2C extends S2CManagerPacket {

	private final Position ELEMENT_POSITION;
	private final UUID ELEMENT_ID;

	public ManagerPositionPacketS2C(int windowId, UUID elementId, Position elementPos) {
		super(windowId);
		this.ELEMENT_ID = elementId;
		this.ELEMENT_POSITION = elementPos;
	}

	public static class Handler extends S2CHandler<ManagerPositionPacketS2C> {

		@Override
		public void finishEncode(ManagerPositionPacketS2C msg, PacketBuffer buf) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
			buf.writeLong(msg.ELEMENT_POSITION.toLong());
		}

		@Override
		public ManagerPositionPacketS2C finishDecode(int windowId, PacketBuffer buf) {
			return new ManagerPositionPacketS2C(
				windowId,
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void handleDetailed(ManagerScreen screen,
			ManagerPositionPacketS2C msg) {
			screen.getData(msg.ELEMENT_ID)
				.filter(data -> data instanceof PositionProvider)
				.map(data -> ((PositionProvider) data).getPosition())
				.ifPresent(pos -> pos.setXY(msg.ELEMENT_POSITION));
			screen.CONTROLLER.loadFromScreenData();
		}
	}
}
