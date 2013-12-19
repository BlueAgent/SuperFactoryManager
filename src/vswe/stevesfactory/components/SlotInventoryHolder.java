package vswe.stevesfactory.components;


import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;
import java.util.Map;

public class SlotInventoryHolder {
    private TileEntity inventory;
    private Map<Integer, SlotSideTarget> validSlots;
    private int sharedOption;

    public SlotInventoryHolder(TileEntity inventory, int sharedOption) {
        this.inventory = inventory;
        this.validSlots = new HashMap<Integer, SlotSideTarget>();
        this.sharedOption = sharedOption;
    }

    public IInventory getInventory() {
        return (IInventory)inventory;
    }

    public Map<Integer, SlotSideTarget> getValidSlots() {
        return validSlots;
    }

    public boolean isShared() {
        return sharedOption == 0;
    }

    public int getSharedOption() {
        return sharedOption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SlotInventoryHolder that = (SlotInventoryHolder) o;

        return inventory.xCoord == that.inventory.xCoord && inventory.yCoord == that.inventory.yCoord && inventory.zCoord == that.inventory.yCoord;
    }

    @Override
    public int hashCode() {
        return inventory.hashCode();
    }
}
