package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import javax.swing.*;

public class ReceiveThread extends Thread{

	private Socket m_Socket;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		try {
			FrameExam temp = new FrameExam();
			temp = FrameExam.getFrame();
			BufferedReader tmpbuf = new BufferedReader(new InputStreamReader(m_Socket.getInputStream()));
			
			String receiveString;
			String[] split;
			
			while(true)
			{
				receiveString = tmpbuf.readLine();
				
				split = receiveString.split(">");
				if(split.length >= 2 && split[0].equals(ChatClient.UserID))
				{
					continue;
				}
				
				temp.label.append(ChatClient.UserID + "> " + receiveString +"\n");
				System.out.println(receiveString);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}
	
	public void setSocket(Socket _socket)
	{
		m_Socket = _socket;
	}

}
