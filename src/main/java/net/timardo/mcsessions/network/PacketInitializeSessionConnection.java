package net.timardo.mcsessions.network;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.timardo.mcsessions.MCSessions;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class PacketInitializeSessionConnection extends SessionPacket<PacketInitializeSessionConnection> {
	private String IPAdress;
	private int port;
	
	public PacketInitializeSessionConnection() {}

	public PacketInitializeSessionConnection(String serverHostname, int serverPort) {
		this.IPAdress = serverHostname;
		this.port = serverPort;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.IPAdress = ByteBufUtils.readUTF8String(buf);
		this.port = buf.readInt();
		
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, this.IPAdress);
		buf.writeInt(this.port);
		
	}

	@Override
	public void handleClientSide(PacketInitializeSessionConnection paramREQ, EntityPlayer paramEntityPlayer) {
		MCSessions.manager.initSessionConnection(paramREQ.IPAdress, paramREQ.port);
	}

	@Override
	public void handleServerSide(PacketInitializeSessionConnection paramREQ, EntityPlayer paramEntityPlayer) {}

}
