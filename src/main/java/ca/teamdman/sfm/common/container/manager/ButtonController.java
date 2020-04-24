package ca.teamdman.sfm.common.container.manager;

import ca.teamdman.sfm.client.gui.ManagerScreen;
import ca.teamdman.sfm.common.container.ManagerContainer;

import java.util.ArrayList;


public class ButtonController {
	private final ArrayList<Button> BUTTON_LIST = new ArrayList<>();
	private final ManagerContainer     CONTAINER;
	private       Button            active      = null;

	public ButtonController(ManagerContainer container) {
		this.CONTAINER = container;
	}

	public Button addButton(Button b) {
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
		return true;
	}

}
