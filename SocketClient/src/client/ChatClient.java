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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Pair;

class setData{
	private String ip;
	private String port;
	private String name;
	
	public setData(String ip, String port, String name) {
		this.ip = ip;
		this.port = port;
		this.name = name;
	}
}

public class ChatClient extends Application{
	static Socket socket;
	static HashMap<Integer, String> quizDB = new HashMap<Integer, String>();
	static ArrayList<setData> setdata = new ArrayList<setData>();
	static String result = "";
	static int count = 0; static int correctResult = 0; static int incorrectResult = 0;
	static String userResult = "";
	static String chatLogtemp = "";
	
	Scene scene;
	
	static TextArea rootMessage = new TextArea();
   	
	static TextField ipField = new TextField();
	static TextField portField = new TextField();
	static TextField timerField = new TextField();
	HBox serverBar = new HBox(3.,ipField,portField,timerField);
	
   	Button sendButton = new Button("전 송");
   	Button endButton = new Button("나 가 기");
   	HBox buttonBar = new HBox(2. ,sendButton,endButton);	

   	static TextArea chatlog = new TextArea();
   	static TextField chatField = new TextField();
   	static TextArea loging = new TextArea("접속 중인 사람\n------------\n");
   	static TextArea quizlog = new TextArea("퀴즈 목록\n--------\n");
   	GridPane grid = new GridPane();
   	
	VBox root = new VBox(3.,rootMessage,serverBar,grid);
 
  	static String userName = null;
   	static Dialog<setData> dialog = new Dialog<>();
   	static String serverIP = null;
   	static String serverPort = null;
	
	@Override
	public void start(Stage stage) {
	   try {	   			   		
		rootMessage.setEditable(false);										
		chatlog.setEditable(false);
		loging.setEditable(false);											
	 	quizlog.setEditable(false);	
	    	ipField.setEditable(false);
		portField.setEditable(false);
		timerField.setEditable(false);

		loging.prefHeightProperty().bind(stage.heightProperty());  
		quizlog.prefHeightProperty().bind(stage.heightProperty());
		chatField.prefWidthProperty().bind(stage.widthProperty());			

		sendButton.prefWidthProperty().bind(buttonBar.widthProperty());
		endButton.prefWidthProperty().bind(buttonBar.widthProperty());    
		timerField.prefWidthProperty().bind(serverBar.widthProperty());
		ipField.prefWidthProperty().bind(serverBar.widthProperty());
		portField.prefWidthProperty().bind(serverBar.widthProperty());

		grid.add(chatlog, 0, 0, 2, 2);	    	
		grid.add(loging, 2, 0, 1, 1); 
		grid.add(quizlog, 2, 1, 1, 1);
		grid.add(chatField, 0, 2, 1, 1);
		grid.add(buttonBar, 2, 2, 1, 1);

		dialog.setTitle("Welcome Quiz-chat");
		TextField ipField2 = new TextField("192.168.0.13");
		TextField portField2 = new TextField("8888");
		TextField nameField2 = new TextField("익명");

		VBox dialogPane = new VBox(3.,new HBox(2., new Label("IP : "), ipField2),
				new HBox(2., new Label("PORT : "), portField2), 
				new HBox(2., new Label("NAME : "), nameField2));

		ButtonType okButton = new ButtonType("OK", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
		dialog.getDialogPane().setContent(dialogPane);
		dialogPane.getStylesheets().add(getClass().getResource("./login.css").toExternalForm());

	    	
		dialog.setResultConverter(new Callback<ButtonType, setData>() {	
			@Override
			public setData call(ButtonType ok) {					
				if(ok == okButton) 
					return new setData(ipField2.getText(), portField2.getText(), nameField2.getText());		    		
				return null;
				}
			});
	    	
		Optional<setData> result = dialog.showAndWait();
		if(result.isPresent()) {
			ipField.setText(serverIP = ipField2.getText());
			portField.setText(serverPort = portField2.getText());
			userName = "name:" + nameField2.getText();
			System.out.println(serverIP + " " + userName + " " +serverPort);
			
			showSceneChanged(stage, root, "./chatRoom.css");
			}
	    	
		chatField.setOnKeyPressed(e -> {if (e.getCode() == KeyCode.ENTER) {sendButton.getOnAction().handle(null);}});
		sendButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent event) {
			send(chatField.getText());
			chatField.setText("");
			chatField.requestFocus();
		    }
		});
	    	
