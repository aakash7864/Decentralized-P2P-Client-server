package org.cs550.peer.exception;

public class Peer2PeerException extends Exception {

	public Peer2PeerException(String message) {
		super(message);
	}

	public Peer2PeerException(Throwable cause) {
		super(cause);
	}

	public Peer2PeerException(String message, Throwable cause) {
		super(message, cause);
	}

}
