package org.cs550.cis.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.cs550.commands.Commands;

/**
 * Registry class that keeps all the information related to peer
 * 
 */
public final class Registry extends Commands {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4150033655352128303L;
	/**
	 * List of file names present in that peer
	 */
	private List<String> fileNames = new ArrayList<String>();
	private String peerName;
	private String registeredIp;
	private String portNo;
	private int noOfReplicaNeeded;
	private List<String> replicaInfo = new ArrayList<>();

	/**
	 * Making default Constructor as private
	 */
	private Registry() {

	}

	public int getNoOfReplicaNeeded() {
		return noOfReplicaNeeded;
	}

	/**
	 * Constructor for Registry creation
	 * 
	 * @param peerName
	 *            - Peer Name
	 * @param fileNames
	 *            - File Names present in that Peer
	 * @param registeredIp
	 *            - Registered IP Address of Peer
	 * @param portNo
	 *            - Port No of Peer
	 */
	public Registry(String peerName, List<String> fileNames, String registeredIp, String portNo, int noOfReplicaNeeded) {
		super();
		this.fileNames = fileNames;
		this.peerName = peerName;
		this.registeredIp = registeredIp;
		this.portNo = portNo;
		this.noOfReplicaNeeded = noOfReplicaNeeded;
	}

	/**
	 * Method that provide the functionality to update the files linked to peer
	 * 
	 * @param fileNames
	 *            - new files that needed to be linked
	 */
	public void UpdateFileNames(final List<String> fileNames) {
		this.fileNames = null;
		this.fileNames = fileNames;
	}

	/**
	 * Getter method to get file name list
	 * 
	 * @return list of files in that peer
	 */
	public List<String> getFileNames() {
		return Collections.unmodifiableList(fileNames);
	}

	/**
	 * Getter method for peer
	 * 
	 * @return Peer Name
	 */
	public String getPeerName() {
		return peerName;
	}

	/**
	 * Getter method for RegisteredIp
	 * 
	 * @return Registerd IP address of Peer
	 */
	public String getRegisteredIp() {
		return registeredIp;
	}

	/**
	 * Getter Method for portNo
	 * 
	 * @return portNo
	 */
	public String getPortNo() {
		return portNo;
	}

	public List<String> getReplicaInfo() {
		return (List<String>) Collections.unmodifiableList(replicaInfo);
	}

	public void addReplica(String key) {
		this.replicaInfo.add(key);
	}
}
