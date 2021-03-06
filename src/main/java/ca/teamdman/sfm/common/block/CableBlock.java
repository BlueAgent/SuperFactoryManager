package ca.teamdman.sfm.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class CableBlock extends Block implements ICable {
	public CableBlock(final Properties props) {
		super(props);
	}

	@Override
	public boolean isValidPosition(
		BlockState state, IWorldReader worldIn, BlockPos pos
	) {
		return super.isValidPosition(state, worldIn, pos);
	}
}
