package org.cs550.cis.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import org.cs550.cis.api.IndexServerAPI;
import org.cs550.cis.exception.CentralIndexServerException;
import org.cs550.cis.registry.DecentralizedServerIndexServerRegistry;
import org.cs550.commands.BradCastRegistryCommand;
import org.cs550.commands.MessageCommand;

public class RegistryBroadCastService implements Runnable {

	IndexServerAPI centralIndexServerAPI;

	public RegistryBroadCastService(IndexServerAPI centralIndexServerAPI) {
		this.centralIndexServerAPI = centralIndexServerAPI;
	}

	private void runRegistryBroadCast() {
		List<Socket> broadCastList = DecentralizedServerIndexServerRegistry.getindexServerRegistry().getIndexServerRegistry();
		for (Socket indexServer : broadCastList) {
			System.out.println("Running BradCast");
			if (indexServer.isConnected()) {
				try {
					OutputStream os = indexServer.getOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(os);
					oos.writeObject(new BradCastRegistryCommand(this.centralIndexServerAPI.getRegistryMap()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void run() {
		runRegistryBroadCast();

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
}
