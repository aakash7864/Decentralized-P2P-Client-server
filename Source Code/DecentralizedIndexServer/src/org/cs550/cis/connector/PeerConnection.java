package org.cs550.cis.connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.cs550.cis.api.IndexServerAPI;
import org.cs550.cis.exception.CentralIndexServerException;
import org.cs550.cis.registry.DecentralizedServerIndexServerRegistry;
import org.cs550.cis.registry.Registry;
import org.cs550.cis.services.RegistryBroadCastService;
import org.cs550.commands.BradCastRegistryCommand;
import org.cs550.commands.FileLookUpCommand;
import org.cs550.commands.IndexServerRegistryCommand;
import org.cs550.commands.InitiateReplica;
import org.cs550.commands.LookUpResponseCommand;
import org.cs550.commands.MessageCommand;
import org.cs550.commands.RegisterPeerCommand;
import org.cs550.commands.UpdatePeerFilesCommand;

public class PeerConnection {
	private Socket connectedSocket;
	private OutputStream os;
	private InputStream is;
	private IndexServerAPI centralIndexServerAPI;
	private String ipAddress;
	private int conenctionPort;
	private int peerServerPort;
	private volatile boolean isConnected;

	public PeerConnection(Socket connSocket, IndexServerAPI centralIndexServerAPI) {
		try {
			init(connSocket, centralIndexServerAPI);
			start();
		} catch (CentralIndexServerException e) {
			System.out.println(e.getMessage());
		}
	}

	private void init(Socket connSocket, IndexServerAPI centralIndexServerAPI) throws CentralIndexServerException {
		this.connectedSocket = connSocket;
		this.isConnected = this.connectedSocket.isConnected();
		this.ipAddress = this.connectedSocket.getInetAddress().getHostAddress();
		this.conenctionPort = this.connectedSocket.getPort();
		this.centralIndexServerAPI = centralIndexServerAPI;
		try {
			this.os = this.connectedSocket.getOutputStream();
			this.is = this.connectedSocket.getInputStream();
		} catch (IOException e) {
			this.isConnected = false;
			CISConnectionManager.getConnectionManager().removeConnection(this.conenctionPort);
			throw new CentralIndexServerException("There seems like problem happened cloosing connection");
		}

	}

	private void start() throws CentralIndexServerException {
		if (isConnected()) {
			writeMessageCommand();
			while (isConnected()) {
				// Long startTime=System.nanoTime();
				Object obj = readObjectFromSocket();
				if (obj != null && isConnected()) {
					if (obj instanceof RegisterPeerCommand) {
						registerPeerConnection(obj);

						ExecutorService exec = Executors.newFixedThreadPool(2);
						exec.execute(new RegistryBroadCastService(centralIndexServerAPI));
					} else if (obj instanceof FileLookUpCommand) {
						peerFileLookUp(obj);
					} else if (obj instanceof UpdatePeerFilesCommand) {
						// System.out.println("Inside Update Command");
						updatePeerFiles((UpdatePeerFilesCommand) obj);
					} else if (obj instanceof IndexServerRegistryCommand) {
						DecentralizedServerIndexServerRegistry DeIndexServer = DecentralizedServerIndexServerRegistry.getindexServerRegistry();
						DeIndexServer.updateIndexServerRegistry(this.connectedSocket);
						String[] tokens = ((IndexServerRegistryCommand) obj).getServerIpAddressAndPortNo().split(":");

						if (tokens.length == 2) {
							Thread connector = new Thread(new DISClientConnector(tokens[0], tokens[1], ""));
							connector.start();
						}
						System.out.println("Updated IndexServer Registry");
					} else if (obj instanceof BradCastRegistryCommand) {
						System.out.println("Updating Registry");
						Map<String, Registry> brokerregistry = ((BradCastRegistryCommand) obj).getRegistryMap();
//						System.out.println("********************Updating Registry");
//						System.out.println("BoradCasted Registry" + brokerregistry);
//						System.out.println("Local Registry" + this.centralIndexServerAPI.getRegistryMap());
						for (Entry<String, Registry> braodCastEntry : brokerregistry.entrySet()) {
							if (!this.centralIndexServerAPI.hasPeer(braodCastEntry.getKey())) {
								this.centralIndexServerAPI.registerPeer(braodCastEntry.getKey(), braodCastEntry.getValue());
								runReplicationJob();
							}
						}
//						try {
////							this.connectedSocket.close();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						System.out.println("Updated Local Registry" + this.centralIndexServerAPI.getRegistryMap());
					}
				}
			}
			// System.out.println("Total responseTime "+
			// peerServerPort+" "+(System.nanoTime()-startTime));
		}
	}

	private void updatePeerFiles(UpdatePeerFilesCommand updatePeerFilesCommand) throws CentralIndexServerException {
		if (this.centralIndexServerAPI.hasPeer(generatePeerName(getConnAddress(), this.peerServerPort))) {
			Registry peerReg = this.centralIndexServerAPI.getPeer(generatePeerName(getConnAddress(), this.peerServerPort));
			peerReg.UpdateFileNames(updatePeerFilesCommand.getFileList());
			for (String iterable_element : updatePeerFilesCommand.getFileList()) {
				System.out.println(iterable_element);
			}
		}
	}

