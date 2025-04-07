package net.timardo.mcsessions.proxy;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.timardo.mcsessions.event.ServerEventHandler;
import net.timardo.mcsessions.network.PacketHandler;
import net.timardo.mcsessions.network.PacketInitializeSessionConnection;
import net.timardo.mcsessions.network.SessionManager;

import static net.timardo.mcsessions.MCSessions.*;

import java.net.InetSocketAddress;

public class CommonProxy {
	
	public void init() {
		manager = new SessionManager(Side.SERVER);
		MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
	}
	
	public void handleSessionCreation(MinecraftServer server, ICommandSender sender, String[] args) {
		PacketHandler.sendTo(new PacketInitializeSessionConnection(server.getServerHostname(), manager.sessionServer.getPort()), (EntityPlayerMP)sender);
	}

	public EntityPlayer getClientSidePlayer() {
		return null;
	}

	public void handleSessionConnection(MinecraftServer server, ICommandSender sender, String[] args) {
		String hostIP = args[0].split(":")[0];
		String clientIP = ((InetSocketAddress)((EntityPlayerMP)sender).connection.netManager.getRemoteAddress()).getAddress().getHostAddress();
		manager.sessionServer.forwardServer.addClientToHostConnection(clientIP, hostIP);
	}
}
