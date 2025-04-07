package net.timardo.mcsessions;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.timardo.mcsessions.network.PacketHandler;
import net.timardo.mcsessions.network.SessionManager;
import net.timardo.mcsessions.proxy.CommonProxy;

@Mod(modid = MCSessions.ID, name = MCSessions.NAME, version = MCSessions.VERSION, acceptedMinecraftVersions = MCSessions.MCVERSION)
public class MCSessions {
	
	public static final String ID = "mcsessions";
	public static final String NAME = "MCSessions";
	public static final String VERSION = "1.0";
	public static final String MCVERSION = "[1.12.2]";
	
	@SidedProxy(serverSide = "net.timardo.mcsessions.proxy.CommonProxy", clientSide = "net.timardo.mcsessions.proxy.ClientProxy")
	public static CommonProxy proxy;
	public static SessionManager manager;
	public static Logger logger;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		PacketHandler.init();
		proxy.init();
	}
	
	@EventHandler
	public void serverStartup(FMLServerStartingEvent event) {
		event.registerServerCommand(new TestCommand());
	}

}
