package org.cs550.peer.server.service;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.cs550.commands.UpdatePeerFilesCommand;
import org.cs550.peer.exception.Peer2PeerException;
import org.cs550.peer.util.PeerUtils;

public class FileUpdateService implements Runnable {

	private List<String> oldFileList;
	private String appDir;
	private Socket socket;
	
	private FileUpdateService() {
		// TODO Auto-generated constructor stub
	}

	public FileUpdateService(String appDir, List<String> oldFileList, Socket socket) {
		this.oldFileList = oldFileList;
		this.appDir = appDir;
		this.socket = socket;
	}

	@Override
	public void run() {
		List<String> updatedFileList = PeerUtils.getFileListFromDir(this.appDir);
		if (!oldFileList.containsAll(updatedFileList)) {
			this.oldFileList = null;
			this.oldFileList = updatedFileList;
			try {
				OutputStream connos = this.socket.getOutputStream();
				ObjectOutputStream connoos = new ObjectOutputStream(connos);
				connoos.writeObject(new UpdatePeerFilesCommand(updatedFileList));
				connoos.flush();
				connos.flush();
			} catch (IOException e) {
				try {
					throw new Peer2PeerException("Error while updating files to central index server");
				} catch (Peer2PeerException e1) {
					System.out.println(e1.getMessage());
				}
			}
		}
	}

}
