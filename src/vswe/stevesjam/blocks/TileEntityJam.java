package vswe.stevesjam.blocks;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import vswe.stevesjam.components.*;
import vswe.stevesjam.network.DataBitHelper;
import vswe.stevesjam.network.DataReader;
import vswe.stevesjam.network.DataWriter;

import java.util.*;


public class TileEntityJam extends TileEntity {

    private List<FlowComponent> items;
    private Connection currentlyConnecting;
    private boolean isPowered;
    public List<Button> buttons;
    public boolean justSentServerComponentRemovalPacket;

    public TileEntityJam() {
        items = new ArrayList<>();
        buttons = new ArrayList<>();
        removedIds = new ArrayList<>();

        for (int i = 0; i < ComponentType.values().length; i++) {
            buttons.add(new ButtonCreate(ComponentType.values()[i]));
        }

        buttons.add(new Button("Delete [Drop command here]") {
            @Override
            protected void onClick(DataReader dr) {
                int idToRemove = dr.readData(DataBitHelper.FLOW_CONTROL_COUNT);
                removeFlowComponent(idToRemove);
            }

            @Override
            public void onClick(DataWriter dw) {
                justSentServerComponentRemovalPacket = true;
                for (FlowComponent item : items) {
                    if (item.isBeingMoved()) {
                        dw.writeData(item.getId(), DataBitHelper.FLOW_CONTROL_COUNT);
                        return;
                    }
                }
            }

            @Override
            public boolean activateOnRelease() {
                return true;
            }
        });
    }

    private List<Integer> removedIds;

    public void removeFlowComponent(int idToRemove, List<FlowComponent> items) {
        for (int i =  items.size() - 1; i >= 0; i--) {
            if (i == idToRemove) {
                items.remove(i);
            }else{
                FlowComponent component = items.get(i);
                if (i > idToRemove) {
                    component.decreaseId();
                }
                component.updateConnectionIdsAtRemoval(idToRemove);
            }
        }


    }

    public void removeFlowComponent(int idToRemove) {
        removeFlowComponent(idToRemove, items);
        if (!worldObj.isRemote) {
            removedIds.add(idToRemove);
        }
    }


    public List<FlowComponent> getFlowItems() {
        return items;
    }

    List<TileEntity> inventories = new ArrayList<>();
    public List<TileEntity> getConnectedInventories() {
        return inventories;
    }

    public static final int MAX_CABLE_LENGTH = 64;

