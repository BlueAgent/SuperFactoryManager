package vswe.stevesfactory.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.FakePlayerFactory;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ForgeHooks;
import vswe.stevesfactory.network.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class TileEntityBreaker extends TileEntityClusterElement implements IInventory, IPacketBlock {

    private static final String FAKE_PLAYER_NAME = "[SFM_PLAYER]";
    private List<ItemStack> inventory;
    private List<ItemStack> inventoryCache;
    private boolean broken;
    private int placeDirection;


    private List<ItemStack> getInventory() {
        if (inventory == null) {
            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length];

            int x = xCoord + direction.offsetX;
            int y = yCoord + direction.offsetY;
            int z = zCoord + direction.offsetZ;
            Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];
            if (canBreakBlock(block, x, y, z)) {
                inventory = block.getBlockDropped(worldObj, x, y, z, worldObj.getBlockMetadata(x, y, z), 0);
            }
            if (inventory == null) {
                inventory = new ArrayList<ItemStack>();
            }
            inventoryCache = new ArrayList<ItemStack>();
            for (ItemStack itemStack : inventory) {
                inventoryCache.add(itemStack.copy());
            }
        }

        return inventory;
    }

    private void placeItem(ItemStack itemstack) {
        ForgeDirection side = ForgeDirection.VALID_DIRECTIONS[getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length];
        ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[placeDirection];

        int x = xCoord + side.offsetX - direction.offsetX;
        int y = yCoord + side.offsetY - direction.offsetY;
        int z = zCoord + side.offsetZ - direction.offsetZ;
        float hitX = 0.5F + direction.offsetX * 0.5F;
        float hitY = 0.5F + direction.offsetY * 0.5F;
        float hitZ = 0.5F + direction.offsetZ * 0.5F;

        EntityPlayerMP player = FakePlayerFactory.get(worldObj, FAKE_PLAYER_NAME);
        int rotationSide = ROTATION_SIDE_MAPPING[placeDirection];
        player.rotationYaw = rotationSide * 90;

        if (itemstack.getItem() != null && itemstack.stackSize > 0) {
            System.out.println("Side: " + side.ordinal());
            System.out.println("Direction: " + direction.ordinal());
            System.out.println();
            player.theItemInWorldManager.activateBlockOrUseItem(player, worldObj, itemstack, x, y, z, placeDirection, hitX, hitY, hitZ);
        }
    }

    private static  final  double SPEED_MULTIPLIER = 0.05F;
    private static final Random rand = new Random();

    @Override
    public void updateEntity() {
        if (missingPlaceDirection) {
            setPlaceDirection(getBlockMetadata());
            missingPlaceDirection = false;
        }
        if (worldObj.isRemote) {
            keepClientDataUpdated();
        }

        if (inventory != null) {
            ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length];

            double x = xCoord + direction.offsetX;
            double y = yCoord + direction.offsetY;
            double z = zCoord + direction.offsetZ;

            for (ItemStack itemStack : getInventoryForDrop()) {
                placeItem(itemStack);
                if (itemStack != null && itemStack.stackSize > 0) {


                    double spawnX = x + rand.nextDouble() * 0.8 + 0.1;
                    double spawnY = y + rand.nextDouble() * 0.8 + 0.1;
                    double spawnZ = z + rand.nextDouble() * 0.8 + 0.1;

                    EntityItem entityitem = new EntityItem(worldObj, spawnX, spawnY, spawnZ, itemStack);

                    entityitem.motionX = rand.nextGaussian() * SPEED_MULTIPLIER;
                    entityitem.motionY = rand.nextGaussian() * SPEED_MULTIPLIER + 0.2F;
                    entityitem.motionZ = rand.nextGaussian() * SPEED_MULTIPLIER;

                    worldObj.spawnEntityInWorld(entityitem);
                }
            }
        }
        inventory = null;
        inventoryCache = null;
        broken = false;
    }


    private List<ItemStack> getInventoryForDrop() {
        List<ItemStack> ret = new ArrayList<ItemStack>();
        for (ItemStack itemStack : inventory) {
            if (itemStack != null) {
                ItemStack newStack = itemStack.copy();


                if (!broken) {
                    for (int i = 0; i < inventoryCache.size(); i++) {
                        ItemStack copyStack = inventoryCache.get(i);

                        if (copyStack != null && newStack.isItemEqual(copyStack) && ItemStack.areItemStackTagsEqual(newStack, copyStack)) {
                            int max = Math.min(copyStack.stackSize, newStack.stackSize);

                            copyStack.stackSize -= max;
                            if (copyStack.stackSize == 0) {
                                inventoryCache.set(0, null);
                            }

                            newStack.stackSize -= max;
                            if (newStack.stackSize == 0) {
                                newStack = null;
                                break;
                            }
                        }
                    }
                }


                if (newStack != null) {
                    ret.add(newStack);
                }
            }
        }
        return ret;
    }

    @Override
    public int getSizeInventory() {
        return getInventory().size() + 1;
    }

    @Override
    public ItemStack getStackInSlot(int id) {
        if (id < getInventory().size()) {
            return getInventory().get(id);
        }else{
            return null;
        }
    }

    @Override
    public ItemStack decrStackSize(int id, int count) {

        ItemStack item = getStackInSlot(id);
        if (item != null) {
            if (item.stackSize <= count) {
                getInventory().set(id, null);
                return item;
            }

            ItemStack ret = item.splitStack(count);

            if (item.stackSize == 0) {
                getInventory().set(id, null);
            }

            return ret;
        }else{
            return null;
        }

    }

    private static final int[] ROTATION_SIDE_MAPPING = {0, 0, 2, 0, 1, 3};

    @Override
    public void setInventorySlotContents(int id, ItemStack itemstack) {
        if (id <  getInventory().size()) {
            getInventory().set(id, itemstack);
        }else{
            getInventory().add(itemstack);
            inventoryCache.add(null);
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return null;
    }

    @Override
    public String getInvName() {
        return Blocks.blockCableBreaker.getLocalizedName();
    }

    @Override
    public boolean isInvNameLocalized() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return false;
    }

    @Override
    public void openChest() {

    }

    @Override
    public void closeChest() {

    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public void onInventoryChanged() {
        super.onInventoryChanged();

        if (inventory != null && !broken) {
            boolean match = true;
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack itemStack = inventory.get(i);
                ItemStack itemStackCopy = inventoryCache.get(i);

                if (itemStackCopy != null && (itemStack == null || itemStack.itemID != itemStackCopy.itemID || itemStack.getItemDamage() != itemStackCopy.getItemDamage() || !ItemStack.areItemStackTagsEqual(itemStack, itemStackCopy) || itemStack.stackSize < itemStackCopy.stackSize)) {
                    match = false;
                    break;
                }
            }

           if (!match) {
               ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length];

               int x = xCoord + direction.offsetX;
               int y = yCoord + direction.offsetY;
               int z = zCoord + direction.offsetZ;

               Block block = Block.blocksList[worldObj.getBlockId(x, y, z)];


               if (canBreakBlock(block, x, y, z)) {
                   broken = true;
                   int meta = worldObj.getBlockMetadata(x, y, z);
                   block.breakBlock(worldObj, x, y, z, block.blockID, meta);
                   worldObj.playAuxSFX(2001, x, y, z, block.blockID + (meta << 12));
                   worldObj.setBlockToAir(x, y, z);
               }

           }
        }
    }

    private boolean canBreakBlock(Block block, int x, int y, int z) {
        return block != null && block.blockID != Block.bedrock.blockID && block.getBlockHardness(worldObj, x, y, z) >= 0;
    }

    @Override
    protected EnumSet<ClusterMethodRegistration> getRegistrations() {
        return EnumSet.of(ClusterMethodRegistration.ON_BLOCK_PLACED_BY, ClusterMethodRegistration.ON_BLOCK_ACTIVATED);
    }

    private static final String NBT_DIRECTION = "Direction";

    private boolean missingPlaceDirection;
    @Override
    protected void readContentFromNBT(NBTTagCompound tagCompound) {
        if (tagCompound.hasKey(NBT_DIRECTION)) {
            setPlaceDirection(tagCompound.getByte(NBT_DIRECTION));
        }else{
            if (worldObj != null) {
                setPlaceDirection(getBlockMetadata());
            }else{
                missingPlaceDirection = true;
            }
        }
    }

    @Override
    protected void writeContentToNBT(NBTTagCompound tagCompound) {
        //TODO
        //tagCompound.setByte(NBT_DIRECTION, (byte)placeDirection);
    }

    private static final int UPDATE_BUFFER_DISTANCE = 5;
    private boolean hasUpdatedData;

    @SideOnly(Side.CLIENT)
    private void keepClientDataUpdated() {
        if (isPartOfCluster()) {
            return;
        }

        double distance = Minecraft.getMinecraft().thePlayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);

        if (distance > Math.pow(PacketHandler.BLOCK_UPDATE_RANGE, 2)) {
            hasUpdatedData = false;
        }else if(!hasUpdatedData && distance < Math.pow(PacketHandler.BLOCK_UPDATE_RANGE - UPDATE_BUFFER_DISTANCE, 2)) {
            hasUpdatedData = true;
            PacketHandler.sendBlockPacket(this, Minecraft.getMinecraft().thePlayer, 0);
        }
    }

    @Override
    public void writeData(DataWriter dw, EntityPlayer player, boolean onServer, int id) {
        if (onServer) {
            dw.writeData(placeDirection, DataBitHelper.PLACE_DIRECTION);
        }else{
            //nothing to write, empty packet
        }
    }

    @Override
    public void readData(DataReader dr, EntityPlayer player, boolean onServer, int id) {
        if (onServer) {
            //respond by sending the data to the client that required it
            PacketHandler.sendBlockPacket(this, player, 0);
        }else{
            placeDirection = dr.readData(DataBitHelper.PLACE_DIRECTION);
            worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public int infoBitLength(boolean onServer) {
        return 0;
    }

    public int getPlaceDirection() {
        return placeDirection;
    }

    public void setPlaceDirection(int placeDirection) {
        if (this.placeDirection != placeDirection) {
            this.placeDirection = placeDirection;

            if (!isPartOfCluster() && worldObj != null && !worldObj.isRemote) {
                PacketHandler.sendBlockPacket(this, null, 0);
            }
        }
    }


}
