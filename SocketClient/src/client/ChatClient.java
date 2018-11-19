package client;

import java.io.*;
import java.sql.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import javafx.application.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ChatClient extends Application{
	static Socket socket;
	static HashMap<Integer, String> quizDB = new HashMap<Integer, String>();
	static String result = "0";
	static String chatLogtemp = "";
	
	VBox root = new VBox();
	static TextArea rootMessage = new TextArea();
   	static TextArea chatlog = new TextArea();
	static TextField chatField = new TextField();
	static TextField ipField = new TextField();
	static TextField portField = new TextField();
	static TextField timerField = new TextField();
	static TextArea loging = new TextArea("���� ���� ���\n------------\n");
	static TextArea quizlog = new TextArea("���� ���\n--------\n");
   	static Button sendButton = new Button();
   	static Button endButton = new Button();
   	static GridPane grid = new GridPane();
   	static TextInputDialog dialog = new TextInputDialog();
   	static String userName = null;
	
	@Override
	public void start(Stage stage) {
	   	try {	   		
		   	root.setPadding(new Insets(10)); // ���� ���� ����
		   	root.setSpacing(10); // ��Ʈ�� ���� ���� ���� ����
		   	
		   	rootMessage.setEditable(false);
	    	rootMessage.prefWidthProperty().bind(stage.widthProperty());
	    	
	    	//chatlog.setDisable(true);
	    	chatlog.prefWidthProperty().bind(stage.widthProperty());
	    	chatlog.prefHeightProperty().bind(stage.heightProperty());
	    	chatlog.setWrapText(true); 
	    	chatlog.setEditable(false);
	    	
	    	loging.setEditable(false);
	    	loging.prefWidthProperty().bind(stage.widthProperty());
	    	loging.prefHeightProperty().bind(stage.heightProperty());
		    	
	    	quizlog.setEditable(false);
	    	quizlog.prefWidthProperty().bind(stage.widthProperty());
	    	quizlog.prefHeightProperty().bind(stage.heightProperty());
		    	
	    	sendButton.setText("�� ��");
	    	sendButton.prefWidthProperty().bind(stage.widthProperty());
	    	sendButton.setMaxWidth(1000);
	    	
	    	endButton.setText("�� �� ��");
	    	endButton.prefWidthProperty().bind(stage.widthProperty());
	    	endButton.setMaxWidth(500);
		    	
	    	chatField.prefWidthProperty().bind(stage.widthProperty());
	    	
	    	ipField.prefWidthProperty().bind(stage.widthProperty());
	    	
	    	portField.prefWidthProperty().bind(stage.widthProperty());
	    	
	    	timerField.setEditable(false);
	    	timerField.prefWidthProperty().bind(stage.widthProperty());
	
	    	grid.setVgap(10);
	    	grid.setHgap(10);
	    	grid.add(ipField, 0, 0, 1, 1);
	    	grid.add(portField, 1, 0, 1, 1);
	    	grid.add(timerField, 2, 0, 1, 1);
	    	grid.add(chatlog, 0, 1, 2, 2);
	    	grid.add(chatField, 0, 3, 1, 1);
	    	grid.add(sendButton, 1, 3, 1, 1);
	    	grid.add(loging, 2, 1, 1, 1); 
	    	grid.add(quizlog, 2, 2, 1, 1);
	    	grid.add(endButton, 2, 3, 1, 1);
		    	
	    	dialog.setTitle("�̸� �Է� â");
	    	dialog.setHeaderText("ä�� �濡�� ����� �̸��� �Է��ϼ���");
	    	dialog.setContentText("�̸� : ");

	    	Optional<String> result = dialog.showAndWait();
	    	if (result.isPresent()){
	    	    System.out.println("�̸� : " + result.get());
	    	    if(result.get().equals("")) {
	    	    	userName = "name:�͸�";
	    	    	send(userName);
	    	    }
	    	    
	    	    else {
		    	    userName = "name:" + result.get();
		    	    send(userName);
	    	    }
	    	}
	    	
	    	sendButton.setOnAction(new EventHandler<ActionEvent>() {
	    	    @Override
	    	    public void handle(ActionEvent event) {
	    	    	send(chatField.getText());
	    	    	chatField.setText("");
	    	    	chatField.requestFocus();
	    	    }
	    	});
	    	
	    	endButton.setOnAction(new EventHandler<ActionEvent>() {
	    	    @Override
	    	    public void handle(ActionEvent event) {
	    	    	send("quit" + userName);
	    	    	try{
	    	    		Thread.sleep(100);
	    	    	}catch(Exception e){
	    	    		e.printStackTrace();
	    	    	}
	    	    	stopClient();
	    	    	stage.hide();
	    	    }
	    	});
	    	
	    	chatField.setOnKeyPressed(new EventHandler<KeyEvent>() {
		    		@Override
		    		public void handle(KeyEvent event) {
		    			if(event.getCode() == KeyCode.ENTER) {
			    			send(chatField.getText());
			    	    	chatField.setText("");
			    	    	chatField.requestFocus();
		    			}
		    		}
	    	});
	    	
	    	ObservableList<Node> list = root.getChildren();
	    	list.add(rootMessage);
	    	
	    	list.add(grid);
		    	//list.add(grid2);
		    	Scene scene = new Scene(root, 500, 500);
	
	    	stage.setTitle("ä��â");
	    	stage.setScene(scene);
	    	stage.show();	
    	
    	} catch(Exception e) {
    		System.out.println(e);
    	}
	}
	
	public static void runQuiz() {
		chatLogtemp = chatlog.getText();
		chatlog.setText("");
		
		chatlog.appendText(quizDB.get(1) + "\n");
	}
	
	public static void stopClient() {		
		try {
			if(socket != null && !socket.isClosed()) {	
				send(userName);
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void receive() {
		while(true) {
			try {
				InputStream in = socket.getInputStream();
				byte[] buffer = new byte[2048];
				int length = in.read(buffer);
				if(length == -1) throw new IOException();
				String message = new String(buffer, 0, length, "UTF-8");

				Platform.runLater(()->{
					if(message.length() >= 1 && message.substring(0,1).equals("m")) {
						if(message.length() >= 6 && message.substring(0, 5).equals("mquiz")) {
							quizDB.put(1, message.split("\\^")[2]);
							result = message.split("\\^")[3];
							System.out.println(result);
						}
						
						else if(message.length() >= 10 && message.substring(0, 10).equals("mstartquiz")) {
							runQuiz();	
						}
						
						else if(message.length() >= 3 && message.substring(0, 3).equals("m��:")) {
							System.out.println(message.substring(3));
							if(mark(message.substring(3))) {
								send("�����Դϴ�.");
							}
							else {
								send("�����Դϴ�.");
							}
							
						}
						
						
						else if(message.length() >= 7 && message.substring(0, 7).equals("mDBquiz")) {
							quizlog.setText("���� ���\n--------\n");
							quizlog.appendText(message.substring(7));
							
						}
						
						else
							chatlog.appendText(message.substring(1) + "\n");
					}
					
					else if(message.length() >= 8 && message.substring(0, 8).equals("userinfo") 
							&& !loging.getText().contains(message.substring(8))) {
						loging.appendText(message.substring(8) + "\n");
					}
					
					else if(message.length() >= 6 && message.substring(0, 6).equals("delete") 
							&& loging.getText().contains(message.substring(6))) {
						loging.setText(loging.getText().replace(message.substring(6)+"\n", ""));
						System.out.println(message.substring(6));
					}
					
					
				});
				
			} catch (IOException e) {
				stopClient();
				break;
			}
		}
	}
	
	public static boolean mark(String quizResult) {
		if(quizResult.equals(result))
			return true;
		else
			return false;
	}
	
	public static void send(String message) {
		Thread thread = new Thread() {
			public void run() {
				try {
					OutputStream out = socket.getOutputStream();
					byte[] buffer = message.getBytes("UTF-8");
					out.write(buffer);
					out.flush();
				} catch (Exception e) {
					stopClient();
				}
			}
		};
		
		thread.start();
	}
	
	public static void startClient(String IP, int port) {
		Thread thread = new Thread() {
			public void run() {
				try {
					socket = new Socket(IP, port);
					System.out.println("[���� ���� ����]");
					chatlog.appendText("[���� ���� ����]" + "\n");
	    	    	chatField.requestFocus();
					receive();
				} catch (Exception e) {
					if(!socket.isClosed()) {
					stopClient();
					System.out.println("[���� ���� ����]");
					Platform.exit();
					}
				}
			}
		};
		thread.start();
	}
	
	public static void main(String[] args) {
		startClient("192.168.43.206", 8888);
		launch(args);
	}
}
