package org.cs550.csi;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.cs550.cis.connector.CISServer;
import org.cs550.cis.connector.DISClientConnector;
import org.cs550.cis.exception.CentralIndexServerException;
import org.cs550.cis.registry.DecentralizedServerIndexServerRegistry;

public class ServerApplication {
	private Properties prop = null;
	private InputStream input = null;
	private static final String CONFIG_DIR = "conf//";
	private static final String CONFIG_FILE = "CentralIndexServer.properties";

	public static void main(String[] args) {
		ServerApplication server = new ServerApplication();
		server.init();
	}

	private void init() {
		this.prop = new Properties();
		try {
			this.input = new FileInputStream(CONFIG_DIR + CONFIG_FILE);
			this.prop.load(input);
			Thread th = new Thread(new CISServer(Integer.parseInt(getServerPort())));
			th.start();
			String bokerProps = this.prop.getProperty("brokerServer.info");
			if (bokerProps != null && !bokerProps.isEmpty()) {
				String[] bokers = bokerProps.split(",");
				for (int i = 0; i < bokers.length; i++) {
					System.out.println("For Broker"+bokers[i]);
				//	DecentralizedServerIndexServerRegistry.getindexServerRegistry().updateIndexServerRegistry(bokers[i]);
					String[] broker = bokers[i].split(":");
					Thread connector = new Thread(new DISClientConnector(broker[0], broker[1], getServerPort()));
					connector.start();
				}
			}
		} catch (IOException e) {
			System.out.println("Error While Reading Properties !!! Closing application");
			System.exit(0);
		}
	}

	private String getServerPort() {
		return getStringValue("server.port", "9000");
	}

	private String getStringValue(String key, String defaultValue) {
		String value = null;
		if (this.prop != null) {
			if (this.prop.get(key) != null) {
				value = this.prop.getProperty(key, defaultValue);
			}
		} else {
			System.out.println("Error While Reading Properties !!! Closing application");
			System.exit(0);
		}
		return value;
	}
}
