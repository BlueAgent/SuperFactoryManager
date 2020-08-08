package ca.teamdman.sfm.client.gui.core;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;


/**
 * Credit to VSWE for lots of the rendering scaling tech
 */
public abstract class BaseScreen extends Screen {

	public static final Colour3f DEFAULT_LINE_COLOUR = new Colour3f(0.4f, 0.4f, 0.4f);
	public static final Colour3f HIGHLIGHTED_LINE_COLOUR = new Colour3f(0.15686275f, 0.5294118f,
		0.94509804f);
	final int zLevel = 0;
	protected int guiLeft;
	protected int guiTop;
	protected int scaledWidth;
	protected int scaledHeight;

	public BaseScreen(ITextComponent titleIn, int scaledWidth, int scaledHeight) {
		super(titleIn);
		this.scaledWidth = scaledWidth;
		this.scaledHeight = scaledHeight;
	}

	/**
	 * Binds a texture to be drawn
	 *
	 * @param resource Texture location
	 */
	public static void bindTexture(ResourceLocation resource) {
		Minecraft.getInstance().getTextureManager().bindTexture(resource);
	}

	public ItemRenderer getItemRenderer() {
		return this.itemRenderer;
	}

	/**
	 * Draws a string to the screen
	 */
	public void drawString(MatrixStack matrixStack, String str, int x,
		int y, float mult, int color) {
		RenderSystem.pushMatrix();
		RenderSystem.scalef(mult, mult, 1F);
		this.font
//			.drawString(matrixStack, str, (int) ((x + guiLeft) / mult), (int) ((y + guiTop) / mult), color);
			.drawString(matrixStack, str, (int) ((x) / mult), (int) ((y) / mult), color);
		//bindTexture(getComponentResource());
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.popMatrix();
	}

	public void drawRightAlignedString(MatrixStack matrixStack,
		String str, int x, int y, int color) {
		drawRightAlignedString(matrixStack, str, x, y, 1, color);
	}

	/**
	 * Draws a string, aligned to the right of the screen
	 */
	public void drawRightAlignedString(MatrixStack matrixStack, String str, int x, int y,
		float mult, int color) {
		drawString(
			matrixStack, str,
			(int) (x - fixScaledCoordinate(font.getStringWidth(str), getScale(),
				Minecraft.getInstance().getMainWindow().getWidth())),
			y,
			mult,
			color
		);
	}

	/**
	 * Converts local values to screen values.
	 *
	 * @param val   Local value
	 * @param scale Scale factor
	 * @param size  Screen dimension
	 * @return Screen value
	 */
	public double fixScaledCoordinate(int val, double scale, int size) {
		double d = val / scale;
		d *= size;
		d = Math.floor(d);
		d /= size;
		d *= scale;

		return d;
	}

	/**
	 * Gets the ratio from screen to local.
	 *
	 * @return Scaling factor
	 */
	public double getScale() {
		double xFactor = (width * 0.9F) / this.scaledWidth;
		double yFactor = (height * 0.9F) / this.scaledHeight;
		double mult = Math.min(xFactor, yFactor);
		mult = Math.min(1, mult);
		mult = Math.floor(mult * 1000) / 1000F;
		return mult;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	/**
	 * Draws the bound texture to the screen
	 *
	 * @param matrixStack
	 * @param x           Draw begin x scaled coordinate
	 * @param y           Draw begin y scaled coordinate
	 * @param left        Texture begin x offset
	 * @param top         Texture begin y offset
	 * @param width       Texture sample width
	 * @param height      Texture sample height
	 */
	public void drawSprite(MatrixStack matrixStack, int x, int y,
		int left, int top, int width, int height) {
		drawTexture(matrixStack, x, y, left, top, width, height);
	}

	/**
	 * Scales and draws the currently bound texture.
	 *
	 * @param matrixStack
	 * @param x           Local value
	 * @param y           Local value
	 * @param srcX        Sprite value
	 * @param srcY        Sprite value
	 * @param w           Local width
	 * @param h           Local height
	 */
	public void drawTexture(MatrixStack matrixStack, int x, int y,
		int srcX, int srcY, int w, int h) {
		blit(matrixStack,
			x,
			y,
			srcX, srcY,
			w, h
		);
	}

	/**
	 * Renders the GUI, creating a scaled matrix for sub-renderers.
	 */
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack); // MC method, draw greyed out background
		startScaling(matrixStack);
		draw(matrixStack, scaleX(mouseX) - guiLeft, scaleY(mouseY) - guiTop, partialTicks);
		stopScaling(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}