		stage.setOnCloseRequest(e -> {endButton.getOnAction().handle(null);});
		endButton.setOnAction(event -> {						//endButton check delete
			send("quit" + userName);
			try{Thread.sleep(150);}catch(Exception e){e.printStackTrace();} 	// sleep(150) <- (100)(50)
			Quiz.interrupted();
			try {Thread.sleep(50);} catch(Exception e2) {} 
			Timer.interrupted();
			stopClient();
			stage.close();														// stage.close() <- stage.hide() 
		});	
	   } catch(Exception e) {
		   System.out.println(e);
	   }	   	
	}
	
	private void showSceneChanged(Stage stage, Parent nextRoot, String CSSfilelocation) { 
    	stage.hide();
 
    	scene = new Scene(nextRoot, 780, 600);
    	stage.setTitle("채팅창");
    	scene.getStylesheets().clear();
    	scene.getStylesheets().add(getClass().getResource(CSSfilelocation).toExternalForm());
    	stage.setScene(scene); 
    	
    	stage.show();
    } 
	
	static class Quiz extends Thread{
		public static void startQuiz(String temp) {
			Thread thread = new Thread() {
				public void run() {
					Timer timer = new Timer();
					try {
						timer.startTimer();
						
						chatlog.setText("");
						chatLogtemp = chatlog.getText();	
						chatlog.appendText(quizDB.get(1) + "\n");
						
						sleep(6000);
						
						count++;
						if(count == 6) {
							count = 0;
							timerField.setText("");
							System.out.println("정답 : " + correctResult + ", 오답 : " + incorrectResult);
							send("userResult정답 : " + correctResult + ", 오답 : " + incorrectResult);
							correctResult = 0;
							incorrectResult = 0;
							chatlog.setText(chatLogtemp);
							try {sleep(50);} catch(Exception e) {} Timer.interrupted();
							try {sleep(50);} catch(Exception e) {} Quiz.interrupted();
							return;
						}
						
						try {sleep(100);} catch(Exception e) {}
						if(!temp.equals(userResult)) { incorrectResult++; userResult = ""; }
						send("다음");
					} catch (Exception e) {
						try {sleep(50);} catch(Exception e2) {} 
						Quiz.interrupted();
					}
				}
			};
			
			thread.start();
		}
	}
	
	static class Timer extends Thread{
		public void startTimer() {
			Thread thread = new Thread() {
				public void run() {
					int second = 5;
					while(second >= 0) {						
						try {
							timerField.setText(" " + Integer.toString(second) + "초");
							second--;
							sleep(1000);
						} catch (Exception e) {
							Timer.interrupted();
						}
					}
				}
			};
			
			thread.start();
		}
	}
	
	public static boolean mark(String quizResult) {
		if(result.equals(quizResult)) {
			correctResult++;
			return true;
		}
		
		else {
			incorrectResult++;
			return false;
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
							Quiz.startQuiz(result);
						}
						
						else if(message.length() >= 3 && message.substring(0, 3).equals("m답:")) {
							System.out.println(message.substring(3));
							userResult = message.substring(3);
							if(mark(message.substring(3))) {
								send("정답입니다.\n");
							}
							
							else {
								send("오답입니다.\n");
							}
							
						}
						
						
						else if(message.length() >= 7 && message.substring(0, 7).equals("mDBquiz")) {
							quizlog.setText("퀴즈 목록\n--------\n");
							quizlog.appendText(message.substring(7));
						}
						
						else if(message.length() >= 10 && message.substring(0, 10).equals("userResult")) {
							chatlog.appendText(message.substring(10));
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
					receive();
				} catch (Exception e) {
					if(!socket.isClosed()) {
					stopClient();
					System.out.println("[서버 접속 실패]");
					Platform.exit();
					}
				}
			}
		};
		thread.start();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
