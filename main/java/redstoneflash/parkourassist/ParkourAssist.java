package redstoneflash.parkourassist;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import redstoneflash.parkourassist.proxy.CommonProxy;

@Mod(modid = ParkourAssist.MODID, name = ParkourAssist.NAME, version = ParkourAssist.VERSION)
public class ParkourAssist {
	
    public static final String MODID = "parkourassist";
    public static final String NAME = "Parkour Assist";
    public static final String VERSION = "v0.1";
    
    @Instance
    public static ParkourAssist instance;
    
    @SidedProxy(serverSide = "redstoneflash.parkourassist.proxy.ServerProxy", clientSide = "redstoneflash.parkourassist.proxy.ClientProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
    	proxy.init(event);
    }
    
    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	proxy.postInit(event);
    }
    
}
