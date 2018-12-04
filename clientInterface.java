package EncryptedChatRoom;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class clientInterface extends JFrame implements ActionListener {
	private String defaultHost;
	private int defaultPort;
	private JTextField txtFName, txtFPort, txtFServer, txtFMessage;
	private JTextArea txtAChat;
	private JButton btnConnect, btnDisconnect;
	private boolean connected;
	private Client client;
	
	clientInterface(String host, int port){
		defaultHost = host;
		defaultPort = port;
		
		JPanel topPanel = new JPanel(new GridLayout(2,2));
		JPanel settingsPanel = new JPanel(new GridLayout(1,5,1,2));
		JPanel namePanel = new JPanel(new GridLayout(2,2));
		
		txtFServer = new JTextField(host);
		txtFServer.setHorizontalAlignment(SwingConstants.CENTER);
		txtFPort = new JTextField(port);
		txtFPort.setHorizontalAlignment(SwingConstants.CENTER);
		
		settingsPanel.add(new JLabel("Server: "));
		settingsPanel.add(txtFServer);
		settingsPanel.add(new JLabel("Port Number: "));
		settingsPanel.add(txtFPort);
		topPanel.add(settingsPanel);
		
		namePanel.add(new JLabel("Username: "));
		namePanel.add(txtFName);
		txtFName = new JTextField("User");
		txtFName.setHorizontalAlignment(SwingConstants.CENTER);
		topPanel.add(namePanel);
		
		JPanel middlePanel = new JPanel(new GridLayout(1,1));
		
		middlePanel.add(new JScrollPane(txtAChat));
		txtAChat = new JTextArea("Welcome to the chat room \n");
		txtAChat.setEditable(false);
		add(middlePanel, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel(new GridLayout(3,1));
		
		txtFMessage = new JTextField("Enter message here");
		txtFMessage.setEditable(false);
		btnConnect = new JButton("Connect");
		btnConnect.addActionListener(this);
		btnDisconnect = new JButton("Disconnect");
		btnDisconnect.addActionListener(this);
		
		bottomPanel.add(txtFMessage);
		bottomPanel.add(btnConnect);
		bottomPanel.add(btnDisconnect);
		
		add(bottomPanel, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 800);
		setVisible(true);
		txtFMessage.requestFocus();
	}
	
	void append(String str) {
		txtAChat.append(str);
		txtAChat.setCaretPosition(txtAChat.getText().length()-1);
	}
	
	void connectionFailed() {
		btnConnect.setEnabled(true);
		btnDisconnect.setEnabled(false);
		txtFServer.setText(defaultHost);
		txtFPort.setText("" + defaultPort);
		txtFName.setText("User");
		txtFName.setEditable(false);
		txtFServer.setEditable(false);
		txtFPort.setEditable(false);
		txtFMessage.setEditable(false);
		txtFName.removeActionListener(this);
		connected = false;
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if(o == btnConnect) {
			String username = txtFName.getText().trim();
			if(username.length() == 0) {
				return;
			}
			String server = txtFServer.getText().trim();
			if(server.length() == 0) {
				return;
			}
			String portNum = txtFPort.getText().trim();
			if(portNum.length() == 0) {
				return;
			}
			int port = 0;
			try {
				port = Integer.parseInt(portNum);
			}
			catch(Exception e2) {
				return;
			}
			client = new Client(server, port, username, this);
			if(!client.start()) {
				return;
			}
			connected = true;
			btnConnect.setEnabled(false);
			btnDisconnect.setEnabled(true);
			txtFServer.setEditable(false);
			txtFPort.setEditable(false);
			txtFName.setEditable(false);
			txtFMessage.setEditable(true);
			txtFMessage.addActionListener(this);
		}
		if(o == btnDisconnect) {
			client.sendMessage(new Message(Message.LOGOUT, ""));
			return;
		}
		if(connected) {
			client.sendMessage(new Message(Message.MESSAGE, txtFMessage.getText()));
			return;
		}
	}
	public static void main(String[] args) {
		new clientInterface("local", 8000);
	}
}
