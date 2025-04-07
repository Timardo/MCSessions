package net.timardo.mcsessions.proxy;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.timardo.mcsessions.MCSessions;
import net.timardo.mcsessions.event.ClientEventHandler;
import net.timardo.mcsessions.network.SessionManager;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void init() {
		MCSessions.manager = new SessionManager(Side.CLIENT);
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	}

	@Override
	public void handleSessionCreation(MinecraftServer server, ICommandSender sender, String[] args) {
		MCSessions.manager.sessionRunning = true;
	}

	@Override
	public EntityPlayer getClientSidePlayer() {
		return FMLClientHandler.instance().getClientPlayerEntity();
	}

}
