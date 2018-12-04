package EncryptedChatRoom;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	private String server, username;
	private int port;
	private clientInterface cInterface;
	private Socket socket;
	private ObjectInputStream streamIn;
	private ObjectOutputStream streamOut;
	
	Client(String server, int port, String username){
		this(server, port, username, null);
	}
	
	Client(String server, int port, String username, clientInterface cInterface){
		this.server = server;
		this.port = port;
		this.username = username;
		this.cInterface = cInterface;
	}
	
	public boolean start() {
		try {
			socket = new Socket(server, port);
		}
		catch(Exception e) {
			display("Error - Couldn't conect to server");
			return false;
		}
		String msg = "Connected - " socket.getInetAddress() + " : " + socket.getPort();
		display(msg);
		
		try {
			streamIn = new ObjectInputStream(socket.getInputStream());
			streamOut = new ObjectOutputStream(socket.getOutputStream());
		}
		catch(IOException e) {
			display("Exception - Creatin input/output stream.");
			return false;
		}
		new ListenFromServer().start();
		try {
			streamOut.writeObject(username);
		}
		catch(IOException e) {
			display("Exception - Login");
			disconnect();
			return false;
		}
		return true;
	}
	
	private void display(String msg) {
		if(cInterface == null) {
			System.out.println(msg);
		}
		else {
			cInterface.append(msg + "\n");
		}
	}
	
	void sendMessage(Message msg) {
		try {
			streamOut.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception - Writing to server.");
		}
	}
	
	private void disconnect() {
		try {
			if(streamIn != null) {
				streamIn.close();
			}
		}
		catch(Exception e) {	
		}
		try {
			if(streamOut != null) {
				streamOut.close();
			}
		}
		catch(Exception e) {
		}
		try {
			if(socket != null) {
				socket.close();
			}
		}
		catch(Exception e) {
		}
		
		if(cInterface != null) {
			cInterface.connectionFailed();
		}
	}
	
	public static void main(String[] args) {
		int portNum = 8000;
		String serverAddress = "localhost";
		String userName = "User";
		switch(args.length) {
			case 3:
				serverAddress = args[2];
			case 2:
				try {
					portNum = Integer.parseInt(args[1]);
				}
				catch(Exception e) {
					System.out.println("Port number not valid.");
					return;
				}
			case 1: 
				userName = args[0];
			case 0:
				break;
			default:
				System.out.println("Port number not valid.");
				return;
		}
		Client client = new Client(serverAddress, portNum, userName);
		if(!client.start()) {
			return;
		}
		Scanner input = new Scanner(System.in);
		while(true) {
			System.out.println("> ");
			String msg = input.nextLine();
			if(msg.equalsIgnoreCase("EXIT")) {
				client.sendMessage(new Message(Message.LOGOUT, ""));
				break;
			}
			else if(msg.equalsIgnoreCase("ONLINE")) {
				client.sendMessage(new Message(Message.ONLINE, ""));
			}
			else {
				client.sendMessage(new Message(Message.MESSAGE, ""));
			}
		}
		client.disconnect();
	}
	
	class ListenFromServer extends Thread{
		public void run() {
			while(true) {
				try {
					String msg = (String) streamIn.readObject();
					if(cInterface == null) {
						System.out.println(msg);
						System.out.print("> ");
					}
					else {
						cInterface.append(msg);
					}
				}
				catch(IOException e) {
					display("Server - Connection closed");
					if(cInterface != null) {
						cInterface.connectionFailed();
						break;
					}
				}
				catch(ClassNotFoundException e2) {
					
				}
			}
		}
	}
}
