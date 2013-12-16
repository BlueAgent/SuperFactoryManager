package vswe.stevesjam.components;


import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.lwjgl.opengl.GL11;
import vswe.stevesjam.blocks.TileEntityManager;
import vswe.stevesjam.interfaces.ContainerManager;
import vswe.stevesjam.interfaces.GuiManager;
import vswe.stevesjam.network.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowComponent implements IComponentNetworkReader {
    private static final int COMPONENT_SRC_X = 0;
    private static final int COMPONENT_SRC_Y = 0;
    private static final int COMPONENT_SIZE_W = 64;
    private static final int COMPONENT_SIZE_H = 20;
    private static final int COMPONENT_SIZE_LARGE_W = 124;
    private static final int COMPONENT_SIZE_LARGE_H = 152;
    private static final int COMPONENT_SRC_LARGE_X = 64;
    private static final int DRAGGABLE_SIZE = 6;

    private static final int ARROW_X = -10;
    private static final int ARROW_Y = 5;
    private static final int ARROW_SIZE_W = 9;
    private static final int ARROW_SIZE_H = 10;
    private static final int ARROW_SRC_X = 0;
    private static final int ARROW_SRC_Y = 20;

    private static final int MENU_ITEM_SIZE_W = 120;
    private static final int MENU_ITEM_SIZE_H = 13;
    private static final int MENU_ITEM_SRC_X = 0;
    private static final int MENU_ITEM_SRC_Y = 152;
    private static final int MENU_X = 2;
    private static final int MENU_Y = 20;
    private static final int MENU_SIZE_H = 130;
    private static final int MENU_ITEM_CAPACITY = 5;

    private static final int MENU_ARROW_X = 109;
    private static final int MENU_ARROW_Y = 2;
    private static final int MENU_ARROW_SIZE_W = 9;
    private static final int MENU_ARROW_SIZE_H = 9;
    private static final int MENU_ARROW_SRC_X = 0;
    private static final int MENU_ARROW_SRC_Y = 40;

    private static final int MENU_ITEM_TEXT_X = 5;
    private static final int MENU_ITEM_TEXT_Y = 3;

    private static final int CONNECTION_SIZE_W = 7;
    private static final int CONNECTION_SIZE_H = 6;
    private static final int CONNECTION_SRC_X = 0;
    private static final int CONNECTION_SRC_Y = 58;


    public FlowComponent(TileEntityManager manager, int x, int y, ComponentType type) {
        this.x = x;
        this.y = y;
        this.connectionSet = type.getSets()[0];
        this.type = type;
        this.manager = manager;
        this.id = manager.getFlowItems().size();

        menus = new ArrayList<>();
        for (Class<? extends ComponentMenu> componentMenuClass : type.getClasses()) {
            try {
                Constructor<? extends ComponentMenu> constructor = componentMenuClass.getConstructor(FlowComponent.class);
                Object obj = constructor.newInstance(this);


                menus.add((ComponentMenu)obj);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        openMenuId = -1;
        connections = new HashMap<>();
    }

    private int x;
    private int y;
    private int mouseDragX;
    private int mouseDragY;
    private boolean isDragging;
    private boolean isLarge;
    private List<ComponentMenu> menus;
    private int openMenuId;
    private ConnectionSet connectionSet;
    private ComponentType type;
    private TileEntityManager manager;
    private int id;
    private Map<Integer, Connection> connections;
    private int currentInterval;

    public int getCurrentInterval() {
        return currentInterval;
    }

    public void setCurrentInterval(int currentInterval) {
        this.currentInterval = currentInterval;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ConnectionSet getConnectionSet() {
        return connectionSet;
    }

    public void setConnectionSet(ConnectionSet connectionSet) {
        this.connectionSet = connectionSet;
    }

    public void draw(GuiManager gui, int mX, int mY) {
        gui.drawTexture(x, y, isLarge ? COMPONENT_SRC_LARGE_X : COMPONENT_SRC_X, COMPONENT_SRC_Y, getComponentWidth(), getComponentHeight());

        int internalX = mX - x;
        int internalY = mY - y;

        int srcArrowX = isLarge ? 1 : 0;
        int srcArrowY = inArrowBounds(internalX, internalY) ? 1 : 0;
        gui.drawTexture(x + getComponentWidth() + ARROW_X, y + ARROW_Y, ARROW_SRC_X + ARROW_SIZE_W * srcArrowX, ARROW_SRC_Y + ARROW_SIZE_H * srcArrowY, ARROW_SIZE_W, ARROW_SIZE_H);


        if (isLarge) {
            for (int i = 0; i < menus.size(); i++) {
                ComponentMenu menu = menus.get(i);

                int itemX = getMenuAreaX();
                int itemY = y + getMenuItemY(i);
                gui.drawTexture(itemX, itemY, MENU_ITEM_SRC_X, MENU_ITEM_SRC_Y, MENU_ITEM_SIZE_W, MENU_ITEM_SIZE_H);

                int srcItemArrowX = inMenuArrowBounds(i, internalX, internalY) ? 1 : 0;
                int srcItemArrowY = i == openMenuId ? 1 : 0;
                gui.drawTexture(itemX + MENU_ARROW_X, itemY + MENU_ARROW_Y, MENU_ARROW_SRC_X + MENU_ARROW_SIZE_W * srcItemArrowX, MENU_ARROW_SRC_Y + MENU_ARROW_SIZE_H * srcItemArrowY, MENU_ARROW_SIZE_W, MENU_ARROW_SIZE_H);

                gui.drawString(menu.getName(), x + MENU_X + MENU_ITEM_TEXT_X, y + getMenuItemY(i) + MENU_ITEM_TEXT_Y, 0x404040);

                if (i == openMenuId) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(itemX, getMenuAreaY(i), 0);
                    menu.draw(gui, mX - itemX, mY - getMenuAreaY(i));
                    GL11.glPopMatrix();
                }
            }
        }

        int outputCount = 0;
        int inputCount = 0;
        for (int i = 0; i < connectionSet.getConnections().length; i++) {
            ConnectionOption connection = connectionSet.getConnections()[i];

            int[] location = getConnectionLocation(connection, inputCount, outputCount);
            if (connection.isInput()) {
                inputCount++;
            }else{
                outputCount++;
            }

            int srcConnectionX = (GuiManager.inBounds(location[0], location[1], CONNECTION_SIZE_W, CONNECTION_SIZE_H, mX, mY)) ? 1 : 0;

            Connection current = manager.getCurrentlyConnecting();
            if (current != null && current.getComponentId() == id && current.getConnectionId() == i) {
                gui.drawLine(location[0] + CONNECTION_SIZE_W / 2, location[1] + CONNECTION_SIZE_H / 2, mX, mY);
            }

            Connection connectedConnection = connections.get(i);
            if (connectedConnection != null && id < connectedConnection.getComponentId()) {
                int[] otherLocation = manager.getFlowItems().get(connectedConnection.getComponentId()).getConnectionLocationFromId(connectedConnection.getConnectionId());

                gui.drawLine(location[0] + CONNECTION_SIZE_W / 2, location[1] + CONNECTION_SIZE_H / 2, otherLocation[0] + CONNECTION_SIZE_W / 2, otherLocation[1] + CONNECTION_SIZE_H / 2);
            }

            gui.drawTexture(location[0], location[1], CONNECTION_SRC_X +  srcConnectionX * CONNECTION_SIZE_W, location[2], CONNECTION_SIZE_W, CONNECTION_SIZE_H);
        }

        gui.drawString(getType().toString().charAt(0) + getType().toString().substring(1).toLowerCase(), x + 10, y + 10, 0.7F, 0x404040);
    }

    private int[] getConnectionLocationFromId(int id) {
        int outputCount = 0;
        int inputCount = 0;
        for (int i = 0; i < connectionSet.getConnections().length; i++) {
            ConnectionOption connection = connectionSet.getConnections()[i];

            int[] location = getConnectionLocation(connection, inputCount, outputCount);
            if (id == i) {
                return location;
            }
            if (connection.isInput()) {
                inputCount++;
            }else{
                outputCount++;
            }
        }
        return null;
    }



    public void drawMouseOver(GuiManager gui, int mX, int mY) {
        if (isLarge) {
            for (int i = 0; i < menus.size(); i++) {
                ComponentMenu menu = menus.get(i);

                if (i == openMenuId) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(getMenuAreaX(), getMenuAreaY(i), 0);
                    menu.drawMouseOver(gui, mX - getMenuAreaX(), mY - getMenuAreaY(i));
                    GL11.glPopMatrix();
                }
            }
        }

        int outputCount = 0;
        int inputCount = 0;
        for (ConnectionOption connection : connectionSet.getConnections()) {
            int[] location = getConnectionLocation(connection, inputCount, outputCount);
            if (connection.isInput()) {
                inputCount++;
            }else{
                outputCount++;
            }

            if (GuiManager.inBounds(location[0], location[1], CONNECTION_SIZE_W, CONNECTION_SIZE_H, mX, mY)) {
                gui.drawMouseOver(connection.toString(), mX, mY);
            }
        }
    }

    public void onClick(int mX, int mY, int button) {
        if (GuiManager.inBounds(x, y, getComponentWidth(), getComponentHeight(), mX, mY)) {
           int internalX = mX - x;
           int internalY = mY - y;

            if (internalX <= DRAGGABLE_SIZE && internalY <= DRAGGABLE_SIZE) {
                mouseDragX = mX;
                mouseDragY = mY;
                isDragging = true;
            }else if(inArrowBounds(internalX, internalY)) {
                isLarge = !isLarge;
            }else if (isLarge){

                for (int i = 0; i < menus.size(); i++) {
                    ComponentMenu menu = menus.get(i);

                    if (inMenuArrowBounds(i, internalX, internalY)) {
                        if (openMenuId == i) {
                            openMenuId = -1;
                        }else{
                            openMenuId = i;
                        }

                        return;
                    }

                    if (i == openMenuId) {
                        menu.onClick(mX - getMenuAreaX(), mY - getMenuAreaY(i), button);
                    }
                }

            }
        }else{
            int outputCount = 0;
            int inputCount = 0;
            for (int i = 0; i < connectionSet.getConnections().length; i++) {
                ConnectionOption connection = connectionSet.getConnections()[i];

                int[] location = getConnectionLocation(connection, inputCount, outputCount);
                if (connection.isInput()) {
                    inputCount++;
                }else{
                    outputCount++;
                }

                if (GuiManager.inBounds(location[0], location[1], CONNECTION_SIZE_W, CONNECTION_SIZE_H, mX, mY)) {
                    Connection current = manager.getCurrentlyConnecting();
                    if (current == null) {
                        if (connections.get(i) != null) {
                            removeConnection(i);
                        }
                        manager.setCurrentlyConnecting(new Connection(id, i));
                    }else if (current.getComponentId() == this.id && current.getConnectionId() == i) {
                        manager.setCurrentlyConnecting(null);
                    }else if (current.getComponentId() != id){
                        FlowComponent connectTo = manager.getFlowItems().get(current.getComponentId());
                        ConnectionOption connectToOption = connectTo.connectionSet.getConnections()[current.getConnectionId()];
                        if (connectToOption.isInput() != connection.isInput()) {
                            if (connections.get(i) != null) {
                                removeConnection(i);
                            }

                            Connection thisConnection = new Connection(id, i);
                            connectTo.addConnection(current.getConnectionId(), thisConnection);
                            addConnection(i, manager.getCurrentlyConnecting());
                            manager.setCurrentlyConnecting(null);
                        }
                    }
                }
            }
        }
    }

    private void addConnection(int id, Connection connection) {
        DataWriter dw = PacketHandler.getWriterForServerComponentPacket(this, null);
        if (connection != null) {
            writeConnectionData(dw, id, true, connection.getComponentId(), connection.getConnectionId());
        }else{
            writeConnectionData(dw, id, false, 0, 0);
        }

        PacketHandler.sendDataToServer(dw);
    }

    private void removeConnection(int id) {
        Connection connection = connections.get(id);

        addConnection(id, null);
        manager.getFlowItems().get(connection.getComponentId()).addConnection(connection.getConnectionId(), null);
    }

    public void onDrag(int mX, int mY) {
        followMouse(mX, mY);

        for (int i = 0; i < menus.size(); i++) {
            ComponentMenu menu = menus.get(i);
            menu.onDrag(mX - getMenuAreaX(), mY - getMenuAreaY(i));
        }
    }

    public void onRelease(int mX, int mY) {
        followMouse(mX, mY);
        if (isDragging) {
            writeLocationData();
        }
        isDragging = false;

        for (int i = 0; i < menus.size(); i++) {
            ComponentMenu menu = menus.get(i);
            menu.onRelease(mX - getMenuAreaX(), mY - getMenuAreaY(i));
        }
    }

    private void followMouse(int mX, int mY) {
        if (isDragging) {
            x += mX - mouseDragX;
            y += mY - mouseDragY;

            mouseDragX = mX;
            mouseDragY = mY;
        }
    }


    private boolean inArrowBounds(int internalX, int internalY) {
        return GuiManager.inBounds(getComponentWidth() + ARROW_X, ARROW_Y, ARROW_SIZE_W, ARROW_SIZE_H, internalX, internalY);
    }


    private boolean inMenuArrowBounds(int i, int internalX, int internalY) {
        return GuiManager.inBounds(MENU_X + MENU_ARROW_X, getMenuItemY(i) + MENU_ARROW_Y, MENU_ARROW_SIZE_W, MENU_ARROW_SIZE_H, internalX, internalY);
    }

    private int getMenuItemY(int i) {
        int ret = MENU_Y + i * (MENU_ITEM_SIZE_H - 1);
        if (openMenuId != -1 && openMenuId < i) {
            ret += getMenuOpenSize() - 1;
        }

        return ret;
    }


    public static int getMenuOpenSize() {
        return MENU_SIZE_H - MENU_ITEM_CAPACITY * (MENU_ITEM_SIZE_H - 1);
    }


    private int getComponentWidth() {
        return isLarge ? COMPONENT_SIZE_LARGE_W : COMPONENT_SIZE_W;
    }

    private int getComponentHeight() {
        return isLarge ? COMPONENT_SIZE_LARGE_H : COMPONENT_SIZE_H;
    }

    private int[] getConnectionLocation(ConnectionOption connection, int inputCount, int outputCount) {
        int targetX;
        int targetY;

        int srcConnectionY;
        int currentCount;
        int totalCount;

        if (connection.isInput()) {
            currentCount = inputCount;
            totalCount = connectionSet.getInputCount();
            srcConnectionY = 1;
            targetY = y - CONNECTION_SIZE_H;
        }else{
            currentCount = outputCount;
            totalCount = connectionSet.getOutputCount();
            srcConnectionY = 0;
            targetY = y + getComponentHeight();
        }

        targetX = x + (int)(getComponentWidth() * ((currentCount + 0.5)  / totalCount));
        targetX -= CONNECTION_SIZE_W / 2;

        return new int[] {targetX, targetY, CONNECTION_SRC_Y + srcConnectionY * CONNECTION_SIZE_H};
    }


    private int getMenuAreaX() {
        return x + MENU_X;
    }

    private int getMenuAreaY(int i) {
        return  y + getMenuItemY(i) + MENU_ITEM_SIZE_H;
    }

    public boolean onKeyStroke(GuiManager gui, char c, int k) {
        if (isLarge && openMenuId != -1) {
            return menus.get(openMenuId).onKeyStroke(gui, c, k);
        }

        return false;
    }

    public ComponentType getType() {
        return type;
    }

    public List<ComponentMenu> getMenus() {
        return menus;
    }


    public TileEntityManager getManager() {
        return manager;
    }

    @Override
    public void readNetworkComponent(DataReader dr) {
        if (dr.readBoolean()) {
            x = dr.readData(DataBitHelper.FLOW_CONTROL_X);
            y = dr.readData(DataBitHelper.FLOW_CONTROL_Y);
        }else {
            int connectionId = dr.readData(DataBitHelper.CONNECTION_ID);
            Connection connection;
            if (dr.readBoolean()) {
                int targetComponentId = dr.readData(DataBitHelper.FLOW_CONTROL_COUNT);
                int targetConnectionId = dr.readData(DataBitHelper.CONNECTION_ID);

                connection = new Connection(targetComponentId, targetConnectionId);
            }else{
                connection = null;
            }

            connections.put(connectionId, connection);
        }
    }


    private void writeLocationData() {
        DataWriter dw = PacketHandler.getWriterForServerComponentPacket(this, null);
        writeLocationData(dw);
        PacketHandler.sendDataToServer(dw);
    }

    private void writeLocationData(DataWriter dw) {
        dw.writeBoolean(true);
        dw.writeData(x, DataBitHelper.FLOW_CONTROL_X);
        dw.writeData(y, DataBitHelper.FLOW_CONTROL_Y);
    }

    public FlowComponent copy() {
        FlowComponent copy = new FlowComponent(manager, x, y, type);
        copy.id = id;

        for (int i = 0; i < menus.size(); i++) {
            ComponentMenu menu = menus.get(i);

            copy.menus.get(i).copyFrom(menu);
        }

        return copy;
    }

    private void writeConnectionData(DataWriter dw, int i, boolean target, int targetComponent, int targetConnection) {
        dw.writeBoolean(false);
        dw.writeData(i, DataBitHelper.CONNECTION_ID);
        dw.writeBoolean(target);
        if (target) {
            dw.writeData(targetComponent, DataBitHelper.FLOW_CONTROL_COUNT);
            dw.writeData(targetConnection, DataBitHelper.CONNECTION_ID);
        }
    }

    public void refreshData(ContainerManager container, FlowComponent newData) {
        if (x != newData.x || y != newData.y) {
            x = newData.x;
            y = newData.y;

            DataWriter dw = PacketHandler.getWriterForClientComponentPacket(container, this, null);
            writeLocationData(dw);
            PacketHandler.sendDataToListeningClients(container, dw);
        }

        for (int i = 0; i < connectionSet.getConnections().length; i++) {
            if (newData.connections.get(i) == null && connections.get(i) != null) {
                connections.put(i, null);
                DataWriter dw = PacketHandler.getWriterForClientComponentPacket(container, this, null);
                writeConnectionData(dw, i, false, 0, 0);
                PacketHandler.sendDataToListeningClients(container, dw);
            }

            if (newData.connections.get(i) != null && (connections.get(i) == null || newData.connections.get(i).getComponentId() != connections.get(i).getComponentId() || newData.connections.get(i).getConnectionId() != connections.get(i).getConnectionId())) {
                connections.put(i, newData.connections.get(i));
                DataWriter dw = PacketHandler.getWriterForClientComponentPacket(container, this, null);
                writeConnectionData(dw, i, true, connections.get(i).getComponentId(), connections.get(i).getConnectionId());
                PacketHandler.sendDataToListeningClients(container, dw);
            }
        }

        for (int i = 0; i < menus.size(); i++) {
            menus.get(i).refreshData(container, newData.menus.get(i));
        }
    }

    public int getId() {
        return id;
    }


    public Connection getConnection(int i) {
        return connections.get(i);
    }

    public boolean isBeingMoved() {
        return isDragging;
    }

    public void decreaseId() {
        id--;
    }

    public void updateConnectionIdsAtRemoval(int idToRemove) {
        for (Connection connection : connections.values()) {
            if (connection.getComponentId() > idToRemove) {
                connection.setComponentId(connection.getComponentId() - 1);
            }
        }
    }

    private static final String NBT_POS_X = "PosX";
    private static final String NBT_POS_Y = "PosY";
    private static final String NBT_TYPE= "Type";
    private static final String NBT_CONNECTION = "Connection";
    private static final String NBT_CONNECTION_POS = "ConnectionPos";
    private static final String NBT_CONNECTION_TARGET_COMPONENT = "ConnectionComponent";
    private static final String NBT_CONNECTION_TARGET_CONNECTION = "ConnectionConnection";
    private static final String NBT_INTERVAL = "Interval";
    private static final String NBT_MENUS = "Menus";

    public static FlowComponent readFromNBT(TileEntityManager jam, NBTTagCompound nbtTagCompound) {
        int x = nbtTagCompound.getShort(NBT_POS_X);
        int y = nbtTagCompound.getShort(NBT_POS_Y);
        int typeId = nbtTagCompound.getByte(NBT_TYPE);

        FlowComponent component = new FlowComponent(jam, x, y, ComponentType.getTypeFromId(typeId));

        NBTTagList connections = nbtTagCompound.getTagList(NBT_CONNECTION);
        for (int i = 0; i < connections.tagCount(); i++) {
            NBTTagCompound connectionTag = (NBTTagCompound)connections.tagAt(i);

            Connection connection = new Connection(connectionTag.getByte(NBT_CONNECTION_TARGET_COMPONENT), connectionTag.getByte(NBT_CONNECTION_TARGET_CONNECTION));
            component.connections.put((int)connectionTag.getByte(NBT_CONNECTION_POS), connection);
        }

        if (component.type == ComponentType.TRIGGER) {
            component.currentInterval = nbtTagCompound.getShort(NBT_INTERVAL);
        }

        NBTTagList menuTagList = nbtTagCompound.getTagList(NBT_MENUS);
        for (int i = 0; i < menuTagList.tagCount(); i++) {
            NBTTagCompound menuTag = (NBTTagCompound)menuTagList.tagAt(i);

            component.menus.get(i).readFromNBT(menuTag);
        }

        return component;
    }

    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        nbtTagCompound.setShort(NBT_POS_X, (short)x);
        nbtTagCompound.setShort(NBT_POS_Y, (short)y);
        nbtTagCompound.setByte(NBT_TYPE, (byte)type.getId());

        NBTTagList connections = new NBTTagList();
        for (int i = 0; i < connectionSet.getConnections().length; i++) {
            Connection connection = this.connections.get(i);

            if (connection != null) {
                NBTTagCompound connectionTag = new NBTTagCompound();
                connectionTag.setByte(NBT_CONNECTION_POS, (byte)i);
                connectionTag.setByte(NBT_CONNECTION_TARGET_COMPONENT, (byte)connection.getComponentId());
                connectionTag.setByte(NBT_CONNECTION_TARGET_CONNECTION, (byte)connection.getConnectionId());
                connections.appendTag(connectionTag);
            }
        }
        nbtTagCompound.setTag(NBT_CONNECTION, connections);

        if (type == ComponentType.TRIGGER) {
            nbtTagCompound.setShort(NBT_INTERVAL, (short) currentInterval);
        }

        NBTTagList menuTagList = new NBTTagList();
        for (int i = 0; i < menus.size(); i++) {
            ComponentMenu menu = menus.get(i);

            NBTTagCompound menuTag = new NBTTagCompound();

            menu.writeToNBT(menuTag);

            menuTagList.appendTag(menuTag);

        }
        nbtTagCompound.setTag(NBT_MENUS, menuTagList);
    }
}
