package org.cs550.commands;

public class DownloadPeerFileCommand extends Commands{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5034259547045752486L;
	private String fileName;
	
	private DownloadPeerFileCommand() {
		// TODO Auto-generated constructor stub
	}
	
	public DownloadPeerFileCommand(String fileName) {
		this.fileName=fileName;
	}

	public String getFileName() {
		return fileName;
	}
	
}
