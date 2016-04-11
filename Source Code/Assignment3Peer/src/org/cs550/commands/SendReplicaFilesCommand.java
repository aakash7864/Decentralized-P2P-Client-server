package org.cs550.commands;

import java.util.List;

public class SendReplicaFilesCommand extends Commands {

	/**
		 * 
		 */
	private static final long serialVersionUID = 1539687747271201032L;
	/**
		 * 
		 */

	private List<PeerFileCommand> fileNames;

	private SendReplicaFilesCommand() {
		// TODO Auto-generated constructor stub
	}

	public SendReplicaFilesCommand(List<PeerFileCommand> fileNames) {
		this.fileNames = fileNames;
	}

	public List<PeerFileCommand> getFileName() {
		return fileNames;
	}

}
