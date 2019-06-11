package net.timardo.mcsessions;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.timardo.mcsessions.proxy.CommonProxy;

@Mod(modid = MCSessions.ID, name = MCSessions.NAME, version = MCSessions.VERSION, acceptedMinecraftVersions = MCSessions.MCVERSION)
public class MCSessions {
	
	public static final String ID = "mcsessions";
	public static final String NAME = "MCSessions";
	public static final String VERSION = "1.0";
	public static final String MCVERSION = "[1.12.2]";
	
	//@SidedProxy(serverSide = "net.timardo.mcsessions.proxy.ServerProxy")
	public static CommonProxy proxy;
	public static SessionManager manager;
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		//proxy.init();
		ClientCommandHandler.instance.registerCommand(new TestCommand());
	}
	
	@EventHandler
	public void serverStartup(FMLServerStartingEvent event) {
		event.registerServerCommand(new TestCommandServer());
	}

}
