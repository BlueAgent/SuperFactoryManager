package vswe.stevesfactory;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import vswe.stevesfactory.components.ModItemHelper;
import vswe.stevesfactory.init.ModBlocks;
import vswe.stevesfactory.lib.ModInfo;
import vswe.stevesfactory.network.FileHelper;
import vswe.stevesfactory.network.PacketEventHandler;
import vswe.stevesfactory.proxy.CommonProxy;

@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.MOD_VERSION)
public class StevesFactoryManager
{
    public static FMLEventChannel packetHandler;

    @SidedProxy(clientSide = "vswe.stevesfactory.proxy.ClientProxy", serverSide = "vswe.stevesfactory.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(ModInfo.MOD_ID)
    public static StevesFactoryManager instance;


    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        packetHandler = NetworkRegistry.INSTANCE.newEventDrivenChannel(ModInfo.MOD_CHANNEL_ID);

        ModBlocks.init();

        proxy.preInit();

        FileHelper.setConfigDir(event.getModConfigurationDirectory());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        packetHandler.register(new PacketEventHandler());

        ModBlocks.addRecipes();
        //new ChatListener();
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

        FMLInterModComms.sendMessage("Waila", "register", "Provider.callbackRegister");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        ModItemHelper.init();
    }


}
