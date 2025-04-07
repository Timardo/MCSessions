package net.timardo.mcsessions.event;

import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.timardo.mcsessions.MCSessions;

public class ClientEventHandler {
	
	@SubscribeEvent
	public void forwardChat(ClientChatEvent e) {
		if (MCSessions.manager.sessionRunning) {
			MCSessions.manager.sendMessageToServer(new TextComponentString(e.getMessage()));
		}
	}
}
