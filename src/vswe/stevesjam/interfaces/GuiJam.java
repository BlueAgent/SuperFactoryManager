package vswe.stevesjam.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import vswe.stevesjam.StevesJam;
import vswe.stevesjam.blocks.TileEntityJam;
import vswe.stevesjam.components.FlowComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class GuiJam extends GuiContainer {
    public GuiJam(TileEntityJam jam, InventoryPlayer player) {
        super(new ContainerJam(jam, player));

        xSize = 512;
        ySize = 256;

        this.jam = jam;
    }

    private static final ResourceLocation BACKGROUND_1 = registerTexture("Background1");
    private static final ResourceLocation BACKGROUND_2 = registerTexture("Background2");
    private static final ResourceLocation COMPONENTS = registerTexture("FlowComponents");

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        bindTexture(BACKGROUND_1);
        drawTexture(0, 0, 0, 0, 256, 256);

        bindTexture(BACKGROUND_2);
        drawTexture(256, 0, 0, 0, 256, 256);

        x -= guiLeft;
        y -= guiTop;

        bindTexture(COMPONENTS);
        for (FlowComponent itemBase : jam.getFlowItems()) {
            itemBase.draw(this, x, y);
        }
        for (FlowComponent itemBase : jam.getFlowItems()) {
            itemBase.drawMouseOver(this, x, y);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);

        x -= guiLeft;
        y -= guiTop;

        for (FlowComponent itemBase : jam.getFlowItems()) {
            itemBase.onClick(x, y);
        }
    }

    @Override
    protected void mouseClickMove(int x, int y, int button, long ticks) {
        x -= guiLeft;
        y -= guiTop;

        for (FlowComponent itemBase : jam.getFlowItems()) {
            itemBase.onDrag(x, y);
        }
    }

    @Override
    protected void mouseMovedOrUp(int x, int y, int button) {
        x -= guiLeft;
        y -= guiTop;

        for (FlowComponent itemBase : jam.getFlowItems()) {
            itemBase.onRelease(x, y);
        }
    }


    public void drawTexture(int x, int y, int srcX, int srcY, int w, int h) {
        drawTexturedModalRect(guiLeft + x, guiTop + y, srcX, srcY, w, h);
    }

    public static void bindTexture(ResourceLocation resource)  {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resource);
    }

    public static ResourceLocation registerTexture(String name) {
        return new ResourceLocation(StevesJam.RESOURCE_LOCATION, "textures/gui/" +  name + ".png");
    }

    private TileEntityJam jam;

    public TileEntityJam getJam() {
        return jam;
    }

    public void drawString(String str, int x, int y, float mult, int color) {
        GL11.glPushMatrix();
        GL11.glScalef(mult, mult, 1F);
        fontRenderer.drawString(str, (int)((x + guiLeft) / mult), (int)((y + guiTop) / mult), color);
        bindTexture(COMPONENTS);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glPopMatrix();
    }

    public void drawString(String str, int x, int y, int color) {
        drawString(str, x, y, 1F, color);
    }

    public void drawMouseOver(String str, int x, int y) {
        drawHoveringText(Arrays.asList(str.split("\n")), x + guiLeft, y + guiTop, fontRenderer);
    }

    public void drawItemStack(ItemStack itemstack, int x, int y) {
        itemRenderer.renderItemAndEffectIntoGUI(this.fontRenderer, this.mc.getTextureManager(), itemstack, x + guiLeft, y + guiTop);
        bindTexture(COMPONENTS);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    public static boolean inBounds(int leftX, int topY, int width, int height, int mX, int mY) {
        return leftX <= mX && mX <= leftX + width && topY <= mY && mY <= topY + height;
    }

    public int getStringWidth(String str) {
        return fontRenderer.getStringWidth(str);
    }

    @Override
    protected void keyTyped(char c, int k) {
        for (FlowComponent itemBase : jam.getFlowItems()) {
            if (itemBase.onKeyStroke(this, c, k) && k != 1) {
                return;
            }
        }

        super.keyTyped(c, k);
    }

    public void drawCenteredString(String str, int x, int y, float mult, int width, int color) {
        drawString(str, x + (width - (int)(getStringWidth(str) * mult)) / 2, y, mult, color);
    }

    public void drawCursor(int x, int y, int z, int color) {
        GL11.glPushMatrix();
        GL11.glTranslatef(0, 0, z);
        x += guiLeft;
        y += guiTop;
        Gui.drawRect(x , y + 1 , x + 1, y + 10, color);
        GL11.glPopMatrix();
    }
}
