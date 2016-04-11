package org.cs550.peer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.cs550.commands.Commands;
import org.cs550.commands.DownloadPeerFileCommand;
import org.cs550.commands.FileLookUpCommand;
import org.cs550.commands.LookUpResponseCommand;
import org.cs550.commands.MessageCommand;
import org.cs550.commands.PeerFileCommand;
import org.cs550.commands.RegisterPeerCommand;
import org.cs550.peer.exception.Peer2PeerException;
import org.cs550.peer.server.PeerServer;
import org.cs550.peer.server.service.FileUpdateService;
import org.cs550.peer.util.PeerUtils;

public class Peer2PeerApp {

	private static Peer2PeerApp instance = null;
	private String peerServerPort;
	private String appDir;
	private String clientName;
	private int noOfReplica;
	private String[] toRunPerformanceFile;
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	private Peer2PeerApp() {

	}

	private Peer2PeerApp(Socket indexServerSocket, String peerServerPort, String clientName) {
		// Making this private to create static factory method
		try {
			this.appDir = ApplicationGobalVariableHolder.getInstance().getAppDir();
			this.noOfReplica = ApplicationGobalVariableHolder.getInstance().getRelicationNo();
			this.toRunPerformanceFile = ApplicationGobalVariableHolder.getInstance().getFilesToRunPerformance();
			init(indexServerSocket, peerServerPort);
			this.clientName = clientName;
		} catch (Peer2PeerException e) {
			System.out.println(e.getMessage());
		}
	}

	public static Peer2PeerApp startApp(Socket indexServerSocket, String peerServerPort, String clientName) {
		if (instance == null) {
			instance = new Peer2PeerApp(indexServerSocket, peerServerPort, clientName);
		}
		return instance;
	}

