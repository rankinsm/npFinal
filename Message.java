package EncryptedChatRoom;

import java.io.Serializable;

public class Message implements Serializable{
	private int type;
	private String message;
	static final int ONLINE = 0, MESSAGE = 1, LOGOUT = 2;
	
	Message(int type, String message){
		this.type = type;
		this.message = message;
	}
	
	int getType() {
		return type;
	}
	
	String getMessage() {
		return message;
	}
}
