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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ChatClient extends Application{
	static Socket socket;
	static HashMap<Integer, String> quizDB = new HashMap<Integer, String>();
	static String result = "";
	static int count = 0; static int correctResult = 0; static int incorrectResult = 0;
	static String userResult = "";
	static String chatLogtemp = "";
	
	Scene scene;
	static Label userNameLabel = new Label("유저 이름");
	static TextField userNameField = new TextField();
	static Button dialogbutton = new Button("접속");
	HBox preRoot = new HBox(3.,userNameLabel,userNameField,dialogbutton);
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

	@Override
	public void start(Stage stage) {
	   	try {
    		preRoot.setId("loginhbox");			//set css seleter for tag
    		root.setId("chatvbox");
    		userNameLabel.setId("loginlabel");
	   		
	   		preRoot.setAlignment(Pos.CENTER);;
	   	    
    		root.setPadding(new Insets(10));					 // set inner space
    		root.setSpacing(10); 								// set space each other in VBox 
		   	rootMessage.setEditable(false);										//textArea in vBox
	    	rootMessage.prefWidthProperty().bind(stage.widthProperty());
	   
	    	chatlog.setEditable(false);
	    	chatlog.setWrapText(true); 	    	
	    	chatlog.prefWidthProperty().bind(stage.widthProperty());			// grid in vBOx 0022  //textArea
	    	chatlog.prefHeightProperty().bind(stage.heightProperty());	    	
	    	loging.setEditable(false);											// grid in vBOx 2011	//textArea
	    	loging.prefWidthProperty().bind(stage.widthProperty());
	    	loging.prefHeightProperty().bind(stage.heightProperty());    	
	    	quizlog.setEditable(false);											// grid in vBox 2111	//textArea
	    	quizlog.prefWidthProperty().bind(stage.widthProperty());
	    	quizlog.prefHeightProperty().bind(stage.heightProperty());
		    
	    	chatField.prefWidthProperty().bind(stage.widthProperty());			// grid in vBox 0211	//textfield
	    	sendButton.prefWidthProperty().bind(buttonBar.widthProperty());
	    	endButton.prefWidthProperty().bind(buttonBar.widthProperty());    
	    	
	    	timerField.setEditable(false);
	    	timerField.prefWidthProperty().bind(serverBar.widthProperty());
	    	ipField.prefWidthProperty().bind(serverBar.widthProperty());
	    	portField.prefWidthProperty().bind(serverBar.widthProperty());
	    		
	    	grid.setVgap(10);
	    	grid.setHgap(10);	    	
	    	grid.add(chatlog, 0, 0, 2, 2);	    	
	    	grid.add(loging, 2, 0, 1, 1); 
	    	grid.add(quizlog, 2, 1, 1, 1);
	    	grid.add(chatField, 0, 2, 1, 1);
	    	grid.add(buttonBar, 2, 2, 1, 1);				// grid in vBox 1211	//button	    		    	

	    	
    		userNameField.setOnKeyPressed(e -> {if (e.getCode() == KeyCode.ENTER) {dialogbutton.getOnAction().handle(null);}});
	    	dialogbutton.setOnAction(new EventHandler<ActionEvent>() {  @Override
	    	    public void handle(ActionEvent event) {
	    	    	if( userNameField.getText() != null) {
	    	    	    System.out.println("이름 : " + userNameField.getText());
	    	    	    if(userNameField.getText().equals("")) {
	    	    	    	userName = "name:익명";
	    	    	    	send(userName);
	    	    	    }
	    	    	    else {
	    		    	    userName = "name:" + userNameField.getText();
	    		    	    send(userName);
	    	    	    }
	    	    	}
						showSceneChanged(stage,root);
	    	    }});	    
	    	
	    	 
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
	    	endButton.setOnAction(new EventHandler<ActionEvent>() {
	    	    @Override
	    	    public void handle(ActionEvent event) {
	    	    	send("q" + userName);
	    	    	try{
	    	    		Thread.sleep(100);
	    	    	}catch(Exception e){
	    	    		e.printStackTrace();
	    	    	}
	    	    	stopClient();
    	    	stage.hide();
	    	    }
	    	});	
	    	
	    	
	    	Scene scene = new Scene(preRoot, 320, 240);
	    	stage.setTitle("접속 창");
	    	scene.getStylesheets().clear();
	    	scene.getStylesheets().add(getClass().getResource("./login.css").toExternalForm());

	    	stage.setScene(scene);
	    	stage.show();	
	    	
    	
    	} catch(Exception e) {
    		System.out.println(e);
    	}
	}
	private void showSceneChanged(Stage stage,Parent nextRoot) { 
    	stage.hide();
 
    	scene = new Scene(nextRoot, 780, 600);
    	stage.setTitle("채팅창");
    	scene.getStylesheets().clear();
    	scene.getStylesheets().add(getClass().getResource("./chatRoom.css").toExternalForm());
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
						
						else if(message.length() >= 9 && message.substring(0, 9).equals("userResult")) {
							chatlog.appendText(message.substring(9));
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
					System.out.println("[서버 접속 성공]");
					chatlog.appendText("[서버 접속 성공]" + "\n");
	    	    	chatField.requestFocus();
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
		startClient("192.168.43.206", 8888);
		launch(args);
	}
}
