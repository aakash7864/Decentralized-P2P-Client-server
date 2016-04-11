package org.cs550.peer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class DistributedHashMapImpl {

	int noOfNodes;
	Socket[] severSockets = null;
	ArrayList<Long> getTimeArray = new ArrayList<>();
	ArrayList<Long> putTimeArray = new ArrayList<>();
	ArrayList<Long> delTimeArray = new ArrayList<>();

	public DistributedHashMapImpl() {
		severSockets = ConnectionHolder.getConnectionSockets();
		this.noOfNodes = severSockets.length;
	}

	public int hash(String key) {
		int hashvalue = 0;
		if (key != null || !key.isEmpty()) {
			for (int i = 0; i < key.length(); i++) {
				hashvalue = hashvalue + ((int) key.charAt(i));
			}
			hashvalue = hashvalue % noOfNodes;
		}
		return hashvalue;
	}

	public Long getPutTime() {
		Long value = 0L;
		for (Long iterable_element : this.putTimeArray) {
			value = value + iterable_element;
		}
		return value;
	}

	public Long getGetTime() {
		Long value = 0L;
		for (Long iterable_element : this.getTimeArray) {
			value = value + iterable_element;
		}
		return value;
	}

	public Long getDelTime() {
		Long value = 0L;
		for (Long iterable_element : this.delTimeArray) {
			value = value + iterable_element;
		}
		return value;
	}

	public boolean put(String key, String value) {
		boolean result = false;
		Socket connSocket = LookUpServer(key);
		Long putStartTime = System.nanoTime();
		sendDataToServer(connSocket, key + value);
		if (reciveDataFromServer(connSocket).equals("True")) {
			result = true;
			this.putTimeArray.add(System.nanoTime() - putStartTime);
		}
		return result;
	}

	private String reciveDataFromServer(Socket connSocket) {
		String str = null;
		try {
			while (connSocket.isConnected()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
				if (br.ready()) {
					str = br.readLine();
					break;
				}
			}
		} catch (IOException e) {

		}
		return str;
	}

	private void sendDataToServer(Socket connSocket, String value) {
		OutputStream outstream;
		try {
			outstream = connSocket.getOutputStream();
			PrintWriter printWriter = new PrintWriter(outstream);
			printWriter.println(value);
			printWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String get(String key) {
		String result = "";
		Socket connSocket = LookUpServer(key);
		Long putStartTime = System.nanoTime();
		sendDataToServer(connSocket, key);
		result = reciveDataFromServer(connSocket);
		if (result != null) {
			this.getTimeArray.add(System.nanoTime() - putStartTime);
		}
		return result;
	}

	public boolean del(String key) {
		boolean result = false;
		Socket connSocket = LookUpServer(key);
		Long putStartTime = System.nanoTime();
		sendDataToServer(connSocket, key);
		if (reciveDataFromServer(connSocket).equals("True")) {
			result = true;
			this.delTimeArray.add(System.nanoTime() - putStartTime);
		}
		return result;
	}

	private Socket LookUpServer(String key) {
		return severSockets[hash(key.substring(1))];
	}
}
