package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.interfaces.IItemBlockProvider;
import vswe.stevesfactory.tiles.TileEntityBUD;
import vswe.stevesfactory.tiles.TileEntityCluster;

public class BlockCableBUD extends BlockContainer {
	public BlockCableBUD() {
		super(Material.IRON);
		setCreativeTab(StevesFactoryManager.creativeTab);
		setSoundType(SoundType.METAL);
		setUnlocalizedName(StevesFactoryManager.UNLOCALIZED_START + "cable_bud");
		setHardness(1.2F);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityBUD();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}


	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
		TileEntityBUD bud = TileEntityCluster.getTileEntity(TileEntityBUD.class, world, pos);
		if (bud != null) {
			bud.onTrigger();
		}
		super.neighborChanged(state, world, pos, blockIn, fromPos);
	}
}
