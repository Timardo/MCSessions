package net.timardo.mcsessions;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraft.network.status.client.CPacketServerQuery;
import net.minecraft.network.status.server.SPacketPong;
import net.minecraft.network.status.server.SPacketServerInfo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class TestCommandServer implements ICommand {
	
	private final List<String> aliases;
	
	public TestCommandServer() {
		aliases = new ArrayList<String>(); 
        aliases.add("testserv"); 
	}

	@Override
	public int compareTo(ICommand arg0) {
		return 0;
	}

	@Override
	public String getName() {
		return "testserver";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/testserver";
	}

	@Override
	public List<String> getAliases() {
		return aliases;
	}

	@Override
	public void execute(MinecraftServer servers, ICommandSender sender, String[] args) throws CommandException {
		ServerData server = new ServerData(servers.getName(), servers.getServerHostname(), false);
		try {
		((EntityPlayerMP)sender).connection.disconnect(new TextComponentString("grc"));
		ServerAddress serveraddress = ServerAddress.fromString(server.serverIP);
        final NetworkManager networkmanager = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(serveraddress.getIP()), serveraddress.getPort(), false);
        server.serverMOTD = I18n.format("multiplayer.status.pinging");
        server.pingToServer = -1L;
        server.playerList = null;
        networkmanager.setNetHandler(new INetHandlerStatusClient()
        {
            private boolean successful;
            private boolean receivedStatus;
            private long pingSentAt;
            public void handleServerInfo(SPacketServerInfo packetIn)
            {
                if (this.receivedStatus)
                {
                    networkmanager.closeChannel(new TextComponentTranslation("multiplayer.status.unrequested", new Object[0]));
                }
                else
                {
                    this.receivedStatus = true;
                    ServerStatusResponse serverstatusresponse = packetIn.getResponse();

                    if (serverstatusresponse.getServerDescription() != null)
                    {
                        server.serverMOTD = serverstatusresponse.getServerDescription().getFormattedText();
                    }
                    else
                    {
                        server.serverMOTD = "";
                    }

                    if (serverstatusresponse.getVersion() != null)
                    {
                        server.gameVersion = serverstatusresponse.getVersion().getName();
                        server.version = serverstatusresponse.getVersion().getProtocol();
                    }
                    else
                    {
                        server.gameVersion = I18n.format("multiplayer.status.old");
                        server.version = 0;
                    }

                    if (serverstatusresponse.getPlayers() != null)
                    {
                        server.populationInfo = TextFormatting.GRAY + "" + serverstatusresponse.getPlayers().getOnlinePlayerCount() + "" + TextFormatting.DARK_GRAY + "/" + TextFormatting.GRAY + serverstatusresponse.getPlayers().getMaxPlayers();

                        if (ArrayUtils.isNotEmpty(serverstatusresponse.getPlayers().getPlayers()))
                        {
                            StringBuilder stringbuilder = new StringBuilder();

                            for (GameProfile gameprofile : serverstatusresponse.getPlayers().getPlayers())
                            {
                                if (stringbuilder.length() > 0)
                                {
                                    stringbuilder.append("\n");
                                }

                                stringbuilder.append(gameprofile.getName());
                            }

                            if (serverstatusresponse.getPlayers().getPlayers().length < serverstatusresponse.getPlayers().getOnlinePlayerCount())
                            {
                                if (stringbuilder.length() > 0)
                                {
                                    stringbuilder.append("\n");
                                }

                                stringbuilder.append(I18n.format("multiplayer.status.and_more", serverstatusresponse.getPlayers().getOnlinePlayerCount() - serverstatusresponse.getPlayers().getPlayers().length));
                            }

                            server.playerList = stringbuilder.toString();
                        }
                    }
                    else
                    {
                        server.populationInfo = TextFormatting.DARK_GRAY + I18n.format("multiplayer.status.unknown");
                    }

                    if (serverstatusresponse.getFavicon() != null)
                    {
                        String s = serverstatusresponse.getFavicon();

                        if (s.startsWith("data:image/png;base64,"))
                        {
                            server.setBase64EncodedIconData(s.substring("data:image/png;base64,".length()));
                        }
                    }
                    else
                    {
                        server.setBase64EncodedIconData((String)null);
                    }

                    net.minecraftforge.fml.client.FMLClientHandler.instance().bindServerListData(server, serverstatusresponse);
                    this.pingSentAt = Minecraft.getSystemTime();
                    networkmanager.sendPacket(new CPacketPing(this.pingSentAt));
                    this.successful = true;
                }
            }
            public void handlePong(SPacketPong packetIn)
            {
                long i = this.pingSentAt;
                long j = Minecraft.getSystemTime();
                server.pingToServer = j - i;
                networkmanager.closeChannel(new TextComponentString("Finished"));
            }
            public void onDisconnect(ITextComponent reason)
            {
                if (!this.successful)
                {
                    server.serverMOTD = TextFormatting.DARK_RED + I18n.format("multiplayer.status.cannot_connect");
                    server.populationInfo = "";
                    // ServerPinger.this.tryCompatibilityPing(server);
                }
            }
        });

        try
        {
            networkmanager.sendPacket(new C00Handshake(serveraddress.getIP(), serveraddress.getPort(), EnumConnectionState.STATUS, true));
            networkmanager.sendPacket(new CPacketServerQuery());
        }
        catch (Throwable throwable)
        {
            ;
        } } catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		finally {
			System.out.println(server.serverMOTD);
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}

}
