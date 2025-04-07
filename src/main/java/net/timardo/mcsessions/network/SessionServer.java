package net.timardo.mcsessions.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.exception.ExceptionUtils;

import net.minecraft.util.text.ITextComponent;

import static net.timardo.mcsessions.MCSessions.*;

public class SessionServer implements Runnable {
	
	private final ExecutorService threadPool;
	
	private ServerSocket serverSocket;
	private Thread runningThread; //not sure why is this here, taken from stack overflow
	private boolean running;
	private int nextClientID = 0;
	public ForwardServerThread forwardServer;
	
	public List<ClientConnectionHandler> clients = new ArrayList<ClientConnectionHandler>();
	
	public SessionServer(int maxThreads) {
		 threadPool = Executors.newFixedThreadPool(maxThreads);
	}
	
	@Override
	public void run() {
		synchronized(this) {
            runningThread = Thread.currentThread();
        }
		
		try {
			serverSocket = new ServerSocket(0);
			logger.info("Session Server is being started on port " + getPort());
			running = false;
		} 
		
		catch (IOException e) {
			logger.fatal(ExceptionUtils.getStackTrace(e));
		}
		
        while(running) {
            Socket clientSocket = null;
            
            try {
                clientSocket = serverSocket.accept();
            }
            
            catch (IOException exc) {
                if(!running) {
                    break;
                }
                
                logger.fatal(ExceptionUtils.getStackTrace(exc));
            }
            
            ClientConnectionHandler newConnection = new ClientConnectionHandler(clientSocket, this);
            threadPool.execute(newConnection);
            clients.add(newConnection);
        }
        
        threadPool.shutdown();
	}
	
	public synchronized int getPort() {
		return serverSocket.getLocalPort();
	}	

	public synchronized boolean isRunning() {
		return running;
	}
	
	public synchronized void terminate() {
		running = false;
		
		try {
            serverSocket.close();
        }
		
		catch (IOException e) {
        	ExceptionUtils.getStackTrace(e);
        }
	}
	
	public synchronized ClientConnectionHandler getClientByID(int id) {
		for (ClientConnectionHandler client : clients) {
			if (client.ID == id) {
				return client;
			}
		}
		
		return null;
	}
	
	public synchronized int getFwServerPort() {
		return forwardServer.forwardSocket.getLocalPort();
	}
	
	/**
	 * This class represents the connection between client and session server
	 * @author Timardo
	 *
	 */
	public static class ClientConnectionHandler implements Runnable {
		
		public final Socket clientSocket;
		public final SessionServer clientOwner;
		public final int ID;
		//public 
		
		private ObjectOutputStream out;
		private ObjectInputStream in;
		
		public ClientConnectionHandler(Socket socket, SessionServer server) {
			this.clientSocket = socket;
			this.clientOwner = server;
			this.ID = server.nextClientID;
			server.nextClientID++;
		}
		
		@Override
		public void run() {
            try {
            	logger.info("Client connection established");
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
                ISessionClientData inboundData;
                
                while(clientOwner.isRunning()) try {
                	inboundData = (ISessionClientData)in.readObject();
                	manager.fireServerDataReceivedEvent(inboundData);
                }
                
                catch (ClassNotFoundException | IOException e) {
                	logger.error(ExceptionUtils.getStackTrace(e));
                    break;
                }
                
                in.close();
                out.close();
                clientSocket.close();
            }
            
            catch (IOException ex) {
                logger.debug(ExceptionUtils.getStackTrace(ex));
            }
        }
		
		public synchronized void chatToClient(ITextComponent message) {
			try {
				out.writeObject("s");
			}
			
			catch (IOException e) {
				logger.error(ExceptionUtils.getStackTrace(e));
			}
		}
	}
	
	/**
	 * Server thread used to forward data stream from clients which want to connect to a client hosting a session to the host.
	 * Acts as a simple proxy server to bypass blocked ports by firewall and/or NATs. This code is based on
	 * NakovForwardServer project on Github.
	 * 
	 * @author Timardo
	 *
	 */
	public static class ForwardServerThread extends Thread {
		
		private ServerSocket forwardSocket;
		private boolean running = false;
		
		//             hostIP  ListOfPlayerIPs
		public HashMap<String, List<String>> sessionHostPlayerPair = new HashMap<String, List<String>>();
		//            playerIP hostIP
		public HashMap<String, String> playerSessionHostPair = new HashMap<String, String>();
		
		public ForwardServerThread() {
			try {
				this.forwardSocket = new ServerSocket(0);
				this.running = true;
			}
			
			catch (IOException e) {
				logger.fatal(ExceptionUtils.getStackTrace(e));
			}
		}
		
		// THIS CODE IS COMPLETELY BROKEN
		// I HAVE NO IDEA WHAT I WAS DOING 6 YEARS AGO 
		@Override
		public void run() {
			/*try*/ {
            	logger.info("Forward Server starting on port " + forwardSocket.getLocalPort());
                
                while(running) try {
                	Socket socket = forwardSocket.accept();
                	String clientAdress = socket.getInetAddress().getHostAddress() + socket.getPort();
                	Thread clientForwardThread = new Thread() {
                		
                	    private Socket clientSocket = socket;
                	    private Socket hostSocket = null;
                	    private String clientIP;
                	    private String hostIP;
                		
                		public void run() {
                	        try {
                	        	clientIP = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
                	 
                	            // Create a new socket connection to one of the servers from the list
                	        	//hostSocket = new Socket(playerSessionHostPair.get(clientIP));
                	  
                	            // Obtain input and output streams of server and client
                	            InputStream clientIn = clientSocket.getInputStream();
                	            OutputStream clientOut = clientSocket.getOutputStream();
                	            InputStream serverIn = hostSocket.getInputStream();
                	            OutputStream serverOut = hostSocket.getOutputStream();
                	  
                	            //hostIP = mServer.host + ":" + mServer.port;
                	            //mNakovForwardServer.log("TCP Forwarding  " + clientIP + " <--> " + hostIP + "  started.");
                	  
                	            // Start forwarding of socket data between server and client
                	            /*ForwardThread clientForward = new ForwardThread(this, clientIn, serverOut);
                	            ForwardThread serverForward = new ForwardThread(this, serverIn, clientOut);
                	            clientForward.start();
                	            serverForward.start();*/
                	 
                	        } catch (IOException ioe) {
                	           ioe.printStackTrace();
                	        }
                		}
                	};
                }
                
                catch (/*ClassNotFoundException | */IOException e) {
                	logger.error(ExceptionUtils.getStackTrace(e));
                    break;
                }
                
                /*in.close();
                out.close();
                clientSocket.close();*/
            }
            
            /*catch (IOException ex) {
                logger.debug(ExceptionUtils.getStackTrace(ex));
            }*/
		}
		
		public synchronized void addClientToHostConnection(String clientIP, String hostIP) {
			List<String> oldValues = sessionHostPlayerPair.containsKey(hostIP) ? manager.sessionServer.forwardServer.sessionHostPlayerPair.get(hostIP) : new ArrayList<String>();
			oldValues.add(clientIP);
			sessionHostPlayerPair.put(hostIP, oldValues);
			playerSessionHostPair.put(clientIP, hostIP);
		}
		
	}
}