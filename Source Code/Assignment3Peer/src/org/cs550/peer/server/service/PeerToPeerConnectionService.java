package org.cs550.peer.server.service;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.cs550.commands.DownloadPeerFileCommand;
import org.cs550.commands.InitiateReplica;
import org.cs550.commands.MessageCommand;
import org.cs550.commands.PeerFileCommand;
import org.cs550.commands.ReplicateFilesCommand;
import org.cs550.commands.SendReplicaFilesCommand;
import org.cs550.peer.ApplicationGobalVariableHolder;

public class PeerToPeerConnectionService {
	private Socket connectedSocket;
	private OutputStream os;
	private ObjectOutputStream oos;
	private InputStream is;
	private volatile boolean isConnected;

	public PeerToPeerConnectionService(Socket connSocket) {
		this.connectedSocket = connSocket;
		downloadFile();

	}

	private void downloadFile() {
		if (this.connectedSocket.isConnected()) {
			try {
				while (this.connectedSocket.isConnected()) {
					this.is = this.connectedSocket.getInputStream();
					ObjectInputStream ois = new ObjectInputStream(is);
					Object obj = (Object) ois.readObject();
					if (obj != null && this.connectedSocket.isConnected()) {
						if (obj instanceof DownloadPeerFileCommand) {
							String filePath = ((DownloadPeerFileCommand) obj).getFileName();
							if (new File(filePath).exists()) {
								this.os = this.connectedSocket.getOutputStream();
								this.oos = new ObjectOutputStream(os);
								this.oos.writeObject(createPeerFile(filePath));
							} else {
								this.os = this.connectedSocket.getOutputStream();
								this.oos = new ObjectOutputStream(os);
								this.oos.writeObject(MessageCommand.P2P_FILE_NOT_FOUND_MSG);
							}

						} else if (obj instanceof InitiateReplica) {
							System.out.println("Seding Replica Request");
							Socket replicaSocket = new Socket(((InitiateReplica) obj).getIpAddressFrom(), ((InitiateReplica) obj).getPortNoForm());
							if (replicaSocket.isConnected()) {
								Long startTime = System.nanoTime();
								OutputStream oStream = replicaSocket.getOutputStream();
								ObjectOutputStream objectOutputStream = new ObjectOutputStream(oStream);
								objectOutputStream.writeObject(new ReplicateFilesCommand(((InitiateReplica) obj).getFileNames()));
								while (replicaSocket.isConnected()) {
									InputStream inputStream = replicaSocket.getInputStream();
									ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
									Object object = (Object) objectInputStream.readObject();
									if (object != null && this.connectedSocket.isConnected()) {
										if (object instanceof SendReplicaFilesCommand) {
											for (PeerFileCommand file : ((SendReplicaFilesCommand) object).getFileName()) {
												String fileName = file.getFilename();
												File dstFile = new File(ApplicationGobalVariableHolder.getInstance().getAppDir() + "/" + fileName);
												FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
												fileOutputStream.write(file.getFileData());
												fileOutputStream.flush();
												fileOutputStream.close();
											}
											System.out.println("total time" + (System.nanoTime() - startTime));
											System.out.println("Replica Created Succesfully");
											replicaSocket.close();
										}
									}
								}
							}
						} else if (obj instanceof ReplicateFilesCommand) {
							System.out.println("Creating Replica");
							List<String> filePaths = ((ReplicateFilesCommand) obj).getFileName();
							List<PeerFileCommand> replicaList = new ArrayList<>();
							for (String filePath : filePaths) {
								if (new File(filePath).exists()) {
									replicaList.add(createPeerFile(filePath));
								}
							}
							this.os = this.connectedSocket.getOutputStream();
							this.oos = new ObjectOutputStream(os);
							this.oos.writeObject(new SendReplicaFilesCommand(replicaList));
							this.connectedSocket.close();
						}
					} else if (obj instanceof PeerFileCommand) {
						String fileName = ((PeerFileCommand) obj).getFilename();
						File dstFile = new File(ApplicationGobalVariableHolder.getInstance().getAppDir() + "/" + fileName);
						FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
						fileOutputStream.write(((PeerFileCommand) obj).getFileData());
						fileOutputStream.flush();
						fileOutputStream.close();
						System.out.println("your donwload file : " + fileName + " is successfully saved at location "
								+ ApplicationGobalVariableHolder.getInstance().getAppDir());
					} else {

						this.os = this.connectedSocket.getOutputStream();
						this.oos = new ObjectOutputStream(os);
						this.oos.writeObject(MessageCommand.UNKNOWN_COMMAND_MSG);
					}
				}
			} catch (IOException | ClassNotFoundException e) {
				// System.out.println("Clossing Connection");
			}

		}
	}

	public PeerFileCommand createPeerFile(String path) {
		PeerFileCommand peerFileCommand = new PeerFileCommand();
		peerFileCommand.setFilename(path.substring(path.lastIndexOf("/") + 1, path.length()));
		DataInputStream diStream;
		try {
			diStream = new DataInputStream(new FileInputStream(path));

			File file = new File(path);
			long len = (int) file.length();
			byte[] fileBytes = new byte[(int) len];
			int read = 0;
			int numRead = 0;
			while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read, fileBytes.length - read)) >= 0) {
				read = read + numRead;
			}
			peerFileCommand.setFileData(fileBytes);
			peerFileCommand.setFileSize(len);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return peerFileCommand;
	}

	private String generatePeerName(String ipAddress, int port) {
		System.out.println("IpAddress" + ipAddress + " Port " + port);
		return ipAddress + ":" + port;
	}

	public boolean isAlive() {
		return this.connectedSocket.isConnected();
	}
}
