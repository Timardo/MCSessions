package net.timardo.mcsessions.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.timardo.mcsessions.MCSessions;

public abstract class SessionPacket<REQ extends IMessage> implements IMessage, IMessageHandler<REQ, REQ> {
	
    public REQ onMessage(REQ message, MessageContext context) {
    	
    	if (context.side == Side.SERVER) {
    		handleServerSide(message, context.getServerHandler().player);
    	}
    
    	else {
    		handleClientSide(message, MCSessions.proxy.getClientSidePlayer());
    	}
    
    	return null;
    }
  
    public abstract void handleClientSide(REQ paramREQ, EntityPlayer paramEntityPlayer);
  
    public abstract void handleServerSide(REQ paramREQ, EntityPlayer paramEntityPlayer);
}