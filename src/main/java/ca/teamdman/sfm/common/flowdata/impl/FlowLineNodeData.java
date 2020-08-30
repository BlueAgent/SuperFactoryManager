package ca.teamdman.sfm.common.flowdata.impl;

import ca.teamdman.sfm.common.flowdata.core.FlowData;
import ca.teamdman.sfm.common.flowdata.core.FlowDataFactory;
import ca.teamdman.sfm.common.flowdata.core.Position;
import ca.teamdman.sfm.common.flowdata.core.PositionProvider;
import ca.teamdman.sfm.common.registrar.FlowDataFactoryRegistrar.FlowDataFactories;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class FlowLineNodeData extends FlowData implements PositionProvider {

	public Position position;

	public FlowLineNodeData(UUID uuid, Position position) {
		super(uuid);
		this.position = position;
	}

	public FlowLineNodeData(CompoundNBT tag) {
		deserializeNBT(tag);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = super.serializeNBT();
		tag.put("pos", position.serializeNBT());
		FlowDataFactories.LINE_NODE.stampNBT(tag);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundNBT tag) {
		super.deserializeNBT(tag);
		this.position.deserializeNBT(tag.getCompound("pos"));
	}

	@Override
	public FlowData copy() {
		return new FlowLineNodeData(getId(), getPosition());
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public static class LineNodeFlowDataFactory extends FlowDataFactory<FlowLineNodeData> {

		public LineNodeFlowDataFactory(ResourceLocation key) {
			super(key);
		}

		@Override
		public FlowLineNodeData fromNBT(CompoundNBT tag) {
			return new FlowLineNodeData(tag);
		}
	}
}
