package vswe.stevesfactory.items.itemblocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.init.ModBlocks;
import vswe.stevesfactory.lib.ModInfo;

public class ItemBlockRelay extends ItemBlock
{
    public ItemBlockRelay(Block block)
    {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public String getUnlocalizedName(ItemStack item)
    {
        return "tile." + ModInfo.UNLOCALIZED_START + (ModBlocks.blockCableRelay.isAdvanced(item.getItemDamage()) ? ModBlocks.CABLE_ADVANCED_RELAY_UNLOCALIZED_NAME : ModBlocks.CABLE_RELAY_UNLOCALIZED_NAME);
    }
}
