package net.timardo.mcsessions.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.timardo.mcsessions.network.ISessionClientData;

/**
 * This event is fired whenever a server receives an ISessionClientData object through its ClientConnectionHandler from client.
 * Internally used for chat messages, commands and few events. Modders can subscribe to this event and add their own
 * types, dispatchers and handlers to communicate over the session socket connection with custom information or alter
 * already used internal types.
 * 
 * @author Timardo
 *
 */
@Cancelable
public class ServerDataReceiveEvent extends Event {
	
	private final ISessionClientData receivedData;
	
	public ServerDataReceiveEvent(ISessionClientData data) {
		this.receivedData = data;
	}
	
	/**
	 * Gets the ISessionClientData object that has been received.
	 * @return ISessionClientData that should be later checked with instanceof test and then casted into appropriate type.
	 */
	public ISessionClientData getData() {
		return this.receivedData;
	}
}
