package org.cs550.peer.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.cs550.peer.server.service.PeerToPeerConnectionService;



public class PeerServer implements Runnable{
	private int serverPort = 9001;
	public volatile boolean isRunning =false;
	protected ServerSocket serverSocket = null;
	LinkedBlockingQueue<Socket> connectionQueue = new LinkedBlockingQueue<Socket>();
	
	
	public PeerServer(int port) {
		this.serverPort=port;
	}
	
	public  String start(int port){
		String serverStatus = "";
		if(isRunning==false){
			initServer(port);
			isRunning=true;
			ConnectionManager connectionManager=new ConnectionManager();	
			serverStatus = "Starting Server....";
			while(isRunning){
				connectionManager.run();
				Socket connection;
				try {
					connection = serverSocket.accept();
					if(connection.isConnected()){
						connectionQueue.put(connection);
					}
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
		else{
			serverStatus= "Server is already Running"	;
		}
		return serverStatus;
	}
	
	private void initServer(int port){
		this.serverPort=port;
		openServerSocket(this.serverPort);
	}
	
	 private void openServerSocket(int port) {
	        try {
	            this.serverSocket = new ServerSocket(port);
	        } catch (IOException e) {
	            throw new RuntimeException("Cannot open port at port no "+port, e);
	        }
	    }
	 
	public class ConnectionManager implements Runnable{
			ExecutorService threadPool = Executors.newFixedThreadPool(10);
			
			public void run(){
				if(!connectionQueue.isEmpty()){
					threadPool.execute(new Runnable() {						
						@Override
						public void run() {	
							Socket peerConnection = connectionQueue.poll();
							new PeerToPeerConnectionService(peerConnection);
						}
					});
				}
			}
			
		}

	@Override
	public void run() {
		start(this.serverPort);	
	}
}
