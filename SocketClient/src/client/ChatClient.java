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
	static String userResultLog = "";
	static String chatLogtemp = "";
	
	Scene scene;
	static Label userNameLabel = new Label("���� �̸�");
	static TextField userNameField = new TextField();
	static Button dialogbutton = new Button("����");
	HBox preRoot = new HBox(3.,userNameLabel,userNameField,dialogbutton);
   	
   	
	static TextField ipField = new TextField();
	static TextField portField = new TextField();
	static TextField timerField = new TextField();
	HBox serverBar = new HBox(3.,ipField,portField,timerField);
	
   	Button sendButton = new Button("�� ��");
   	Button endButton = new Button("�� �� �� ��");
   	HBox buttonBar = new HBox(2. ,sendButton,endButton);	

   	static TextArea chatlog = new TextArea();
   	static TextField chatField = new TextField();
   	static TextArea loging = new TextArea("���� ���� ���\n------------\n");
   	static TextArea quizlog = new TextArea("���� ���\n--------\n");
   	GridPane grid = new GridPane();
   	
	VBox root = new VBox(2.,serverBar,grid);
 
  	static String userName = null;
   	static Dialog<setData> dialog = new Dialog<>();
   	static String serverIP = null;
   	static String serverPort = null;
	
	@Override
	public void start(Stage stage) {
	   	try {	   		
    		root.setId("chatvbox");
	   		
		   	root.setPadding(new Insets(10)); // ���� ���� ����
		   	root.setSpacing(10); // ��Ʈ�� ���� ���� ���� ����
	   
		   	chatlog.setOpacity(0.8);
	    	chatlog.setEditable(false);
	    	chatlog.setWrapText(true); 	    	
	    	chatlog.prefWidthProperty().bind(stage.widthProperty());			// grid in vBOx 0022  //textArea
	    	chatlog.prefHeightProperty().bind(stage.heightProperty());	
	    	
	    	loging.setOpacity(0.8);
	    	loging.setEditable(false);											// grid in vBOx 2011	//textArea
	    	loging.prefWidthProperty().bind(stage.widthProperty());
	    	loging.prefHeightProperty().bind(stage.heightProperty());  
	    	
	    	quizlog.setOpacity(0.8);
	    	quizlog.setEditable(false);											// grid in vBox 2111	//textArea
	    	quizlog.prefWidthProperty().bind(stage.widthProperty());
	    	quizlog.prefHeightProperty().bind(stage.heightProperty());
		    
	    	chatField.setOpacity(0.8);
	    	chatField.prefWidthProperty().bind(stage.widthProperty());			// grid in vBox 0211	//textfield
	    	sendButton.prefWidthProperty().bind(buttonBar.widthProperty());
	    	endButton.prefWidthProperty().bind(buttonBar.widthProperty());    
	    	
	    	timerField.setOpacity(0.8);
	    	timerField.setEditable(false);
	    	timerField.prefWidthProperty().bind(serverBar.widthProperty());
	    	ipField.setOpacity(0.8);
	    	ipField.prefWidthProperty().bind(serverBar.widthProperty());
	    	portField.setOpacity(0.8);
	    	portField.prefWidthProperty().bind(serverBar.widthProperty());
	
	    	grid.setVgap(10);
	    	grid.setHgap(10);	    	
	    	grid.add(chatlog, 0, 0, 2, 2);	    	
	    	grid.add(loging, 2, 0, 1, 1); 
	    	grid.add(quizlog, 2, 1, 1, 1);
	    	grid.add(chatField, 0, 2, 1, 1);
	    	grid.add(buttonBar, 2, 2, 1, 1);
		    	
	    	dialog.setTitle("�̸� �Է� â");
	    	dialog.setHeaderText(null);
	    	dialog.setContentText(null);
	    	dialog.setContentText("�̸� : ");

	    	ButtonType okButton = new ButtonType("OK", ButtonData.OK_DONE);
	    	dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);
	    	
	    	ipField.setEditable(false);
	    	portField.setEditable(false);
	    	TextField ipField2 = new TextField();
	    	ipField2.setText("192.168.0.1");
	    	TextField portField2 = new TextField();
	    	portField2.setText("8888");
	    	TextField nameField2 = new TextField();
	    	nameField2.setText("�͸�");
	    	
	    	Label IP = new Label("IP : ");
	    	IP.setMinWidth(80);
	    	IP.setTextFill(Color.web("#FFFFFF"));
	    	Label PORT = new Label("PORT : ");
	    	PORT.setMinWidth(80);
	    	PORT.setTextFill(Color.web("#FFFFFF"));
	    	Label NAME = new Label("NAME : ");
	    	NAME.setMinWidth(80);
	    	NAME.setTextFill(Color.web("#FFFFFF"));
	    	
	    	HBox IP_text = new HBox(2., IP, ipField2);
	    	IP_text.setAlignment(Pos.CENTER);
	    	HBox PORT_text = new HBox(2., PORT, portField2);
	    	PORT_text.setAlignment(Pos.CENTER);
	    	HBox NAME_text = new HBox(2., NAME, nameField2);
	    	NAME_text.setAlignment(Pos.CENTER);
	    	VBox dialogPane = new VBox(3.,IP_text, PORT_text, NAME_text);
	    	
	    	dialogPane.setId("dialogPane");   	
	    	dialogPane.setPadding(new Insets(10, 10, 10, 10));
	    	dialogPane.setSpacing(15);
	    	
	    	dialogPane.setPrefWidth(400);
	    	dialogPane.setPrefHeight(200);
	    	dialog.getDialogPane().setContent(dialogPane);
	    	dialogPane.getStylesheets().clear();
	    	dialogPane.getStylesheets().add(getClass().getResource("./login.css").toExternalForm());
	    	dialogPane.setAlignment(Pos.CENTER);
	    	dialog.setResultConverter(new Callback<ButtonType, setData>() {	
				@Override
				public setData call(ButtonType ok) {					
					if(ok == okButton) {
						return new setData(ipField2.getText(), portField2.getText(), nameField2.getText());
					}
		    		
					return null;
				}
	    	});
	    	
	    	Optional<setData> result = dialog.showAndWait();
	    	if(result.isPresent()) {
	    		System.out.println(ipField2.getText() + " " + nameField2.getText() + " " + portField2.getText());
	    		
	    		serverIP = ipField2.getText();
	    		ipField.setText(ipField2.getText());
	    		serverPort = portField2.getText();
	    		portField.setText(portField2.getText());
	    		userName = "name:" + nameField2.getText();
	    		showSceneChanged(stage, root);
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
	    	
	    	endButton.setOnAction(new EventHandler<ActionEvent>() {
	    	    @Override
	    	    public void handle(ActionEvent event) {
	    	    	if(endButton.getText().equals("�� �� �� ��")) {
	    	    		startClient(ipField.getText(), Integer.parseInt(portField.getText()));
	    	    		Platform.runLater(() -> {
	    	    			chatlog.appendText("[ ä�ù� ���� ]\n");
	    	    		});
	    	    		endButton.setText("�� �� �� ��");
	    	    		try {Thread.sleep(200);} catch(Exception e2) {} 
	    	    		send(userName);
	    	    	}
	    	    	
	    	    	else if(endButton.getText().equals("�� �� �� ��")){
		    	    	send("quit" + userName);
		    	    	try{
		    	    		Thread.sleep(100);
		    	    	}catch(Exception e){
		    	    		e.printStackTrace();
		    	    	}
		    	    	try {Thread.sleep(50);} catch(Exception e2) {} 
						Quiz.interrupted();
						try {Thread.sleep(50);} catch(Exception e2) {} 
						Timer.interrupted();
						try {Thread.sleep(50);} catch(Exception e2) {} 
		    	    	stopClient();
		    	    	try {Thread.sleep(50);} catch(Exception e2) {} 
		    	    	stage.hide();
	    	    	}
	    	    }
	    	});
	    	
	    	stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	            public void handle(WindowEvent we) {
	            	if(endButton.getText().equals("�� �� �� ��")) {
		    	    	try{
		    	    		Thread.sleep(100);
		    	    	}catch(Exception e){
		    	    		e.printStackTrace();
		    	    	}
		    	    	stopClient();
		    	    	stage.hide();
	    	    	}
	    	    	
	    	    	else if(endButton.getText().equals("�� �� �� ��")){
		    	    	send("quit" + userName);
		    	    	try{
		    	    		Thread.sleep(100);
		    	    	}catch(Exception e){
		    	    		e.printStackTrace();
		    	    	}
		    	    	try {Thread.sleep(50);} catch(Exception e2) {} 
						Quiz.interrupted();
						try {Thread.sleep(50);} catch(Exception e2) {} 
						Timer.interrupted();
						try {Thread.sleep(50);} catch(Exception e2) {} 
		    	    	stopClient();
		    	    	try {Thread.sleep(50);} catch(Exception e2) {} 
		    	    	stage.hide();
	    	    	}
	            }
	    	});
    	
    	} catch(Exception e) {
    		System.out.println(e);
    	}
	   	
	}
	
	private void showSceneChanged(Stage stage, Parent nextRoot) { 
    	stage.hide();
 
    	scene = new Scene(nextRoot, 780, 600);
    	stage.setTitle("ä��â");
    	scene.getStylesheets().clear();
    	scene.getStylesheets().add(getClass().getResource("./chatRoom.css").toExternalForm());
    	stage.setScene(scene); 
    	
    	stage.show();
    } 
	
	static class Quiz extends Thread{
		public static void startQuiz(String temp, String table) {
			Thread thread = new Thread() {
				public void run() {
					Timer timer = new Timer();
					try {
						if(count == 5) {
							count = 0;
							timerField.setText("");
							userResultLog = "���� : " + correctResult + ", ���� : " + incorrectResult;
							System.out.println("���� : " + correctResult + ", ���� : " + incorrectResult);
							try {sleep(50);} catch(Exception e) {} Timer.interrupted();
							send("endquiz");
							chatlog.appendText(userName.substring(5) + " > " + "���� : " + correctResult + ", ���� : " + incorrectResult + "\n");
							correctResult = 0;
							incorrectResult = 0;
							try {sleep(50);} catch(Exception e) {} Quiz.interrupted();
							return;
						}
						
						timer.startTimer();
						
						chatlog.setText("");
						chatlog.appendText(quizDB.get(1) + "\n");
							
						sleep(6000);
						count++;
						
						try {sleep(100);} catch(Exception e) {}
						if(!temp.equals(userResult)) { incorrectResult++; userResult = ""; }
						send("����/" + table);
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
		public synchronized void startTimer() {
			Thread thread = new Thread() {
				public void run() {
					int second = 5;
					while(second >= 0) {						
						try {
							timerField.setText(" " + Integer.toString(second) + "��");
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
		
		else if(!result.equals(quizResult)){
			incorrectResult++;
			return false;
		}
		
		return false;
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
	
	public static synchronized void receive() {
		while(true) {
			try {
				InputStream in = socket.getInputStream();
				byte[] buffer = new byte[2048];
				int length = in.read(buffer);
				if(length == -1) throw new IOException();
				String message = new String(buffer, 0, length, "UTF-8");

				Platform.runLater(()->{
					if(message.length() >= 1 && message.substring(0,1).equals("m")) {
						if(message.length() >= 5 && message.substring(0, 5).equals("mquiz")) {
							quizDB.put(1, message.split("\\^")[2]);
							result = message.split("\\^")[3];
							System.out.println(result);
							Quiz.startQuiz(result, message.split("\\^")[0].substring(5));
						}
						
						else if(message.length() >= 14 && message.substring(0, 14).equals("mresultrequest")) {
							send("resultsend" + userName.substring(5) + " > " +  userResultLog);
							try {
								Thread.sleep(100);
							} catch(Exception e) {
								System.out.println("sleep ����");	
							}
						}
						
						
						else if(message.length() >= 7 && message.substring(0, 7).equals("mDBquiz")) {
							quizlog.setText("���� ���\n--------\n");
							quizlog.appendText(message.substring(7));
						}
						
						else if(message.length() >= 4 && message.substring(0, 4).equals("m����/")) {}
						
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
					
					else if(message.length() >= 7 && message.substring(0, 7).equals("setName")) {
						userName = message.substring(7);
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
					String temp = new String(buffer, "UTF-8");
					System.out.println(temp);
					if(temp.length() >= 2 && temp.substring(0,2).equals("��:")) {
						System.out.println(temp);
						userResult = temp.substring(2);
						mark(temp.substring(2));
					}
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
					System.out.println("[���� ���� ����]");
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
