package org.cs550.cis.api;

import java.util.List;
import java.util.Map;

import org.cs550.cis.registry.Registry;

/**
 * CentralIndexServerAPI provides functionality to manage the peer connected to
 * central index server
 * 
 * @author Aishwarya Anand <A20331867>
 * @mail aanand12@hawk.iit.edu
 */
public interface IndexServerAPI {

	/**
	 * Search the file names linked to all peer
	 * 
	 * @param keyword
	 *            - file names as well as keywords to search
	 * @return Map<K, List<V>> - Map where K combination of PeerIpaddress and
	 *         PeerServerPort List<V> - List of files present in peer directory
	 */
	<K, V> Map<K, List<V>> search(String keyword);

	/**
	 * Gives the list of all registry object connected to peer
	 * 
	 * @return List<T> list of peers connected, where T object of any type
	 */
	<T> List<T> showRegistry();

	/**
	 * Provides functionality to add peer to central index server
	 * 
	 * @param peerRegName
	 *            - key to added i.e combination of peer IP Address and peer
	 *            server port no
	 * @param peer
	 *            - Peer object
	 */
	<T> void registerPeer(String peerRegName, T peer);

	/**
	 * Removes peer from central index server
	 * 
	 * @param key
	 *            - peer id to remove
	 */
	void removePeer(String key);

	/**
	 * Looks for a peer in central index server peers list
	 * 
	 * @param key
	 *            - peer that we need to check in central index server
	 * @return true if peer is present else false
	 */
	boolean hasPeer(String key);

	/**
	 * Gives the peer object from central index server
	 * 
	 * @param key
	 *            - key i.e combination of peer IP Address and peer server port
	 *            no
	 * @return T - return Peer object.
	 */
	<T> T getPeer(String key);
	
	Map<String, Registry> getRegistryMap();
	
}