    public void updateInventories() {
        WorldCoordinate[] oldCoordinates = new WorldCoordinate[inventories.size()];
        for (int i = 0; i < oldCoordinates.length; i++) {
            TileEntity inventory = inventories.get(i);
            oldCoordinates[i] = new WorldCoordinate(inventory.xCoord, inventory.yCoord, inventory.zCoord);
        }

        List<WorldCoordinate> visited = new ArrayList<>();
        inventories.clear();
        Queue<WorldCoordinate> queue = new PriorityQueue<>();
        WorldCoordinate start = new WorldCoordinate(xCoord, yCoord, zCoord, 0);
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            WorldCoordinate element = queue.poll();

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (Math.abs(x) + Math.abs(y) + Math.abs(z) == 1) {
                            WorldCoordinate target = new WorldCoordinate(element.getX() + x, element.getY() + y, element.getZ() + z, element.getDepth() + 1);

                            if (!visited.contains(target)) {
                                visited.add(target);
                                TileEntity tileEntity = worldObj.getBlockTileEntity(target.getX(), target.getY(), target.getZ());
                                if (tileEntity != null && tileEntity instanceof IInventory) {
                                    inventories.add(tileEntity);
                                }else if (element.getDepth() < MAX_CABLE_LENGTH){
                                    if (worldObj.getBlockId(target.getX(), target.getY(), target.getZ()) == Blocks.blockCable.blockID) {
                                        queue.add(target);
                                    }
                                }
                            }
                        }

                    }
                }
            }

        }

        if (!worldObj.isRemote) {
            updateInventorySelection(oldCoordinates);
        }
    }

    private void updateInventorySelection(WorldCoordinate[] oldCoordinates) {
        for (FlowComponent item : items) {
            for (ComponentMenu menu : item.getMenus()) {
                if (menu instanceof ComponentMenuInventory) {
                    ComponentMenuInventory menuInventory = (ComponentMenuInventory)menu;

                    if (menuInventory.getSelectedInventory() >= 0 && menuInventory.getSelectedInventory() < oldCoordinates.length) {
                        WorldCoordinate coordinate = oldCoordinates[menuInventory.getSelectedInventory()];

                        boolean foundInventory = false;
                        for (int i = 0; i < inventories.size(); i++) {
                            TileEntity inventory = inventories.get(i);
                            if (coordinate.getX() == inventory.xCoord && coordinate.getY() == inventory.yCoord && coordinate.getZ() == inventory.zCoord) {
                                foundInventory = true;
                                menuInventory.setSelectedInventory(i);
                                break;
                            }
                        }

                        if (!foundInventory) {
                            menuInventory.setSelectedInventory(-1);
                        }

                    }
                }
            }
        }
    }


    public Connection getCurrentlyConnecting() {
        return currentlyConnecting;
    }

    public void setCurrentlyConnecting(Connection currentlyConnecting) {
        this.currentlyConnecting = currentlyConnecting;
    }

    private int timer = 0;

    @Override
    public void updateEntity() {
        justSentServerComponentRemovalPacket = false;
        if (!worldObj.isRemote) {
            if (timer >= 20) {
                timer = 0;

                for (FlowComponent item : items) {
                    if (item.getType() == ComponentType.TRIGGER) {
                        for (ComponentMenu menu : item.getMenus()) {
                            if (menu instanceof ComponentMenuInterval) {
                                int interval = ((ComponentMenuInterval)menu).getInterval();
                                item.setCurrentInterval(item.getCurrentInterval() + 1);
                                if (item.getCurrentInterval() >= interval) {
                                    item.setCurrentInterval(0);

                                    EnumSet<ConnectionOption> valid = EnumSet.of(ConnectionOption.INTERVAL);
                                    if (isPowered) {
                                        valid.add(ConnectionOption.REDSTONE_HIGH);
                                    }else{
                                        valid.add(ConnectionOption.REDSTONE_LOW);
                                    }
                                    activateTrigger(item, valid);
                                }
                            }
                        }
                    }
                }


            }else{
                timer++;
            }
        }
    }



    private void activateTriggers(EnumSet<ConnectionOption> validTriggerOutputs) {
        for (FlowComponent item : items) {
            if (item.getType() == ComponentType.TRIGGER) {
                activateTrigger(item, validTriggerOutputs);
            }
        }
    }

    private void activateTrigger(FlowComponent component, EnumSet<ConnectionOption> validTriggerOutputs) {
        for (TileEntity inventory : inventories) {
            if (inventory.isInvalid()) {
                updateInventories();
                break;
            }
        }

        new CommandExecutor(this).executeTriggerCommand(component, validTriggerOutputs);
    }


    public void triggerRedstone(boolean powered) {
        if (powered && !this.isPowered) {
            activateTriggers(EnumSet.of(ConnectionOption.REDSTONE_PULSE_HIGH));
        }else if (!powered && this.isPowered) {
            activateTriggers(EnumSet.of(ConnectionOption.REDSTONE_PULSE_LOW));
        }


        this.isPowered = powered;
    }

    public void readGenericData(DataReader dr) {
        if (worldObj.isRemote) {
            if (dr.readBoolean()){
                updateInventories();
            }else{
                removeFlowComponent(dr.readData(DataBitHelper.FLOW_CONTROL_COUNT));
            }
        }else{
            int buttonId = dr.readData(DataBitHelper.GUI_BUTTON_ID);
            if (buttonId >= 0 && buttonId < buttons.size()) {
                buttons.get(buttonId).onClick(dr);
            }
        }
    }



    private TileEntityJam self = this;

    public List<Integer> getRemovedIds() {
        return removedIds;
    }

    public abstract class Button {
        private int x;
        private int y;
        private String mouseOver;

        protected Button(String mouseOver) {
            this.x = 5;
            this.y = 5 + buttons.size() * 18;
            this.mouseOver = mouseOver;
        }

        protected abstract void onClick(DataReader dr);
        public abstract void onClick(DataWriter dw);

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public String getMouseOver() {
            return mouseOver;
        }

        public boolean activateOnRelease() {
            return false;
        }
    }

    private class ButtonCreate extends Button {

        private ComponentType type;

        protected ButtonCreate(ComponentType type) {
            super("Create " + type.toString().charAt(0) + type.toString().toLowerCase().substring(1));

            this.type = type;
        }

        @Override
        protected void onClick(DataReader dr) {
            getFlowItems().add(new FlowComponent(self, 50, 50, type));
        }

        @Override
        public void onClick(DataWriter dw) {

        }
    }
}
