package EncryptedChatRoom;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class serverInterface extends JFrame implements ActionListener, WindowListener{
	private chatServer server;
	private JTextField txtFPortNum;
	private JButton btnAction;
	private JTextArea txtAChat, txtALog;
	
	serverInterface(int port){
		super("Chat Room Server Interface");
		server = null;
		
		JPanel topPanel = new JPanel();
		
		topPanel.add(new JLabel("Port: "));
		txtFPortNum = new JTextField(" " + port);
		topPanel.add(txtFPortNum);
		
		btnAction = new JButton("Start");
		btnAction.addActionListener(this);
		topPanel.add(btnAction);
		add(topPanel, BorderLayout.NORTH);
		
		JTabbedPane tabPane = new JTabbedPane();
		
		JPanel chatRoom = new JPanel(new GridLayout(1,1));
		txtAChat = new JTextArea(80,100);
		txtAChat.setEditable(false);
		//appendRoom("Chat Room \n");
		chatRoom.add(new JScrollPane(txtAChat));
		tabPane.addTab("Chat Room", chatRoom);
		
		JPanel serverLog = new JPanel(new GridLayout(1,1));
		txtALog = new JTextArea(80,100);
		txtALog.setEditable(false);
		//appendEvent("Server Log \n");
		serverLog.add(new JScrollPane(txtALog));
		tabPane.addTab("Server Log", serverLog);
		add(tabPane);
		
		addWindowListener(this);
		setSize(400, 600);
		setVisible(true);
	}
	
	void appendRoom(String str) {
		txtAChat.append(str);
		txtAChat.setCaretPosition(txtAChat.getText().length()-1);
	}
	
	void appendLog(String str) {
		txtALog.append(str);
		txtALog.setCaretPosition(txtAChat.getText().length()-1);
	}
	
	public void actionPerformer(ActionEvent e) {
		if(server != null) {
			server.stop();
			server = null;
			txtFPortNum.setEditable(true);
			btnAction.setText("Connect");
			return;
		}
		int port;
		try {
			port = Integer.parseInt(txtFPortNum.getText().trim());
		}
		catch(Exception e2) {
			appendLog("Port number not valid.");
			return;
		}
		server = new chatServer(port, this);
		new chatServerActive.start();
		btnAction.setText("Disconnect");
		txtFPortNum.setEditable(false);
	}
	
	public static void main(String[] arg) {
		new serverInterface(8000);
	}
	
	public void windowCLosing(WindowEvent e) {
		if(server != null) {
			try {
				server.stop();
			}
			catch(Exception e2) {
			}
			server = null;
		}
		dispose();
		System.exit(0);
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	class chatServerActive extends Thread{
		public void run() {
			server.start();
			btnAction.setText("Connect");
			txtFPortNum.setEditable(true);
			appendLog("Server Crashed \n");
			server = null;
		}
	}
}
