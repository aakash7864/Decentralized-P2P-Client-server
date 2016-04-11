package org.cs550.commands;


public class IndexServerRegistryCommand extends Commands {


	private static final long serialVersionUID = -608419239823421711L;
	
	private String ServerIpAddressAndPortNo;

	public IndexServerRegistryCommand(String string) {
		this.ServerIpAddressAndPortNo = string;

	}

	public String getServerIpAddressAndPortNo() {
		return ServerIpAddressAndPortNo;
	}

}
