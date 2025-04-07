package net.timardo.mcsessions.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import org.apache.commons.lang3.exception.ExceptionUtils;

import static net.timardo.mcsessions.MCSessions.*;

public class ClientReceiverThread extends Thread {

	private Socket socketConnection;
	private ObjectInputStream inputStream;
	private boolean running;
	
	public ClientReceiverThread(Socket clientSocket) {
		this.socketConnection = clientSocket;
		
		try {
			inputStream = new ObjectInputStream(socketConnection.getInputStream());
		}
		
		catch (IOException e) {
			logger.fatal(ExceptionUtils.getMessage(e));
		}
	}
	
	@Override
	public void run() {
		ISessionClientData receivedObject;
		running = true;
		
		while(running) try {
			receivedObject = (ISessionClientData)inputStream.readObject();
			manager.fireClientDataReceivedEvent(receivedObject);
		}
		
		catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		
		return;
	}

	public void terminate() {
		running = false;
	}
}
