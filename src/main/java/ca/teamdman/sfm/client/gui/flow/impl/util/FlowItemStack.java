package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f;
import ca.teamdman.sfm.client.gui.flow.core.IFlowController;
import ca.teamdman.sfm.client.gui.flow.core.IFlowTangible;
import ca.teamdman.sfm.client.gui.flow.core.IFlowView;
import ca.teamdman.sfm.client.gui.flow.core.ISelectable;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flowdata.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;

public class FlowItemStack implements IFlowView, IFlowController, IFlowTangible, ISelectable {

	public static final int ITEM_PADDING_X = 4;
	public static final int ITEM_PADDING_Y = 4;
	public static final int ITEM_WIDTH = 20;
	public static final int ITEM_HEIGHT = 20;
	public static final int ITEM_TOTAL_HEIGHT = ITEM_HEIGHT + ITEM_PADDING_Y;
	public static final int ITEM_TOTAL_WIDTH = ITEM_WIDTH + ITEM_PADDING_X;
	private final ItemStack STACK;
	private final FlowPanel HITBOX;
	private boolean selected;
	private boolean depressed = false;

	public FlowItemStack(ItemStack stack, Position pos) {
		this.STACK = stack;
		this.HITBOX = new FlowPanel(
			pos,
			new Size(ITEM_TOTAL_WIDTH, ITEM_TOTAL_HEIGHT)
		);
	}

	public ItemStack getItemStack() {
		return STACK;
	}


	@Override
	public IFlowView getView() {
		return this;
	}


	@Override
	public boolean mousePressed(int mx, int my, int button) {
		if (isInBounds(mx, my)) {
			depressed = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		boolean check = depressed;
		depressed = false;
		if (isInBounds(mx, my) && check) {
			toggleSelected(true);
			return true;
		}
		return false;
	}

	private void drawSquare(BaseScreen screen, MatrixStack matrixStack, Colour3f colour) {
		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			ITEM_TOTAL_WIDTH,
			ITEM_TOTAL_HEIGHT,
			colour
		);
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		if (selected) {
			drawSquare(screen, matrixStack, Colour3f.SELECTED);
		} else if (isInBounds(mx, my)) {
			drawSquare(screen, matrixStack, Colour3f.HIGHLIGHT);
		}
		screen.drawItemStack(
			matrixStack,
			STACK,
			getPosition().getX() + ITEM_PADDING_X / 2,
			getPosition().getY() + ITEM_PADDING_Y / 2
		);
//		Minecraft.getInstance().getBlockRendererDispatcher().renderBlock();
	}


	@Override
	public Position getPosition() {
		return HITBOX.getPosition();
	}

	@Override
	public Size getSize() {
		return HITBOX.getSize();
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean value, boolean notify) {
		this.selected = value;
	}
}
