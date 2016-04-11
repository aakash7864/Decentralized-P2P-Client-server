package org.cs550.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LookUpResponseCommand extends Commands {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6595239813300587L;

	private Map<String, List<String>> lookUpResponse = new HashMap<String, List<String>>();

	private LookUpResponseCommand() {
		// TODO Auto-generated constructor stub
	}

	public LookUpResponseCommand(Map<String, List<String>> lookUpResponse) {
		this.lookUpResponse = lookUpResponse;
	}

	public Map<String, List<String>> getlookUpReult() {
		return Collections.unmodifiableMap(this.lookUpResponse);
	}

}
