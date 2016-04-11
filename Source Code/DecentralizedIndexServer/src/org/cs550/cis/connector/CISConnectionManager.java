package org.cs550.cis.connector;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton class to that get all the peer from connection queue of server and
 * manage peer
 * 
 * @author Aishwarya Anand <A20331867>
 * @mail aanand12@hawk.iit.edu
 * 
 */
public class CISConnectionManager {

	private static CISConnectionManager intance = null;

	/**
	 * Hashmap to maintain peer connection
	 */
	private ConcurrentHashMap<Integer, PeerConnection> concurrentHashMap = new ConcurrentHashMap<>();

	/**
	 * Making default constructor private
	 */
	private CISConnectionManager() {
	}

	/**
	 * provides the functionality of singleton instance
	 * 
	 * @return CISConnectionManager - single instance of connection manager
	 */
	public static CISConnectionManager getConnectionManager() {
		if (intance == null) {
			intance = new CISConnectionManager();
		}
		return intance;
	}

	/**
	 * Adding Peer to connection manager
	 * 
	 * @param port
	 *            - connection port made by peer
	 * @param connection
	 *            - Peer object
	 */
	public void addConnection(int port, PeerConnection connection) {
		concurrentHashMap.put(port, connection);
	}

	/**
	 * Remove peer from connection manager
	 * 
	 * @param port
	 *            - connection port made by peer
	 */
	public void removeConnection(int port) {
		concurrentHashMap.remove(port);
	}
}
