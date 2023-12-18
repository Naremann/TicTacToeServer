package tictactoeserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;

public class ServerUI extends BorderPane {
    protected final FlowPane flowPane;
    protected final Label online_member;
    protected final Label online_member_num;
    protected final Label offline_member;
    protected final Label offline_member_num;
    protected final Button serverBtn;
    protected boolean turnServer;
    private ServerHandler serverHandler;
    private boolean serverRunning = false;
    ServerSocket serverSocket;
    Socket socket ;
    public ServerUI() {
        turnServer = false;
        flowPane = new FlowPane();
        online_member = new Label();
        online_member_num = new Label();
        offline_member = new Label();
        offline_member_num = new Label();
        serverBtn = new Button();
        setMaxHeight(USE_PREF_SIZE);
        setMaxWidth(USE_PREF_SIZE);
        setMinHeight(USE_PREF_SIZE);
        setMinWidth(USE_PREF_SIZE);
        setPrefHeight(400.0);
        setPrefWidth(600.0);

        BorderPane.setAlignment(flowPane, javafx.geometry.Pos.CENTER);
        flowPane.setPrefHeight(73.0);
        flowPane.setPrefWidth(600.0);

        online_member.setText("Online Player");
        online_member.setFont(new Font(18.0));
        FlowPane.setMargin(online_member, new Insets(24.0));

        online_member_num.setText("Label");
        FlowPane.setMargin(online_member_num, new Insets(24.0));
        online_member_num.setFont(new Font(18.0));

        offline_member.setText("Offline Player");
        offline_member.setFont(new Font(18.0));
        FlowPane.setMargin(offline_member, new Insets(24.0, 24.0, 24.0, 72.0));

        offline_member_num.setText("Label");
        offline_member_num.setFont(new Font(18.0));
        FlowPane.setMargin(offline_member_num, new Insets(24.0));
        setTop(flowPane);

        BorderPane.setAlignment(serverBtn, javafx.geometry.Pos.CENTER);
        serverBtn.setMnemonicParsing(false);
        serverBtn.setPrefHeight(51.0);
        serverBtn.setPrefWidth(126.0);
        serverBtn.setStyle("-fx-background-color: #FFA500;");
        serverBtn.setText("Start Server");
        serverBtn.setTextFill(javafx.scene.paint.Color.valueOf("#fdfbfb"));
        serverBtn.setFont(new Font(18.0));
        setCenter(serverBtn);
        serverBtn.setOnAction(event ->{
            serverRunning=!serverRunning;
            if(serverRunning)
            {
                try {
                    serverSocket = new ServerSocket(4000);
                    new Thread(()->{
                         try {
                             while(serverRunning)
                             {
                                socket = serverSocket.accept();
                                serverHandler = new ServerHandler(socket);
                                System.out.println(socket.getInetAddress());  
                             }
                                
                            } catch (IOException ex) {
                                Logger.getLogger(ServerUI.class.getName()).log(Level.SEVERE, null, ex);
                            }
                    
                    }).start();
                    serverBtn.setText("Stop Server");
                } catch (IOException ex) {
                    serverHandler.closeResources();
                    ex.printStackTrace();
                }
            }
            else
            {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        serverHandler.closeResources();
                        serverSocket.close();
                        serverBtn.setText("Start Server");
                        System.out.println("stop");
                    } catch (IOException ex) {
                        serverHandler.closeResources();
                        Logger.getLogger(ServerUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }   
        });

        flowPane.getChildren().add(online_member);
        flowPane.getChildren().add(online_member_num);
        flowPane.getChildren().add(offline_member);
        flowPane.getChildren().add(offline_member_num);

    }
}
