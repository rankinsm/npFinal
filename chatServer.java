package EncryptedChatRoom;

import java.text.SimpleDateFormat;
import java.util.*;
import java.net.*;
import java.io.*;

public class chatServer {
	private serverInterface sInterface;
	private int port;
	private SimpleDateFormat dateFormat;
	private ArrayList<ClientThread> cList;
	private boolean active;
	private static int connectionID;

	public chatServer(int port) {
		this(port, null);
	}
	
	public  chatServer(int port, serverInterface sInterface) {
		this.sInterface = sInterface;
		this.port = port;
		dateFormat = new SimpleDateFormat("HH:mm");
		cList = new ArrayList<ClientThread>();
	}
	
	public void start() {
		active = true;
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while(active) {
				display("Server: Waiting for clients[Port: " + port + "]");
				Socket socket = serverSocket.accept();
				if(!active) {
					break;
				}
				ClientThread cThread = new ClientThread(socket);
				cList.add(cThread);
				cThread.start();
			}
			try {
				serverSocket.close();
				for(int i = 0; i < cList.size(); ++i) {
					ClientThread tempThread = cList.get(i);
					try {
						tempThread.streamIn.close();
						tempThread.streamOut.close();
						tempThread.socket.close();
					}
					catch(IOException e) {
					}
				}
			}
				catch(Exception e) {
					display("Exception - Closing server and clients \n");
			}
			
		}
		catch(IOException e) {
			String message = dateFormat.format(new Date()) + "Exception - Server socket";
			display(message);
		}
	}
	
	protected void stop() {
		active = false;
		try {
			new Socket("localhost", port);
		}
		catch(Exception e){
		}
	}
	
	private void display(String msg) {
		String time = dateFormat.format(new Date()) + "" + msg;
		if (sInterface == null) {
			System.out.println(time);
		}
		else {
			sInterface.appendLog(time + "\n");
		}
	}
	
	private synchronized void broadcast(String message) {
		String time = dateFormat.format(new Date());
		String msgSend = time + " " + message + "\n";
		if (sInterface == null) {
			System.out.println(msgSend);
		}
		else{
			sInterface.appendRoom(msgSend);
		}
		for(int i = cList.size(); --i >= 0;) {
			ClientThread tempThread = cList.get(i);
			if(!tempThread.writeMsg(message)) {
				cList.remove(i);
				display(tempThread.username + " disconnected");
			}
		}
	}
	
	private synchronized void remove(int id) {
		for(int i = 0; i < cList.size(); ++i) {
			ClientThread tempThread = cList.get(i);
			if(tempThread.id == id) {
				cList.remove(i);
				return;
			}
		}
	}
	
	public static void main(String[] args) {
		int portNum = 8000;
		switch(args.length) {
		case 1:
			try {
				portNum = Integer.parseInt(args[0]);
			}
			catch(Exception e) {
				System.out.println("Port number not valid.");
				return;
			}
		case 0:
			break;
		default:
			System.out.println("Port number not valid.");
		}
		chatServer server = new chatServer(portNum);
		server.start();
	}
	
	
	class ClientThread extends Thread{
		Socket socket;
		int id;
		ObjectInputStream streamIn;
		ObjectOutputStream streamOut;
		String username, date;
		Message cMsg;
		
		ClientThread(Socket socket){
			id = ++connectionID;
			this.socket = socket;
			System.out.println("Thread creating input/output streams.");
			try {
				streamIn = new ObjectInputStream(socket.getInputStream());
				streamOut = new ObjectOutputStream(socket.getOutputStream());
				username = (String) streamIn.readObject();
				display(username + " has connected.");
			}
			catch(IOException e){
				display("Exception - Creating input/output streams.");
				return;
			}
			catch(ClassNotFoundException e2) {	
			}
			date = new Date().toString() + "\n";
		}
		
		public void run() {
			boolean active = true;
			while(active) {
				try {
					cMsg = (Message) streamIn.readObject();
				}
				catch(IOException e) {
					display(username + " Exception - Reading streams.");
					break;
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				String message = cMsg.getMessage();
				switch(cMsg.getType()) {
				case Message.MESSAGE:
					broadcast(username + ": " + message);
					break;
				case Message.LOGOUT:
					display(username + "disconnected");
					active = false;
					break;
				case Message.ONLINE:
					writeMsg("Online Users: \n");
					for(int i = 0; i < cList.size(); ++i) {
						ClientThread tempThread = cList.get(i);
						writeMsg((i+1) + tempThread.username);
					}
					break;
				}
			}
			remove(id);
			close();
		}
		
		private void close() {
			try {
				if(streamIn != null) {
					streamIn.close();
				}
			}
			catch(Exception e){
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
			catch(Exception e){	
			}
		}
		
		private boolean writeMsg(String msg){
			if(!socket.isConnected()) {
				close();
				return false;
			}
			try {
				streamOut.writeObject(msg);
			}
			catch(IOException e) {
				display("Error - Could not send message.");
			}
			return true;
		}
	}
}
