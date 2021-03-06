package ca.teamdman.sfm.client.gui.flow.core;

import ca.teamdman.sfm.common.flowdata.core.FlowData;
import java.util.Optional;

public interface IFlowController {
	/**
	 * Fired when a mouse button is pressed
	 *
	 * @param mx     Scaled mouse x coordinate
	 * @param my     Scaled mouse y coordinate
	 * @param button Mouse button pressed
	 * @return Consume event
	 */
	default boolean mousePressed(int mx, int my, int button) { return false; }

	/**
	 * Fired when a mouse button is released
	 *
	 * @param mx     Scaled mouse x coordinate
	 * @param my     Scaled mouse y coordinate
	 * @param button Mouse button pressed
	 * @return Consume event
	 */
	default boolean mouseReleased(int mx, int my, int button) { return false; }

	/**
	 * Fired when a mouse is dragged from one position to another
	 *
	 * @param mx     Drag begin scaled mouse x coordinate
	 * @param my     Drag begin scaled mouse y coordinate
	 * @param button Mouse button held during drag
	 * @param dmx    Drag ended scaled mouse x coordinate
	 * @param dmy    Drag ended scaled mouse y coordiante
	 * @return Consume event
	 */
	default boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) { return false; }

	/**
	 * Gets the view used to draw this controller
	 *
	 * @return view
	 */
	IFlowView getView();

	/**
	 * If this controller is responsible for representing a single FlowData,
	 * then this method returns that data
	 * @return FlowData
	 */
	default Optional<FlowData> getData() {
		return Optional.empty();
	}


	/**
	 * Key press handler
	 * @return consume event
	 */
	default boolean keyPressed(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return false;
	}

	/**
	 * Key press handler
	 * @return consume event
	 */
	default boolean keyReleased(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return false;
	}

	/**
	 * Mouse scroll handler
	 * @param mx Scaled mouse X coordinate
	 * @param my Scaled mouse Y coordinate
	 * @param scroll Scroll amount
	 * @return consume event
	 */
	default boolean mouseScrolled(int mx, int my, double scroll) {
		return false;
	}

	/**
	 * Called once the container has set all the values needed to set the controller defaults.
	 * Expected behaviour: overwrite values in controller with values from container.
	 */
	default void onDataChange(){};
}
