package client;

import javax.swing.*;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import client.ChatClient;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;


class DBManager {
	String driver = "org.mariadb.jdbc.Driver";
	String url = "jdbc:mysql://192.168.0.13:3306/test";
	String uId = "root";
	String uPwd = "1234";
	
	Connection con;
	PreparedStatement pstmt;
	ResultSet rs;
	
	public DBManager() {
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, uId, uPwd);
			if(con != null) {System.out.println("������ ���̽� ���� ����");}
		} catch(ClassNotFoundException e) {
			System.out.println("������ ���̽� �ε� ����");
		} catch(SQLException e) {
			System.out.println("������ ���̽� ���� ����");
		}
	}
	
	public String select() {
		String sql = "select * from a";
		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			String Code;
			int Code2;
			while(rs.next()) {
				/*Code2 = rs.getInt("answerNum");
				System.out.print(Code2);
				Code = rs.getString("answer0");
				System.out.println(". " + Code);
				Code = rs.getString("answer1");
				System.out.println("1�� ���� : " + Code);
				Code = rs.getString("answer2");
				System.out.println("2�� ���� : " + Code);
				Code = rs.getString("answer3");
				System.out.println("3�� ���� : " + Code);*/
				Code = rs.getString("answer4");
				//System.out.println("4�� ���� : " + Code);
				return Code;
			}
		}catch(SQLException e) {
			System.out.println("���� ���� ����");
		}
		return null;
	}
}

public class SendThread extends Thread{
	private Socket m_Socket;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		
		try {
			BufferedReader tmpbuf = new BufferedReader(new InputStreamReader(System.in));
			
			PrintWriter sendWriter = new PrintWriter(m_Socket.getOutputStream());
			
			String sendString;
			
			System.out.print("����� ID�� �Է����ֽʽÿ� : ");

			ChatClient.UserID = 
					tmpbuf.readLine();
			//ChatClient.UserID = frame.textfield.getText();
			
			
			sendWriter.println("ID-Client" + ChatClient.UserID);
			sendWriter.flush();
			
			
			while(true)
			{
				sendString = tmpbuf.readLine();
				if(sendString.equals("exit"))
				{
					break;
				}
				
				sendWriter.println(sendString);
				sendWriter.flush();
			}
			
			sendWriter.close();
			tmpbuf.close();
			m_Socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setSocket(Socket _socket)
	{
		m_Socket = _socket;
	}	
}
