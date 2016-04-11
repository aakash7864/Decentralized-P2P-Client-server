package org.cs550.peer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

import org.cs550.peer.exception.Peer2PeerException;

public class Application {

	private Properties prop = null;
	private InputStream input = null;
	private static final String CONFIG_DIR = "conf//";
	private static final String CONFIG_FILE = "peer2peer.properties";

	public static void main(String[] args) {
		Application app = new Application();
		app.init();
	}

	private void init() {
		this.prop = new Properties();
		try {
			
			System.out.println(CONFIG_DIR + CONFIG_FILE);
			this.input = new FileInputStream(CONFIG_DIR + CONFIG_FILE);
			this.prop.load(input);
			ApplicationGobalVariableHolder.getInstance().setAppDir(this.prop.getProperty("client.dir"));
			ApplicationGobalVariableHolder.getInstance().setRelicationNo(Integer.parseInt((this.prop.getProperty("replica.no"))));
			ApplicationGobalVariableHolder.getInstance().setFilesToRunPerformance(this.prop.getProperty("perfromanceFile.name").split(","));;
			ApplicationGobalVariableHolder.getInstance().setPerformacedir(this.prop.getProperty("perfromanceFile.dir"));
			
			try {
				String indexServerInfo = this.prop.getProperty("indexserver.info");
				if (indexServerInfo != null && !indexServerInfo.isEmpty()) {
					String[] indexServers = indexServerInfo.split(",");
					Socket[] indexServerSockets = new Socket[indexServers.length];
					for (int i = 0; i < indexServers.length; i++) {
						String[] indexServer = indexServers[i].split(":");
						indexServerSockets[i] = connectToServerSocket(indexServer[0], indexServer[1]);
					}
					ConnectionHolder.initConnectionHolder(indexServerSockets);
				}
			} catch (Peer2PeerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			DistributedHashMapImpl distributedHashMapImpl = new DistributedHashMapImpl();
			String clientName = this.prop.getProperty("client.name");
			if (clientName != null && !clientName.isEmpty()) {
				int hashvalue=distributedHashMapImpl.hash(clientName);
//				for (int i=0;i<ConnectionHolder.getConnectionSockets().length;i++){
//					if(i!=hashvalue){
//						ConnectionHolder.getConnectionSocket(i).close();
//					}
//				}
				Peer2PeerApp.startApp(ConnectionHolder.getConnectionSocket(hashvalue), getLocalServerPort(),clientName);
			}
		} catch (IOException e) {
			System.out.println("Error While Reading Properties !!! Closing application");
			System.exit(0);
		}
	}

	private String getLocalServerPort() {
		return getStringValue("localserver.port", "9001");
	}

	private String getCentralIndexServerPort() {
		return getStringValue("indexserver.port", "9000");
	}

	private String getCentralIndexServerAddress() {
		return getStringValue("indexserver.address", "localhost");
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

	private Socket connectToServerSocket(String serverAddress, String port) throws Peer2PeerException {
		Socket socket = null;
		try {
			socket = new Socket(serverAddress, Integer.parseInt(port));
		} catch (NumberFormatException e) {
			throw new Peer2PeerException("Please Check the port No", e.getCause());
		} catch (UnknownHostException e) {
			throw new Peer2PeerException("Host not found", e.getCause());
		} catch (IOException e) {
			throw new Peer2PeerException("Socket closed ", e.getCause());
		}
		return socket;
	}
}
