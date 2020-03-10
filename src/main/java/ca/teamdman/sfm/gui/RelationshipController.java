package ca.teamdman.sfm.gui;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

import static ca.teamdman.sfm.SFM.LOGGER;
import static ca.teamdman.sfm.gui.BaseGui.DEFAULT_LINE_COLOUR;
import static ca.teamdman.sfm.gui.BaseGui.HIGHLIGHTED_LINE_COLOUR;
import static ca.teamdman.sfm.gui.ManagerGui.LEFT;
import static net.minecraft.client.gui.screen.Screen.hasAltDown;
import static net.minecraft.client.gui.screen.Screen.hasShiftDown;

public class RelationshipController {
	private final ManagerGui                                           GUI;
	private final HashMap<Component, HashMap<Component, Relationship>> HIERARCHY         = new HashMap<>();
	private final ArrayList<Relationship>                              RELATIONSHIP_LIST = new ArrayList<>();
	private       Pair<Relationship, Pair<Line, Double>>               dragging          = null;
	private       Component                                            start             = null;

	public RelationshipController(ManagerGui GUI) {
		this.GUI = GUI;
	}


	// Return false to pass through
	public boolean onMouseDown(int x, int y, int button, Component comp) {
		if (button != LEFT)
			return false;
		if (comp == null) {
			dragging = null;
			getRelationship(x, y).ifPresent(r -> {
				if (r.getValue().getValue() > 5) // distance too far
					return;
				dragging = r;
				r.getValue().getKey().setColor(HIGHLIGHTED_LINE_COLOUR);
			});
			if (dragging != null) {
				LOGGER.debug("Relationship controller began line dragging. Mouse down terminated.");
				return true;
			}
			return false;
		} else if (hasShiftDown()) {
			start = comp;
			LOGGER.debug("Relationship controller began linking. Mouse down terminated.");
			return true;
		}
		return false;
	}

	public Optional<Pair<Relationship, Pair<Line, Double>>> getRelationship(int x, int y) {
		return RELATIONSHIP_LIST.stream()
				.map(r -> new Pair<>(r, r.getNearestLineDistance(x, y)))
				.min(Comparator.comparingDouble(p -> p.getValue().getValue()));
	}

	public boolean onDrag(int x, int y, int button) {
		if (!hasShiftDown() && start != null) {
			start = null;
			return false;
		}

		if (hasAltDown() && dragging != null) {
			dragging.getValue().getKey().drag(x, y);
			LOGGER.debug("Relationship controller dragged component. Mouse drag terminated.");
			return true;
		} else if (!hasAltDown() && dragging != null) {
			dragging = null;
		}
		return false;
	}

	public boolean onMouseUp(int x, int y, int button) {
		if (dragging != null) {
			dragging.getValue().getKey().setColor(DEFAULT_LINE_COLOUR);
			dragging = null;
		}

		if (start == null)
			return false;
		if (!hasShiftDown())
			return false;
		for (Command c : GUI.COMMAND_CONTROLLER.getCommands()) {
			if (c != start && c.isInBounds(x, y)) {
				addRelationship(new Relationship(start, c));
				start = null;
				LOGGER.debug("Relationship controller linked components. Mouse up terminated.");
				return true;
			}
		}
		return false;
	}

	public void addRelationship(Relationship r) {
		if (RELATIONSHIP_LIST.contains(r))
			return;
		if (RELATIONSHIP_LIST.contains(r.inverse()))
			return;
		HIERARCHY.computeIfAbsent(r.HEAD, (__) -> new HashMap<>()).put(r.TAIL, r);
		HIERARCHY.computeIfAbsent(r.TAIL, (__) -> new HashMap<>()).put(r.HEAD, r);
		RELATIONSHIP_LIST.add(r);
	}

	public Optional<Relationship> getRelationship(Component a, Component b) {
		if (HIERARCHY.containsKey(a))
			if (HIERARCHY.get(a).containsKey(b))
				return Optional.of(HIERARCHY.get(a).get(b));
		if (HIERARCHY.containsKey(b))
			if (HIERARCHY.get(b).containsKey(a))
				return Optional.of(HIERARCHY.get(b).get(a));
		return Optional.empty();
	}

	public void draw(int x, int y) {
		RELATIONSHIP_LIST.forEach(this::drawRelationship);
		if (start != null)
			if (hasShiftDown())
				GUI.drawArrow(start.getPosition().getX() + start.width / 2, start.getPosition().getY() + start.height / 2, x, y);
			else
				start = null;
	}

	public void drawRelationship(Relationship r) {
		for (Line line : r.LINE_LIST) {
			if (line.getNext() == r.TAIL) {
				GUI.drawArrow(line);
			} else {
				GUI.drawLine(line);
			}
		}
	}

	public void reflow(Component c) {
		RELATIONSHIP_LIST.stream()
				.filter(r -> r.HEAD == c || r.TAIL == c)
				.forEach(r -> {
					if (r.HEAD == c) {
						r.getFirst().ifPresent(line -> {
							line.HEAD.setXY(c.snapToEdge(line.TAIL));
							line.reflow(Line.Direction.FORWARDS);
						});
					} else {
						r.getLast().ifPresent(line -> {
							line.TAIL.setXY(c.snapToEdge(line.HEAD));
							line.reflow(Line.Direction.BACKWARDS);
						});
					}
				});
	}

}
