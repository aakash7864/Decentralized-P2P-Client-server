package org.cs550.cis.registry;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DecentralizedServerIndexServerRegistry {

	private static DecentralizedServerIndexServerRegistry instance = new DecentralizedServerIndexServerRegistry();

	List<Socket> indexServerSocket = new ArrayList<>();

	private DecentralizedServerIndexServerRegistry() {
		// TODO Auto-generated constructor stub
	}

	public static DecentralizedServerIndexServerRegistry getindexServerRegistry() {
		if (instance == null) {
			instance = new DecentralizedServerIndexServerRegistry();
		}
		return instance;
	}

	public void updateIndexServerRegistry(Socket serverKey) {
		this.indexServerSocket.add(serverKey);
	}

	public List<Socket> getIndexServerRegistry() {
		return Collections.unmodifiableList(this.indexServerSocket);
	}
}
