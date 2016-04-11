package org.cs550.commands;

import java.util.List;

public class InitiateReplica extends Commands {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3728455236852274694L;
	
	private List<String> fileNames;
	private String ipAddressFrom;
	private int portNoForm;

	private InitiateReplica() {
		// TODO Auto-generated constructor stub
	}

	public InitiateReplica(String ipAddressFrom, int portNoForm, List<String> fileNames) {
		this.fileNames = fileNames;
		this.ipAddressFrom = ipAddressFrom;
		this.portNoForm = portNoForm;
	}

	public List<String> getFileNames() {
		return fileNames;
	}

	public String getIpAddressFrom() {
		return ipAddressFrom;
	}

	public int getPortNoForm() {
		return portNoForm;
	}

}