	/**
	 * Initializes content, centers GUI on screen
	 */
	@Override
	protected void init() {
		super.init();
		this.guiLeft = (this.width - this.scaledWidth) / 2;
		this.guiTop = (this.height - this.scaledHeight) / 2;
	}

	/**
	 * Converts a screen X value to a local one.
	 *
	 * @param x Screen value
	 * @return Local value
	 */
	public int scaleX(double x) {
		double scale = getScale();
		x /= scale;
		x += guiLeft;
		x -= (this.width - this.scaledWidth * scale) / (2 * scale);
		return (int) x;
	}

	/**
	 * Converts a screen X value to a local one.
	 *
	 * @param y Screen value
	 * @return Local value
	 */
	public int scaleY(double y) {
		double scale = getScale();
		y /= scale;
		y += guiTop;
		y -= (this.height - this.scaledHeight * scale) / (2 * scale);
		return (int) y;
	}

	/**
	 * Sets GL state to fit the current scale ratio.
	 *
	 * @param matrixStack
	 */
	private void startScaling(MatrixStack matrixStack) {
		float scale = (float) getScale();
		matrixStack.push();
		matrixStack.translate(this.width / 2F, this.height / 2F, 0.0F);
		matrixStack.scale(scale, scale, 1);
//		matrixStack.translate(-guiLeft, -guiTop, 0.0F);
		matrixStack.translate(-this.scaledWidth / 2F, -this.scaledHeight / 2F, 0.0F);
	}

	/**
	 * Reverts GL state to normal scaling.
	 *
	 * @param matrixStack
	 */
	private void stopScaling(MatrixStack matrixStack) {
		matrixStack.pop();
	}

	public abstract void draw(MatrixStack matrixStack, int mouseX,
		int mouseY, float partialTicks);

	public void drawLine(MatrixStack matrixStack, int x1, int y1, int x2, int y2, Colour3f color) {
		// normal vector
		int dx = x2 - x1;
		int dy = y2 - y1;

		// scale vector to normal, the to width
		int sqrMag = dx * dx + dy * dy;
		double mag = Math.sqrt(sqrMag == 0 ? 1 : sqrMag);
		int width = 10;
		dx = (int) ((dx / mag) * width / 2f);
		dy = (int) ((dy / mag) * width / 2f);

		Matrix4f m = matrixStack.getLast().getMatrix();
		matrixStack.push();
		RenderSystem.disableTexture();
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		bufferBuilder.pos(m, x1 - dy, y1 + dx, 0)
			.color(color.RED, color.GREEN, color.BLUE, 1)
			.endVertex();
		bufferBuilder.pos(m, x2 - dy, y2 + dx, 0)
			.color(color.RED, color.GREEN, color.BLUE, 1)
			.endVertex();
		bufferBuilder.pos(m, x2 + dy, y2 - dx, 0)
			.color(color.RED, color.GREEN, color.BLUE, 1)
			.endVertex();
		bufferBuilder.pos(m, x1 + dy, y1 - dx, 0)
			.color(color.RED, color.GREEN, color.BLUE, 1)
			.endVertex();
		bufferBuilder.finishDrawing();
		WorldVertexBufferUploader.draw(bufferBuilder);
		RenderSystem.enableTexture();
//		RenderSystem.disableBlend();
		matrixStack.pop();
	}

	public void drawArrow(MatrixStack matrixStack, int x1, int y1, int x2,
		int y2, Colour3f color) {
		drawLine(matrixStack, x1, y1, x2, y2, color);
		int lookX = x2 - x1;
		int lookY = y2 - y1;
		double mag = Math.sqrt((lookX * lookX) + (lookY * lookY));
		mag *= 1 / 24d;
		lookX /= mag;
		lookY /= mag;

		double ang = Math.PI * -7 / 8d;
		drawLine(
			matrixStack, x2,
			y2,
			x2 + (int) (Math.cos(ang) * lookX - Math.sin(ang) * lookY),
			y2 + (int) (Math.sin(ang) * lookX + Math.cos(ang) * lookY),
			color
		);

		ang = Math.PI * 7 / 8d;
		drawLine(
			matrixStack, x2,
			y2,
			x2 + (int) (Math.cos(ang) * lookX - Math.sin(ang) * lookY),
			y2 + (int) (Math.sin(ang) * lookX + Math.cos(ang) * lookY),
			color
		);
	}
}
