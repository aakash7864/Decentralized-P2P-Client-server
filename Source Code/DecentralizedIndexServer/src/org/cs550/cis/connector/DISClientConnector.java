package org.cs550.cis.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;

import org.cs550.cis.exception.CentralIndexServerException;
import org.cs550.cis.registry.DecentralizedServerIndexServerRegistry;
import org.cs550.commands.IndexServerRegistryCommand;
import org.cs550.commands.MessageCommand;

public class DISClientConnector implements Runnable{

	String ipAddress;
	String portno;
	String localPortNo;

	public DISClientConnector(String ipAddress, String portNo,String localPortNo) {
		this.ipAddress = ipAddress;
		this.portno = portNo;
		this.localPortNo=localPortNo;
	}

	public void init(String ipAddress, String portNo, String localPortNo) throws CentralIndexServerException {
		
		Socket connectedSoc = connectToServerSocket(ipAddress, portNo);
		InputStream is;
		try {
			is = connectedSoc.getInputStream();
			ObjectInputStream ois = new ObjectInputStream(is);
			Object serverResp = (Object) ois.readObject();
			if (serverResp != null) {
				if (serverResp instanceof MessageCommand) {
					System.out.println(":: Server Response ::");
					System.out.println(((MessageCommand) serverResp).getMessage());
					OutputStream os = connectedSoc.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					oos.writeObject(new IndexServerRegistryCommand(Inet4Address.getLocalHost().getHostName()+":"+ localPortNo));
					System.out.println("Sended Request");
					DecentralizedServerIndexServerRegistry.getindexServerRegistry().updateIndexServerRegistry(connectedSoc);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Socket connectToServerSocket(String serverAddress, String port) throws CentralIndexServerException {
		Socket socket = null;
		try {
			socket = new Socket(serverAddress, Integer.parseInt(port));
		} catch (NumberFormatException e) {
			throw new CentralIndexServerException("Please Check the port No", e.getCause());
		} catch (UnknownHostException e) {
			throw new CentralIndexServerException("Host not found", e.getCause());
		} catch (IOException e) {
			throw new CentralIndexServerException("Socket closed ", e.getCause());
		}
		return socket;
	}

	@Override
	public void run() {
		try {
			init(ipAddress, portno, localPortNo);
		} catch (CentralIndexServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
