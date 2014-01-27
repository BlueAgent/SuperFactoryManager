package vswe.stevesfactory.blocks;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;

public class BlockCableBUD extends BlockContainer {
    public BlockCableBUD(int id) {
        super(id, Material.iron);
        setCreativeTab(Blocks.creativeTab);
        setStepSound(soundMetalFootstep);
        setUnlocalizedName(Blocks.CABLE_BUD_NAME_TAG);
        setHardness(1.2F);
    }

    @Override
    public TileEntity createNewTileEntity(World world) {
        return new TileEntityBUD();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IconRegister register) {
        blockIcon = register.registerIcon(StevesFactoryManager.RESOURCE_LOCATION + ":cable_bud");
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityBUD) {
            TileEntityBUD bud = (TileEntityBUD)te;

            bud.onTrigger();
        }
    }


}
