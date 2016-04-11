package org.cs550.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterPeerCommand extends Commands {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8161585864718564981L;
	private String peerName;
	private int peerServerPort;
	private List<String> fileList;
	private int noOfReplication;
	private String peerIpAddress;

	public RegisterPeerCommand(String peerName, int peerServerPort, List<String> fileList, int noOfReplication, String peerIpAddress) {
		this.peerName = peerName;
		this.fileList = fileList;
		this.peerServerPort = peerServerPort;
		this.noOfReplication = noOfReplication;
		this.peerIpAddress = peerIpAddress;
	}

	public int getNoOfReplication() {
		return noOfReplication;
	}

	public String getPeerIpAddress() {
		return peerIpAddress;
	}

	public List<String> getFileList() {
		return fileList;
	}

	public String peerName() {
		return this.peerName;
	}

	public int getPeerServerPort() {
		return this.peerServerPort;
	}
}
