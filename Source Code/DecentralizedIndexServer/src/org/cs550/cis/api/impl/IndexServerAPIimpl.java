package org.cs550.cis.api.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cs550.cis.api.IndexServerAPI;
import org.cs550.cis.registry.Registry;
import org.cs550.cis.registry.RegistryService;

/**
 * An Implementation class for Central index Server API
 * 
 * @see org.cs550.cis.api.IndexServerAPI
 * 
 * @author Aishwarya Anand <A20331867>
 * @mail aanand12@hawk.iit.edu
 */
public class IndexServerAPIimpl implements IndexServerAPI {

	private RegistryService registryservice = null;

	public IndexServerAPIimpl() {
		this.registryservice = new RegistryService();
	}

	/**
	 * 
	 * @see org.cs550.cis.api.IndexServerAPI#search(java.lang.String)
	 */
	@Override
	public Map<String, List<String>> search(String keyword) {
		Map<String, List<String>> lookupMap = new HashMap<String, List<String>>();
		List<String> lookUpResult = new ArrayList<String>();
		boolean hasFile = false;
		List<org.cs550.cis.registry.Registry> registryList = this.showRegistry();
		for (org.cs550.cis.registry.Registry registry : registryList) {
			if (registry.getFileNames().size() > 0) {
				for (String file : registry.getFileNames()) {
					if (file.contains(keyword)) {
						lookUpResult.add(file);
						hasFile = true;
					}
				}
				if (hasFile) {
					String relicainfo = "";
					for (String replica : registry.getReplicaInfo()) {
						relicainfo = "#" + replica;
					}
					;
					lookupMap.put(registry.getRegisteredIp() + ":" + registry.getPortNo() + relicainfo, lookUpResult);
					hasFile = false;
				}
			}
		}
		return lookupMap;
	}

	/**
	 * @see org.cs550.cis.api.IndexServerAPI#showRegistry()
	 */
	@Override
	public <Registry> List<Registry> showRegistry() {
		return (List<Registry>) this.registryservice.getAllRegistry();
	}

	/**
	 * @see org.cs550.cis.api.IndexServerAPI#registerPeer(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public <Registry> void registerPeer(String peerRegName, Registry peer) {
		this.registryservice.addRegistry(peerRegName, (org.cs550.cis.registry.Registry) peer);

	}

	/**
	 * @see org.cs550.cis.api.IndexServerAPI#removePeer(java.lang.String)
	 */
	@Override
	public void removePeer(String key) {
		this.registryservice.deleteRegistry(key);
	}

	/**
	 * @see org.cs550.cis.api.IndexServerAPI#hasPeer(java.lang.String)
	 */
	@Override
	public boolean hasPeer(String key) {
		boolean hasPeer = false;
		if (this.registryservice.getRegistry(key) != null) {
			hasPeer = true;
		}
		return hasPeer;
	}

	/**
	 * @see org.cs550.cis.api.IndexServerAPI#getPeer(java.lang.String)
	 */
	@Override
	public <Registry> Registry getPeer(String key) {
		return (Registry) this.registryservice.getRegistry(key);
	}

	public Map<String, Registry> getRegistryMap() {
		return this.registryservice.getRegisMap();
	}

}
