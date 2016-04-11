package org.cs550.peer;

import java.net.Socket;

public class ConnectionHolder {

	private static ConnectionHolder instance = null;

	Socket[] severSockets = null;
	
	private ConnectionHolder() {

	}

	private ConnectionHolder(Socket[] severSockets) {
		this.severSockets = severSockets;
	}

	public static ConnectionHolder initConnectionHolder(Socket[] severSockets) {
		if (instance == null) {
			instance = new ConnectionHolder(severSockets);
		}
		return instance;
	}

	public static Socket[] getConnectionSockets() {
		if (instance != null) {
			return instance.severSockets;
		} else {
			return null;
		}
	}

	public static Socket getConnectionSocket(int index) {
		if (instance != null) {
			return instance.severSockets[index];
		} else {
			return null;
		}
	}

}
