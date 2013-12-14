package vswe.stevesjam.blocks;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import vswe.stevesjam.components.ComponentType;
import vswe.stevesjam.components.ConnectionSet;
import vswe.stevesjam.components.FlowComponent;

import java.util.ArrayList;
import java.util.List;


public class TileEntityJam extends TileEntity {

    private List<FlowComponent> items;

    public TileEntityJam() {
        items = new ArrayList<>();

    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);

        items.add(new FlowComponent(this, 30, 30, ComponentType.INPUT));
        items.add(new FlowComponent(this, 200, 30, ComponentType.INPUT));
        items.add(new FlowComponent(this, 200, 80, ComponentType.INPUT));
    }

    public List<FlowComponent> getFlowItems() {
        return items;
    }

    public List<TileEntity> getConnectedInventories() {
        List<TileEntity> inventories = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    TileEntity tileEntity = worldObj.getBlockTileEntity(x + xCoord, y + yCoord, z + zCoord);
                    if (tileEntity != null && tileEntity instanceof IInventory) {
                        inventories.add(tileEntity);
                    }
                }
            }
        }

        return  inventories;
    }
}
