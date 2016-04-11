package org.cs550.commands;

import java.util.List;

public class UpdatePeerFilesCommand extends Commands {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1782002642466982632L;
	private List<String> fileList;

	public UpdatePeerFilesCommand(List<String> fileList) {
		this.fileList = fileList;
	}

	public List<String> getFileList() {
		return fileList;
	}

}
