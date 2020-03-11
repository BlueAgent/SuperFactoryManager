package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.client.gui.ManagerScreen;

import java.util.ArrayList;

import static ca.teamdman.sfm.SFM.LOGGER;

public class ButtonController {
	private final ArrayList<Button> BUTTON_LIST = new ArrayList<>();
	@SuppressWarnings("FieldCanBeLocal")
	private final ManagerScreen     GUI;
	private       Button            active      = null;

	public ButtonController(ManagerScreen gui) {
		this.GUI = gui;
	}

	public Button addButton(Button b) {
		LOGGER.debug("Button controller added button " + b);
		BUTTON_LIST.add(b);
		return b;
	}

	public boolean onMouseDown(int x, int y, int button, Component comp) {
		if (button != ManagerScreen.LEFT)
			return false;
		if (!(comp instanceof Button))
			return false;
		active = (Button) comp;
		active.setPressed(true);
		LOGGER.debug("Button controller activated button. Mouse down terminated.");
		return true;
	}

	public boolean onDrag(int x, int y, int button) {
		if (button != ManagerScreen.LEFT)
			return false;
		if (active == null)
			return false;
		if (active.isInBounds(x, y) == active.isPressed())
			return false;
		active.setPressed(!active.isPressed());
		LOGGER.debug("Button controller dragged button. Mouse drag terminated.");
		return true;
	}

	public boolean onMouseUp(int x, int y, int button) {
		if (active == null)
			return false;
		if (!active.isPressed())
			return false;
		if (!active.isInBounds(x, y))
			return false;
		active.click();
		active.setPressed(false);
		active = null;
		LOGGER.debug("Button clicked button. Mouse up terminated.");
		return true;
	}

}
