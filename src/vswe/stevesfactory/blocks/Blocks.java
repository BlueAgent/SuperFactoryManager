package vswe.stevesfactory.blocks;


import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;

public final class Blocks {

    public static final byte NBT_CURRENT_PROTOCOL_VERSION = 7;
    public static final String NBT_PROTOCOL_VERSION = "ProtocolVersion";

    private static final String MANAGER_TILE_ENTITY_TAG = "TileEntityMachineManagerName";
    public static int MANAGER_ID;
    public static final String MANAGER_NAME_TAG = "BlockMachineManagerName";
    public static final String MANAGER_LOCALIZED_NAME = "Machine Inventory Manager";
    public static final int MANAGER_DEFAULT_ID = 1311;

    public static int CABLE_ID;
    public static final String CABLE_NAME_TAG = "BlockCableName";
    public static final String CABLE_LOCALIZED_NAME = "Inventory Cable";
    public static final int CABLE_DEFAULT_ID = 1312;


    private static final String CABLE_RELAY_TILE_ENTITY_TAG = "TileEntityCableRelayName";
    public static int CABLE_RELAY_ID;
    public static final String CABLE_RELAY_NAME_TAG = "BlockCableRelayName";
    public static final String CABLE_ADVANCED_RELAY_NAME_TAG = "BlockAdvancedCableRelayName";
    public static final String CABLE_RELAY_LOCALIZED_NAME = "Inventory Relay";
    public static final String CABLE_ADVANCED_RELAY_LOCALIZED_NAME = "Advanced Inventory Relay";
    public static final int CABLE_RELAY_DEFAULT_ID = 1313;

    private static final String CABLE_OUTPUT_TILE_ENTITY_TAG = "TileEntityCableOutputName";
    public static int CABLE_OUTPUT_ID;
    public static final String CABLE_OUTPUT_NAME_TAG = "BlockCableOutputName";
    public static final String CABLE_OUTPUT_LOCALIZED_NAME = "Redstone Emitter";
    public static final int CABLE_OUTPUT_DEFAULT_ID = 1314;

    private static final String CABLE_INPUT_TILE_ENTITY_TAG = "TileEntityCableInputName";
    public static int CABLE_INPUT_ID;
    public static final String CABLE_INPUT_NAME_TAG = "BlockCableInputName";
    public static final String CABLE_INPUT_LOCALIZED_NAME = "Redstone Receiver";
    public static final int CABLE_INPUT_DEFAULT_ID = 1315;

    private static final String CABLE_CREATIVE_TILE_ENTITY_TAG = "TileEntityCableCreativeName";
    public static int CABLE_CREATIVE_ID;
    public static final String CABLE_CREATIVE_NAME_TAG = "BlockCableCreativeName";
    public static final String CABLE_CREATIVE_LOCALIZED_NAME = "Creative Supplier";
    public static final int CABLE_CREATIVE_DEFAULT_ID = 1316;

    public static BlockManager blockManager;
    public static BlockCable blockCable;
    public static BlockCableRelay blockCableRelay;
    public static BlockCableOutput blockCableOutput;
    public static BlockCableInput blockCableInput;
    public static BlockCableCreative blockCableCreative;

    public static void init() {
        blockManager = new BlockManager(MANAGER_ID);
        GameRegistry.registerBlock(blockManager, MANAGER_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityManager.class, MANAGER_TILE_ENTITY_TAG);

        blockCable = new BlockCable(CABLE_ID);
        GameRegistry.registerBlock(blockCable, CABLE_NAME_TAG);

        blockCableRelay = new BlockCableRelay(CABLE_RELAY_ID);
        GameRegistry.registerBlock(blockCableRelay, ItemRelay.class, CABLE_RELAY_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityRelay.class, CABLE_RELAY_TILE_ENTITY_TAG);

        blockCableOutput = new BlockCableOutput(CABLE_OUTPUT_ID);
        GameRegistry.registerBlock(blockCableOutput, CABLE_OUTPUT_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityOutput.class, CABLE_OUTPUT_TILE_ENTITY_TAG);

        blockCableInput = new BlockCableInput(CABLE_INPUT_ID);
        GameRegistry.registerBlock(blockCableInput, CABLE_INPUT_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityInput.class, CABLE_INPUT_TILE_ENTITY_TAG);

        blockCableCreative = new BlockCableCreative(CABLE_CREATIVE_ID);
        GameRegistry.registerBlock(blockCableCreative, CABLE_CREATIVE_NAME_TAG);
        GameRegistry.registerTileEntity(TileEntityCreative.class, CABLE_CREATIVE_TILE_ENTITY_TAG);
    }

    public static void addNames() {
        LanguageRegistry.addName(blockManager, MANAGER_LOCALIZED_NAME);
        LanguageRegistry.addName(blockCable, CABLE_LOCALIZED_NAME);
        LanguageRegistry.addName(new ItemStack(blockCableRelay, 1 , 0), CABLE_RELAY_LOCALIZED_NAME);
        LanguageRegistry.addName(new ItemStack(blockCableRelay, 1 , 8), CABLE_ADVANCED_RELAY_LOCALIZED_NAME);
        LanguageRegistry.addName(blockCableOutput, CABLE_OUTPUT_LOCALIZED_NAME);
        LanguageRegistry.addName(blockCableInput, CABLE_INPUT_LOCALIZED_NAME);
        LanguageRegistry.addName(blockCableCreative, CABLE_CREATIVE_LOCALIZED_NAME);
    }

    public static void addRecipes() {
        GameRegistry.addRecipe(new ItemStack(blockManager),
                "III",
                "IRI",
                "SPS",
                'R', Block.blockRedstone,
                'P', Block.pistonBase,
                'I', Item.ingotIron,
                'S', Block.stone
        );

        GameRegistry.addRecipe(new ItemStack(blockCable, 8),
                "GPG",
                "IRI",
                "GPG",
                'R', Item.redstone,
                'G', Block.glass,
                'I', Item.ingotIron,
                'P', Block.pressurePlateIron
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableRelay, 1),
                blockCable,
                Block.hopperBlock
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableOutput, 1),
                blockCable,
                Item.redstone,
                Item.redstone,
                Item.redstone
        );


        GameRegistry.addShapelessRecipe(new ItemStack(blockCableInput, 1),
                blockCable,
                Item.redstone
        );

        GameRegistry.addShapelessRecipe(new ItemStack(blockCableRelay, 1, 8),
                new ItemStack(blockCableRelay, 1, 0),
                new ItemStack(Item.dyePowder, 1, 4)
        );
    }

    private Blocks() {}
}
