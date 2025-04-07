package net.timardo.mcsessions.event;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ServerEventHandler {

	@SubscribeEvent
	public void onServerEvent(Event e) {
		if (e instanceof ServerChatEvent) {
			
		}
	}
}
