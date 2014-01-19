package vswe.stevesfactory.interfaces;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import vswe.stevesfactory.CollisionHelper;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.blocks.TileEntityManager;
import vswe.stevesfactory.components.FlowComponent;
import vswe.stevesfactory.network.DataBitHelper;
import vswe.stevesfactory.network.DataWriter;
import vswe.stevesfactory.network.PacketHandler;

import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiManager extends GuiBase {

    public GuiManager(TileEntityManager manager, InventoryPlayer player) {
        super(new ContainerManager(manager, player));

        xSize = 512;
        ySize = 256;

        this.manager = manager;
    }

    private static final ResourceLocation BACKGROUND_1 = registerTexture("Background1");
    private static final ResourceLocation BACKGROUND_2 = registerTexture("Background2");
    private static final ResourceLocation COMPONENTS = registerTexture("FlowComponents");

    @Override
    protected ResourceLocation getComponentResource() {
        return COMPONENTS;
    }

    public static int Z_LEVEL_COMPONENT_OPEN_DIFFERENCE = 100;
    public static int Z_LEVEL_COMPONENT_CLOSED_DIFFERENCE = 1;
    public static int Z_LEVEL_COMPONENT_START = 750;
    public static int Z_LEVEL_OPEN_MAXIMUM = 5;

    @Override
    protected void drawBackground(float f, int x, int y) {

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        bindTexture(BACKGROUND_1);
        drawTexture(0, 0, 0, 0, 256, 256);

        bindTexture(BACKGROUND_2);
        drawTexture(256, 0, 0, 0, 256, 256);

        bindTexture(COMPONENTS);
        for (int i = 0; i < manager.buttons.size(); i++) {
            TileEntityManager.Button button = manager.buttons.get(i);
            int srcButtonY = CollisionHelper.inBounds(button.getX(), button.getY(), TileEntityManager.BUTTON_SIZE_W, TileEntityManager.BUTTON_SIZE_H, x, y) ? 1 : 0;

            drawTexture(button.getX(), button.getY(), TileEntityManager.BUTTON_SRC_X, TileEntityManager.BUTTON_SRC_Y + srcButtonY * TileEntityManager.BUTTON_SIZE_H, TileEntityManager.BUTTON_SIZE_W, TileEntityManager.BUTTON_SIZE_H);
            drawTexture(button.getX() + 1, button.getY() + 1, TileEntityManager.BUTTON_INNER_SRC_X, TileEntityManager.BUTTON_INNER_SRC_Y + i * TileEntityManager.BUTTON_INNER_SIZE_H, TileEntityManager.BUTTON_INNER_SIZE_W, TileEntityManager.BUTTON_INNER_SIZE_H);
        }

        int zLevel = Z_LEVEL_COMPONENT_START;
        int openCount = 0;
        for (int i = 0; i < manager.getZLevelRenderingList().size(); i++) {
            FlowComponent itemBase = manager.getZLevelRenderingList().get(i);

            if (itemBase.isOpen() && openCount == Z_LEVEL_OPEN_MAXIMUM) {
                itemBase.close();
            }
 
            if (itemBase.isOpen()) {
                zLevel -= Z_LEVEL_COMPONENT_OPEN_DIFFERENCE;
                openCount++;
            }else{
                zLevel -= Z_LEVEL_COMPONENT_CLOSED_DIFFERENCE;
            }
			itemBase.draw(this, x, y, zLevel);
			
            if (itemBase.isBeingMoved() || CollisionHelper.inBounds(itemBase.getX(), itemBase.getY(), itemBase.getComponentWidth(), itemBase.getComponentHeight(), x, y)) {
                CollisionHelper.disableInBoundsCheck = true;
            }
        }
        CollisionHelper.disableInBoundsCheck = false;

        for (TileEntityManager.Button button : manager.buttons) {
            if (CollisionHelper.inBounds(button.getX(), button.getY(), TileEntityManager.BUTTON_SIZE_W, TileEntityManager.BUTTON_SIZE_H, x, y)) {
                drawMouseOver(button.getMouseOver(), x, y);
            }
        }

        for (FlowComponent itemBase : manager.getZLevelRenderingList()) {
            itemBase.drawMouseOver(this, x, y);
            if (itemBase.isBeingMoved() || CollisionHelper.inBounds(itemBase.getX(), itemBase.getY(), itemBase.getComponentWidth(), itemBase.getComponentHeight(), x, y)) {
                CollisionHelper.disableInBoundsCheck = true;
            }
        }
        CollisionHelper.disableInBoundsCheck = false;

    }

    @Override
    protected void onClick(int x, int y, int button) {
        for (int i = 0; i < manager.getZLevelRenderingList().size(); i++) {
            FlowComponent itemBase = manager.getZLevelRenderingList().get(i);
            if (itemBase.onClick(x, y, button)) {
                manager.getZLevelRenderingList().remove(i);
                manager.getZLevelRenderingList().add(0, itemBase);
                break;
            }
        }


        onClickButtonCheck(x, y, false);
    }

    private void onClickButtonCheck(int x, int y, boolean release) {
        for (int i = 0; i < manager.buttons.size(); i++) {
            TileEntityManager.Button guiButton = manager.buttons.get(i);
            if (CollisionHelper.inBounds(guiButton.getX(), guiButton.getY(), TileEntityManager.BUTTON_SIZE_W, TileEntityManager.BUTTON_SIZE_H, x, y) && guiButton.activateOnRelease() == release) {
                DataWriter dw = PacketHandler.getButtonPacketWriter();
                dw.writeData(i, DataBitHelper.GUI_BUTTON_ID);
                guiButton.onClick(dw);
                PacketHandler.sendDataToServer(dw);
                break;
            }
        }
    }

    @Override
    protected void onDrag(int x, int y, int button, long ticks) {
        for (FlowComponent itemBase : manager.getZLevelRenderingList()) {
            itemBase.onDrag(x, y);
        }
    }

    @Override
    protected void onRelease(int x, int y, int button) {
        onClickButtonCheck(x, y, true);

        if (!manager.justSentServerComponentRemovalPacket) {
            for (FlowComponent itemBase : manager.getZLevelRenderingList()) {
                itemBase.onRelease(x, y, button);
            }
        }

    }

    @Override
    protected void keyTyped(char c, int k) {
        for (FlowComponent itemBase : manager.getFlowItems()) {
            if (itemBase.onKeyStroke(this, c, k) && k != 1) {
                return;
            }
        }

        super.keyTyped(c, k);
    }


    private TileEntityManager manager;

    public TileEntityManager getManager() {
        return manager;
    }


}