	private void init(Socket serverSocket, String peerServerPort) throws Peer2PeerException {
		this.peerServerPort = peerServerPort;
		if (serverSocket != null && serverSocket.isConnected()) {
			System.out.println("Peer Application Started.\nYou are connected to central index server " + serverSocket.getInetAddress().getHostAddress()
					+ " port no :" + serverSocket.getPort() + "\nYour local server is running on Port no :" + peerServerPort);
			startPeerServer(Integer.parseInt(peerServerPort));
			ServerResponseMessage(serverSocket);
			sendServerCommand(serverSocket, createPeerRegisterCommand());
			ServerResponseMessage(serverSocket);
			executor.scheduleAtFixedRate(new FileUpdateService(this.appDir, PeerUtils.getFileListFromDir(appDir), serverSocket), 1, 2, TimeUnit.MINUTES);
			while (serverSocket.isConnected()) {
				int menuOption = printMenu();
				if (menuOption == 1) {
					sendServerCommand(serverSocket, new FileLookUpCommand(getSerachKeyword()));
					ServerResponseMessage(serverSocket);
					
				} else if (menuOption == 2) {
					Long searchstartTime = System.nanoTime();
					handlefileDonwload(serverSocket);
					System.out.println("Total Time for Search in nanosec" + (System.nanoTime() - searchstartTime));
					
				} else if (menuOption == 3) {
					System.out.println("Running Performance for 10000 Operation");
					Long searchstartTime = System.nanoTime();
					for (int i = 0; i < 1000; i++) {
						for (int j = 0; j < this.toRunPerformanceFile.length; j++) {
							sendServerCommand(serverSocket, new FileLookUpCommand(toRunPerformanceFile[j]));
							ServerResponseMessage_test(serverSocket);
						}
					}
					System.out.println("Total Time for Search in nanosec" + (System.nanoTime() - searchstartTime));
					Long downloadstartTime = System.nanoTime();
					for (int i = 0; i < 1000; i++) {
						for (int j = 0; j < this.toRunPerformanceFile.length; j++) {
							handlefileDonwload(serverSocket, toRunPerformanceFile[j]);
						}
					}
					System.out.println("Total Time for Donwload in nanosec" + (System.nanoTime() - downloadstartTime));
				} else if (menuOption == 4) {
					List<String> peerFileList = getDirInfoFromProps(ApplicationGobalVariableHolder.getInstance().getPerformacedir());
					Long searchstartTime = System.nanoTime();
					for (String string : peerFileList) {
						sendServerCommand(serverSocket, new FileLookUpCommand(string));
						ServerResponseMessage_test(serverSocket);
					}
					System.out.println("Total Time for Search in nanosec" + (System.nanoTime() - searchstartTime));
					Long downloadstartTime = System.nanoTime();
					for (String string : peerFileList) {
					System.out.println(string);
						handlefileDonwload(serverSocket, string);
					}
					System.out.println("Total Time for Donwload in nanosec" + (System.nanoTime() - downloadstartTime));
				} else if (menuOption == 5) {
					System.out.println("Closing Application ");
					System.exit(0);
				} else {
					System.out.println("Invalid Menu Option Please Try Again!");
				}
			}

		} else {
			System.out.println("Unable to Start Server");
		}
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

	private void startPeerServer(int port) {
		Thread peerServer = new Thread(new PeerServer(port));
		peerServer.start();
	}

	private RegisterPeerCommand createPeerRegisterCommand() {
		return new RegisterPeerCommand(this.clientName, Integer.parseInt(peerServerPort), getDirInfoFromProps(this.appDir), this.noOfReplica, getIpAddress());
	}

	private void sendServerCommand(Socket socket, Commands cmd) throws Peer2PeerException {
		OutputStream os;
		try {
			os = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(cmd);
		} catch (IOException e) {
			throw new Peer2PeerException("Error while sending request to server", e.getCause());
		}
	}

	@SuppressWarnings("resource")
	private List<String> getDirInfo() {
		String peerDir = "";
		Scanner scanIn;
		boolean isNotDirValid = true;
		while (isNotDirValid) {
			System.out.println("Please Enter your Directory Locaiton");
			scanIn = new Scanner(System.in);
			peerDir = scanIn.nextLine();
			if (peerDir == "\\") {
				System.out.println("Invalid Directory. Please try again");
			} else {
				File folderexistes = new File(peerDir);
				if (folderexistes.exists() && folderexistes.isDirectory()) {
					isNotDirValid = false;
				} else {
					System.out.println("Invalid Directory. Please try again");
				}
			}
		}
		System.out.println("Peer Dir " + peerDir);
		ApplicationGobalVariableHolder.getInstance().setAppDir(peerDir);
		;
		System.out.println("Global Variable " + ApplicationGobalVariableHolder.getInstance().getAppDir());
		this.appDir = peerDir;
		return PeerUtils.getFileListFromDir(peerDir);
	}

	@SuppressWarnings("resource")
	private List<String> getDirInfoFromProps(String dir) {

		return PeerUtils.getFileListFromDir(dir);
	}

	private int setupNoOfReplica() {
		System.out.println("Please Enter No of Replica you want to create (ex: 2)");
		Scanner peerScanIn = new Scanner(System.in);
		return peerScanIn.nextInt();
	}

	private int printMenu() {
		int menu = 0;
		System.out.println("Please Select Menu:");
		System.out.println("1::: Search File IN CI Server");
		System.out.println("2::: Donwload File From Peer");
		System.out.println("3::: Run Performance Test1");
		System.out.println("4::: Run Performance Test1");
		System.out.println("5::: Quit");
		try {
			Scanner menuIn = new Scanner(System.in);
			menu = menuIn.nextInt();
		} catch (InputMismatchException e) {

		}
		return menu;
	}

	private String getSerachKeyword() {
		System.out.println("Please Enter File Name to search:");
		Scanner fileScanIn = new Scanner(System.in);
		return fileScanIn.nextLine();
	}

	private void handlefileDonwload(Socket clientSocket) throws Peer2PeerException {
		String peerIpAdd, downloadfile;
		downloadfile = getFileDownloadPath();
		sendServerCommand(clientSocket, new FileLookUpCommand(downloadfile));
		LookUpResponseCommand searchResult = searchResponseMessage(clientSocket);
		if (searchResult != null) {
			for (Map.Entry<String, List<String>> entry : ((LookUpResponseCommand) searchResult).getlookUpReult().entrySet()) {
				if (entry.getKey().contains("#")) {
					String[] servers = entry.getKey().split("#");
					for (int i = 0; i < servers.length; i++) {
						String[] server = servers[i].split(":");
						Socket peerServerSocket;
						try {
							peerServerSocket = new Socket(server[0], Integer.parseInt(server[1]));
							sendServerCommand(peerServerSocket, new DownloadPeerFileCommand(downloadfile));
							donwloadFileResp(peerServerSocket);
							break;
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnknownHostException e) {
							System.out.println("Unknow host Trying another server");
						} catch (IOException e) {
							if (e instanceof ConnectException) {
								System.out.println("Unable to Connect To Host Trying Another one");
							}
						}

					}
				}
			}
		} else {
			System.out.println("No Result Found");
		}
	}
	
	private void handlefileDonwload(Socket clientSocket,String downloadfile) throws Peer2PeerException {
		sendServerCommand(clientSocket, new FileLookUpCommand(downloadfile));
		LookUpResponseCommand searchResult = searchResponseMessage(clientSocket);
		if (searchResult != null) {
			for (Map.Entry<String, List<String>> entry : ((LookUpResponseCommand) searchResult).getlookUpReult().entrySet()) {
				if (entry.getKey().contains("#")) {
					String[] servers = entry.getKey().split("#");
					for (int i = 0; i < servers.length; i++) {
						String[] server = servers[i].split(":");
						Socket peerServerSocket;
						try {
							peerServerSocket = new Socket(server[0], Integer.parseInt(server[1]));
							sendServerCommand(peerServerSocket, new DownloadPeerFileCommand(downloadfile));
							donwloadFileResp(peerServerSocket);
							break;
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnknownHostException e) {
							System.out.println("Unknow host Trying another server");
						} catch (IOException e) {
							if (e instanceof ConnectException) {
								System.out.println("Unable to Connect To Host Trying Another one");
							}
						}

					}
				}
			}
		} else {
			System.out.println("No Result Found");
		}
	}

	private void handlefileDonwload_Test(Socket clientSocket, String downloadfile) throws Peer2PeerException {
		sendServerCommand(clientSocket, new FileLookUpCommand(downloadfile));
		LookUpResponseCommand searchResult = searchResponseMessage(clientSocket);
		if (searchResult != null) {
			for (Map.Entry<String, List<String>> entry : ((LookUpResponseCommand) searchResult).getlookUpReult().entrySet()) {
				if (entry.getKey().contains("#")) {
					String[] servers = entry.getKey().split("#");
					for (int i = 0; i < servers.length; i++) {
						String[] server = servers[i].split(":");
						Socket peerServerSocket;
						try {
							peerServerSocket = new Socket(server[0], Integer.parseInt(server[1]));
							sendServerCommand(peerServerSocket, new DownloadPeerFileCommand(downloadfile));
							donwloadFileResp_Test(peerServerSocket);
							break;
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnknownHostException e) {
							System.out.println("Unknow host Trying another server");
						} catch (IOException e) {
							if (e instanceof ConnectException) {
								System.out.println("Unable to Connect To Host Trying Another one");
							}
						}

					}
				} else {
					String[] server = entry.getKey().split(":");
					Socket peerServerSocket;
					try {
						peerServerSocket = new Socket(server[0], Integer.parseInt(server[1]));
						sendServerCommand(peerServerSocket, new DownloadPeerFileCommand(downloadfile));
						donwloadFileResp_Test(peerServerSocket);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnknownHostException e) {
						System.out.println("Unknow host Trying another server");
					} catch (IOException e) {
						if (e instanceof ConnectException) {
							System.out.println("Unable to Connect To Host Trying Another one");
						}
					}

				}
			}
		} else {
			System.out.println("No Result Found");
		}
	}

	private LookUpResponseCommand searchResponseMessage(Socket clientSocket) throws Peer2PeerException {
		InputStream is;
		ObjectInputStream ois;
		try {
			is = clientSocket.getInputStream();
			ois = new ObjectInputStream(is);
			Object serverResp = (Object) ois.readObject();
			if (serverResp != null) {
				if (serverResp instanceof LookUpResponseCommand) {
					return (LookUpResponseCommand) serverResp;
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new Peer2PeerException("Error while sending request to server", e.getCause());
		}
		return null;
	}

	private void ServerResponseMessage(Socket clientSocket) throws Peer2PeerException {
		InputStream is;
		ObjectInputStream ois;
		try {
			is = clientSocket.getInputStream();
			ois = new ObjectInputStream(is);
			Object serverResp = (Object) ois.readObject();
			if (serverResp != null) {
				if (serverResp instanceof MessageCommand) {
					System.out.println(":: Server Response ::");
					System.out.println(((MessageCommand) serverResp).getMessage());
				} else if (serverResp instanceof LookUpResponseCommand) {
					if (((LookUpResponseCommand) serverResp).getlookUpReult().size() > 0) {

						StringBuilder sb = new StringBuilder();
						for (Map.Entry<String, List<String>> entry : ((LookUpResponseCommand) serverResp).getlookUpReult().entrySet()) {
							System.out.println(entry);
						}
						System.out.println(sb.toString());
					} else {
						System.out.println("No Result Found");
					}
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new Peer2PeerException("Error while sending request to server", e.getCause());
		}
	}

	private void ServerResponseMessage_test(Socket clientSocket) throws Peer2PeerException {
		InputStream is;
		ObjectInputStream ois;
		try {
			is = clientSocket.getInputStream();
			ois = new ObjectInputStream(is);
			Object serverResp = (Object) ois.readObject();
			if (serverResp != null) {
				if (serverResp instanceof MessageCommand) {
					System.out.println(":: Server Response ::");
					System.out.println(((MessageCommand) serverResp).getMessage());
				} else if (serverResp instanceof LookUpResponseCommand) {
					if (((LookUpResponseCommand) serverResp).getlookUpReult().size() > 0) {

						StringBuilder sb = new StringBuilder();
						for (Map.Entry<String, List<String>> entry : ((LookUpResponseCommand) serverResp).getlookUpReult().entrySet()) {
							// System.out.println(entry);
						}
						// System.out.println(sb.toString());
					} else {
						// System.out.println("No Result Found");
					}
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new Peer2PeerException("Error while sending request to server", e.getCause());
		}
	}

	private void donwloadFileResp(Socket peerSocket) throws Peer2PeerException {
		try {
			InputStream peeris = peerSocket.getInputStream();
			ObjectInputStream peerois = new ObjectInputStream(peeris);
			Object serverResp = (Object) peerois.readObject();
			while (serverResp == null) {
				peeris = peerSocket.getInputStream();
				peerois = new ObjectInputStream(peeris);
				serverResp = (Object) peerois.readObject();
			}
			if (serverResp instanceof PeerFileCommand) {
				String fileName = ((PeerFileCommand) serverResp).getFilename();
				File dstFile = new File(getAppDir() + "/" + fileName);
				FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
				fileOutputStream.write(((PeerFileCommand) serverResp).getFileData());
				fileOutputStream.flush();
				fileOutputStream.close();
//				System.out.println("your donwload file : " + fileName + " is successfully saved at location " + appDir);
			} else {
//				System.out.println("No Result Found");
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new Peer2PeerException("Error while sending request to server", e.getCause());
		}
	}

	private void donwloadFileResp_Test(Socket peerSocket) throws Peer2PeerException {
		try {
			InputStream peeris = peerSocket.getInputStream();
			ObjectInputStream peerois = new ObjectInputStream(peeris);
			Object serverResp = (Object) peerois.readObject();
			if (serverResp instanceof PeerFileCommand) {
				String fileName = ((PeerFileCommand) serverResp).getFilename();
				File dstFile = new File(getAppDir() + "/" + fileName);
				FileOutputStream fileOutputStream = new FileOutputStream(dstFile);
				fileOutputStream.write(((PeerFileCommand) serverResp).getFileData());
				fileOutputStream.flush();
				fileOutputStream.close();
				// System.out.println("your donwload file : " + fileName +
				// " is successfully saved at location " + appDir);
				peerSocket.close();
			} else {
				// System.out.println("No Result Found");
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new Peer2PeerException("Error while sending request to server", e.getCause());
		}
	}

	public String getAppDir() {
		return appDir;
	}

	public void setAppDir(String appDir) {
		ApplicationGobalVariableHolder.getInstance().setAppDir(appDir);
		this.appDir = appDir;
	}

	private String getIpAddress() {
		String ipAddress = null;
		try {
			ipAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ipAddress;
	}

	private String getPeerIpAddress() {
		System.out.println("Pleaase Enter the Peer Host Ip Address");
		Scanner hostScanner = new Scanner(System.in);
		return hostScanner.nextLine();
	}

	private int getPeerHostAddress() {
		System.out.println("Pleaase Enter the Port of peer");
		Scanner portScanner = new Scanner(System.in);
		return portScanner.nextInt();
	}

	private String getFileDownloadPath() {
		System.out.println("Please Enter File Path :");
		Scanner fpathScanner = new Scanner(System.in);
		return fpathScanner.nextLine();
	}
}
