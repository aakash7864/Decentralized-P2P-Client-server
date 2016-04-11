package org.cs550.commands;

public class PeerFileCommand extends Commands {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6564404745011408439L;

	private String filename;
	private long fileSize;
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public byte[] getFileData() {
		return fileData;
	}
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}
	private byte[] fileData;

}
