package org.cs550.commands;

public class FileLookUpCommand extends Commands {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8367315122814677386L;
	
	private String searchkeywords;
	
	private FileLookUpCommand() {
		// This command is immutable
	}
	
	public FileLookUpCommand(String searchkeywords){
		this.searchkeywords=searchkeywords;
	}

	public String getSearchkeywords() {
		return searchkeywords;
	}
	
	

}
