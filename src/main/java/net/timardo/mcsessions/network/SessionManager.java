package net.timardo.mcsessions.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import org.apache.commons.lang3.exception.ExceptionUtils;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.timardo.mcsessions.event.ClientDataReceivedEvent;
import net.timardo.mcsessions.network.SessionServer.ClientConnectionHandler;

import static net.timardo.mcsessions.MCSessions.*;

public class SessionManager {
	
	/**
	 * Determines the side we are running on
	 */
	private final Side side;
	/**
	 * Server-only. The Session Server which handles all communication within scheme Client <-> Server <-> Client
	 */
	public SessionServer sessionServer;
	/**
	 * Client-only. Socket which is used to communicate with server (chat, events, commands..)
	 */
	public Socket clientSocket;
	/**
	 * Client-only. Boolean which is true if the client is running an SP world marked as session and thus is connected to the Session Server. Used in events.
	 */
	public boolean sessionRunning = false;
	/**
	 * Client-only. Object stream used to dispatch chat messages, commands etc. to the server
	 */
	private ObjectOutputStream clientDispatcher;
	private ClientReceiverThread clientReceiverThread;

	public SessionManager(Side side) {
		this.side = side;
		
		//SERVER-SIDE ONLY BLOCK START
		if (isServerSide()) {
			this.sessionServer = new SessionServer(10); //TODO configurable max threads

			new Thread(new Runnable() {
				@Override
				public void run() {
					logger.info("Starting Session server!");
					new Thread(sessionServer).start();
					Object obj = new Object();
					
					synchronized (obj) {
					        try {
								obj.wait();
							} 
					        
					        catch (InterruptedException e) {
					        	logger.fatal(ExceptionUtils.getStackTrace(e));
							}
					    }
					
					logger.info("Stopping Session server!");
					sessionServer.terminate();
				}
				
			}).start();
		}
		//SERVER-SIDE ONLY BLOCK END
	}
	
	/**
	 * @return true if running on server, false if running on client
	 */
	public boolean isServerSide() {
		return side.equals(Side.SERVER);
	}
	
	/**
	 * Client-only. Initiates the connection to the Session Server with given params
	 * 
	 * @param host - IP of the Session Server, it's identical to IP of server which we left in order to start a Session
	 * @param port - port of the Session Server
	 */
	public void initSessionConnection(String host, int port) {
		try {
			clientSocket = new Socket(host, port);
			clientSocket.setKeepAlive(true);
			clientDispatcher = new ObjectOutputStream(clientSocket.getOutputStream());
			clientReceiverThread = new ClientReceiverThread(clientSocket);
			clientReceiverThread.setDaemon(true);
			clientReceiverThread.start();
		} 
		
		catch (IOException e) {
			logger.fatal(ExceptionUtils.getStackTrace(e));
		}
		
		closeSessionConnection();
	}
	
	/**
	 * Client-only. Closes the socket connection as well as the output stream and receiver thread
	 */
	private void closeSessionConnection() {
		try {
			clientDispatcher.close();
			clientSocket.close();
			clientReceiverThread.terminate();
		}
		
		catch (IOException e){
			logger.fatal(ExceptionUtils.getStackTrace(e));
		}
	}
	
	/**
	 * Client-only. Sends a chat message to the server. Since this method is called from an event with lowest
	 * EventPriority it's possible for mods as well as session event handlers to alter the message prior it's sent.
	 * 
	 * @param message - ITextComponent containing the message, style, links etc.
	 */
	public void sendMessageToServer(ITextComponent message) {
		try {
			clientDispatcher.writeObject("");
		}
		
		catch (IOException e) {
			logger.fatal(ExceptionUtils.getStackTrace(e));
		}
	}
	
	/**
	 * Server-only. Sends the chat message either received from one of the clients running a session or a regular server player
	 * to every other player running a session
	 * 
	 * @param chatMsg - ITextComponent containing the message
	 * @param sourceClientID - client ID that will be ignored to prevent doubled chat messages
	 */
	public void chatToAllClients(ITextComponent chatMsg, int sourceClientID) {
		for (ClientConnectionHandler client : sessionServer.clients) {
			if (client.ID != sourceClientID) {
				client.chatToClient(chatMsg);
			}
		}
	}

	/**
	 * Client-only. Used to fire the ClientDataReceivedEvent for other mods/session handlers
	 * 
	 * @param receivedObject - received data ready for handling
	 */
	public void fireClientDataReceivedEvent(ISessionClientData receivedObject) {
		ClientDataReceivedEvent event = new ClientDataReceivedEvent(receivedObject);
		MinecraftForge.EVENT_BUS.post(event);
	}
	
	public void fireServerDataReceivedEvent(ISessionClientData receivedData) {
		
	}
}
