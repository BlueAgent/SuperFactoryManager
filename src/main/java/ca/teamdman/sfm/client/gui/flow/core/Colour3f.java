package ca.teamdman.sfm.client.gui.flow.core;

public class Colour3f {

	public static final Colour3f WHITE = new Colour3f(1,1,1);
	public static final Colour3f HIGHLIGHT = new Colour3f(0.4f, 0.4f, 0.8f);
	public static final Colour3f SELECTED = new Colour3f(0.4f, 0.8f, 0.4f);
	public static final Colour3f PANEL_BACKGROUND = new Colour3f(0.4f,0.4f,0.4f);
	public final float RED, GREEN, BLUE;

	public Colour3f(float RED, float GREEN, float BLUE) {
		this.RED = RED;
		this.GREEN = GREEN;
		this.BLUE = BLUE;
	}
}
