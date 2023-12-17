package tictactoeserver;

import java.io.IOException;
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
    private Server server;
    private boolean serverRunning = false;
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
            if (!serverRunning) {
                serverBtn.setText("Stop Server");
                startServer();
            } else {
                stopServer();
                serverBtn.setText("Start Server");
            }    
        });

        flowPane.getChildren().add(online_member);
        flowPane.getChildren().add(online_member_num);
        flowPane.getChildren().add(offline_member);
        flowPane.getChildren().add(offline_member_num);

    }
    private void startServer() {
        serverRunning = true;
        server = new Server();
    }

    private void stopServer() {
        serverRunning = false;
        // Perform any cleanup or shutdown logic for your server here
        // For example, you can close the server socket
        if (server != null) {
            server.turnServer = false;
            try {
                server.socket1.close();
            } catch (IOException e) {
                System.out.println("Error closing server socket: " + e.getMessage());
            }
        }
    }
}
