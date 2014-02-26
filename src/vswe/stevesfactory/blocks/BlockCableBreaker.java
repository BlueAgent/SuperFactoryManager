package vswe.stevesfactory.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import vswe.stevesfactory.StevesFactoryManager;

//This is indeed not a subclass to the cable, you can't relay signals through this block
public class BlockCableBreaker extends BlockContainer {
    public BlockCableBreaker(int id) {
        super(id, Material.iron);
        setCreativeTab(Blocks.creativeTab);
        setStepSound(soundMetalFootstep);
        setUnlocalizedName(StevesFactoryManager.UNLOCALIZED_START + Blocks.CABLE_BREAKER_UNLOCALIZED_NAME);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityBreaker();
    }

    @SideOnly(Side.CLIENT)
    private Icon doubleIcon;
    @SideOnly(Side.CLIENT)
    private Icon frontIcon;
    @SideOnly(Side.CLIENT)
    private Icon sideIcon;


    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        blockIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_idle");
        doubleIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_breaker");
        frontIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_breaker_front");
        sideIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_breaker_direction");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getIcon(int side, int meta) {
        return side == 3 ? doubleIcon : blockIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Icon getBlockTexture(IBlockAccess world, int x, int y, int z, int side) {
        TileEntityBreaker breaker = TileEntityCluster.getTileEntity(TileEntityBreaker.class, world, x, y, z);

        if (breaker != null) {
            int meta = breaker.getBlockMetadata() % ForgeDirection.VALID_DIRECTIONS.length;
            int direction = breaker.getPlaceDirection();

            if (side == meta && side == direction) {
                return doubleIcon;
            }else if(side == meta) {
                return frontIcon;
            }else if(side == direction) {
                return sideIcon;
            }
        }

        return blockIcon;
    }



    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack item) {
        int meta = BlockPistonBase.determineOrientation(world, x, y, z, entity);

        TileEntityBreaker breaker = TileEntityCluster.getTileEntity(TileEntityBreaker.class, world, x, y, z);
        if (breaker != null) {
            breaker.setMetaData(meta);
            breaker.setPlaceDirection(meta);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            side = ForgeDirection.VALID_DIRECTIONS[side].getOpposite().ordinal();
        }

        TileEntityBreaker breaker = TileEntityCluster.getTileEntity(TileEntityBreaker.class, world, x, y, z);
        if (breaker != null) {
            breaker.setPlaceDirection(side);
            return true;
        }

        return false;
    }
}
