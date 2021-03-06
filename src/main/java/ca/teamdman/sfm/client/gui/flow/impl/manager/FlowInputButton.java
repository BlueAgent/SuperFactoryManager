package ca.teamdman.sfm.client.gui.flow.impl.manager;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.util.CableInventoryDrawerButton;
import ca.teamdman.sfm.common.flowdata.impl.FlowInputData;

public class FlowInputButton extends CableInventoryDrawerButton<FlowInputData> {

	public FlowInputButton(
		ManagerFlowController controller,
		FlowInputData data
	) {
		super(controller, data, ButtonLabel.INPUT);
	}
}
