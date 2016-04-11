package org.cs550.commands;

import java.util.List;

public class ReplicateFilesCommand extends Commands{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8578843625916238463L;
	private List<String> fileName;
	
	private ReplicateFilesCommand() {
		// TODO Auto-generated constructor stub
	}
	
	public ReplicateFilesCommand(List<String> fileName) {
		this.fileName=fileName;
	}

	public List<String> getFileName() {
		return fileName;
	}
	
}