	private Object readObjectFromSocket() throws CentralIndexServerException {
		try {
			ObjectInputStream ois = new ObjectInputStream(this.is);
			return (Object) ois.readObject();
		} catch (ClassNotFoundException e) {
			throw new CentralIndexServerException("Error While reading message Comand does not exist in Server");
		} catch (IOException e) {
			this.isConnected = false;
//			CISConnectionManager.getConnectionManager().removeConnection(conenctionPort);
//			if (this.centralIndexServerAPI.hasPeer(generatePeerName(getConnAddress(), this.peerServerPort))) {
//				this.centralIndexServerAPI.removePeer(generatePeerName(getConnAddress(), this.peerServerPort));
//			}
			throw new CentralIndexServerException("Error while reading message from peer closing connection");
		}
	}

	private void writeMessageCommand() throws CentralIndexServerException {

		try {
			ObjectOutputStream oos = new ObjectOutputStream(this.os);
			oos.writeObject(MessageCommand.SERVER_RESP_CONN_ESTABLISHED);
			oos.flush();
		} catch (IOException e) {
			this.isConnected = false;
			CISConnectionManager.getConnectionManager().removeConnection(conenctionPort);
			throw new CentralIndexServerException("Error While writing message");
		}

	}

	private void writeMessageCommand(MessageCommand messageCommand) throws CentralIndexServerException {

		try {
			ObjectOutputStream oos = new ObjectOutputStream(this.os);
			oos.writeObject(messageCommand);
			oos.flush();
		} catch (IOException e) {
			this.isConnected = false;
			CISConnectionManager.getConnectionManager().removeConnection(conenctionPort);
			throw new CentralIndexServerException("Error While writing message");
		}

	}

	private void peerFileLookUp(Object obj) throws CentralIndexServerException {
		Map<String, List<String>> serverLookUpResp = centralIndexServerAPI.search(((FileLookUpCommand) obj).getSearchkeywords());
		try {
			ObjectOutputStream oos = new ObjectOutputStream(this.os);
			oos.writeObject(new LookUpResponseCommand(serverLookUpResp));
		} catch (IOException e) {
			this.centralIndexServerAPI.removePeer(generatePeerName(getConnAddress(), this.peerServerPort));
			CISConnectionManager.getConnectionManager().removeConnection(conenctionPort);
			this.isConnected = false;
			throw new CentralIndexServerException("Error While File Look up Removing Peer Connection Closed");
		}
	}

	private void registerPeerConnection(Object obj) throws CentralIndexServerException {
		String peerKey = generatePeerName(getConnAddress(), getPeerServerPort((RegisterPeerCommand) obj));
		centralIndexServerAPI.registerPeer(peerKey, createNewRegistry((RegisterPeerCommand) obj));
		System.out.println("Registry Created ");
		runReplicationJob();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(this.os);
			oos.writeObject(MessageCommand.SERVER_RESP_REGISTRY_SUCCESS_MSG);
			oos.flush();

		} catch (IOException e) {
			CISConnectionManager.getConnectionManager().removeConnection(conenctionPort);
			throw new CentralIndexServerException("Error while registring peer closing Connection");
		}
	}

	private void runReplicationJob() {
		Map<String, Registry> brokerregistry = this.centralIndexServerAPI.getRegistryMap();
		if (brokerregistry.size() > 1) {
			System.out.println("Running Replica Job");
			for (Entry<String, Registry> braodCastEntry : brokerregistry.entrySet()) {
				for (Entry<String, Registry> localreg : this.centralIndexServerAPI.getRegistryMap().entrySet()) {
//					System.out.println("Local Key " + localreg.getKey() + "BoradCast Key " + braodCastEntry.getKey());
					if (!localreg.getKey().equals(braodCastEntry.getKey())) {
						if (localreg.getValue().getNoOfReplicaNeeded() >= localreg.getValue().getReplicaInfo().size()) {
//							System.out.println("Needs Replica");
							localreg.getValue().addReplica(braodCastEntry.getKey());
							String[] broadtokens = braodCastEntry.getKey().split(":");
							try {
								String[] localTokens = localreg.getKey().split(":");
//								System.out.println(Integer.parseInt(broadtokens[1]));
								Socket replicaSocketCommand = new Socket(broadtokens[0], Integer.parseInt(broadtokens[1]));
								if (replicaSocketCommand.isConnected()) {
									OutputStream oStream = replicaSocketCommand.getOutputStream();
									ObjectOutputStream objectOutputStream = new ObjectOutputStream(oStream);
									objectOutputStream.writeObject(new InitiateReplica(localTokens[0], Integer.parseInt(localTokens[1]), localreg.getValue()
											.getFileNames()));
									replicaSocketCommand.close();
								}
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (UnknownHostException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	private boolean isConnected() {
		this.isConnected = this.connectedSocket.isConnected();
		return this.isConnected;
	}

	private int getPeerServerPort(RegisterPeerCommand registerPeerCommand) {
		return registerPeerCommand.getPeerServerPort();
	}

	private String getConnAddress() {
		return this.ipAddress;
	}

	private Registry createNewRegistry(RegisterPeerCommand registerPeerCommand) {
		this.peerServerPort = registerPeerCommand.getPeerServerPort();
		return new Registry(registerPeerCommand.peerName(), registerPeerCommand.getFileList(), getConnAddress(), String.valueOf(registerPeerCommand
				.getPeerServerPort()), registerPeerCommand.getNoOfReplication());
	}

	private String generatePeerName(String ipAddress, int port) {
		System.out.println("IpAddress" + ipAddress + " Port " + port);
		return ipAddress + ":" + port;
	}

	public boolean isAlive() {
		return isConnected();
	}
}
