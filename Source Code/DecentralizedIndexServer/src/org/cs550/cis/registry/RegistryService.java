package org.cs550.cis.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RegistryService {

	Map<String, Registry> registryMap = new HashMap<>();

	public void addRegistry(String peerRegName, Registry registry) {
		this.registryMap.put(peerRegName, registry);
	}

	public Registry getRegistry(String ipeerRegName) {
		return this.registryMap.get(ipeerRegName);
	}

	public void deleteRegistry(String peerRegName) {
		for (Iterator<Entry<String, Registry>> it = registryMap.entrySet()
				.iterator(); it.hasNext();) {
			Entry<String, Registry> entry = it.next();
			if (entry.getKey().equals(peerRegName)) {
				it.remove();
			}
		}
	}

	public List<Registry> getAllRegistry() {
		List<Registry> registryList = new ArrayList<>();
		for (Registry registry : registryMap.values()) {
			registryList.add(registry);
		}
		return registryList;
	}
	
	public Map<String, Registry> getRegisMap(){
		return Collections.unmodifiableMap(this.registryMap);
	}
	
}
