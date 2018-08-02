package vswe.stevesfactory.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.registry.ModBlocks;

public class ItemRelay extends ItemBlock {
	public ItemRelay(Block block) {
		super(block);
		setHasSubtypes(true);
		setMaxDamage(0);
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {
		return "tile." + StevesFactoryManager.UNLOCALIZED_START + (BlockCableDirectionAdvanced.isAdvanced(item.getItemDamage()) ? "cable_relay_advanced" : "cable_relay");
	}
}
