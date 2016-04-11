package org.cs550.commands;

import java.util.HashMap;
import java.util.Map;

import org.cs550.cis.registry.Registry;

public class BradCastRegistryCommand extends Commands {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2175030566603883212L;
	Map<String, Registry> registryMap = new HashMap<String, Registry>();

	public BradCastRegistryCommand(Map<String, Registry> registryMap) {
		this.registryMap = registryMap;

	}

	public Map<String, Registry> getRegistryMap() {
		return this.registryMap;
	}

}
