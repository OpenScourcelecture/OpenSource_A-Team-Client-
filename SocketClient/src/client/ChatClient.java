package client;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;

class FrameExam
{
	private static FrameExam exam = new FrameExam();
	JFrame frame = new JFrame("Quiz");
	JTextArea label = new JTextArea();
	JScrollPane sp = new JScrollPane(label);
	
	public static FrameExam getFrame() {
		return exam;
	}
	
	public void createFrame()
	{
		//frame.add(label);
		frame.setSize(500, 600);
		frame.setVisible(true);
		//label.setHorizontalAlignment(SwingConstants.CENTER);
		//label.setVerticalAlignment(SwingConstants.CENTER);
		//contentPane.add(sp);
		frame.add(label);
	}
	
	public void text(String text) {
		label.setText(text);
		frame.add(label);
	}
}

public class ChatClient {

	public static String UserID;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			FrameExam temp = new FrameExam();
			temp = FrameExam.getFrame();
			temp.createFrame();
			Socket c_socket = new Socket("192.168.0.13", 8888);

			ReceiveThread rec_thread = new ReceiveThread();
			rec_thread.setSocket(c_socket);
			
			SendThread send_thread = new SendThread();
			send_thread.setSocket(c_socket);
			
			send_thread.start();
			rec_thread.start();
		
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
