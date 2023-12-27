package tictactoeserver;

import DataAccessLayer.DataAccessLayer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class ServerUI extends BorderPane {
    protected final FlowPane flowPane;
    protected final FlowPane flowBtn;
    protected final Label online_member;
    protected final Label online_member_num;
    protected final Label offline_member;
    protected final Label offline_member_num;
    protected final Button serverBtn;
    protected final PieChart pieChart;
    private boolean serverRunning;
    DataAccessLayer dataAccessLayer ;
    Server server;
    Thread statusThread;
    public ServerUI() {
        serverRunning = true;
        flowPane = new FlowPane();
        flowBtn=new FlowPane();
        online_member = new Label();
        online_member_num = new Label();
        offline_member = new Label();
        offline_member_num = new Label();
        serverBtn = new Button();
        pieChart = new PieChart();
        dataAccessLayer= new DataAccessLayer();
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

        online_member_num.setText(String.valueOf(dataAccessLayer.getCountOnlinePlayers()));
        FlowPane.setMargin(online_member_num, new Insets(24.0));
        online_member_num.setFont(new Font(18.0));

        offline_member.setText("Offline Player");
        offline_member.setFont(new Font(18.0));
        FlowPane.setMargin(offline_member, new Insets(24.0, 24.0, 24.0, 72.0));

        offline_member_num.setText(String.valueOf(dataAccessLayer.getOffLinePlayers()));
        offline_member_num.setFont(new Font(18.0));
        FlowPane.setMargin(offline_member_num, new Insets(24.0));
        setTop(flowPane);
        initPieChart();
        BorderPane.setAlignment(pieChart, Pos.CENTER_LEFT);
        BorderPane.setAlignment(serverBtn, javafx.geometry.Pos.CENTER_RIGHT);
        BorderPane.setMargin(serverBtn, new Insets(24.0));
        serverBtn.setLayoutX(70.0);
        serverBtn.setLayoutY(70.0);
        serverBtn.setMnemonicParsing(false);
        serverBtn.setPrefHeight(51.0);
        serverBtn.setPrefWidth(126.0);
        serverBtn.setStyle("-fx-background-color: #FFA500;");
        serverBtn.setText("Start Server");
        serverBtn.setTextFill(javafx.scene.paint.Color.valueOf("#fdfbfb"));
        serverBtn.setFont(new Font(18.0));
        setCenter(serverBtn);
        serverBtn.setOnAction(event ->{
            pieChart.setVisible(true);
            if(serverRunning)
            {
                server = new Server();
                serverRunning=!serverRunning;
                serverBtn.setText("Stop Server");
                statusThread = new Thread(() -> {
                    try {
                       
                        while (true) {
                            updateStatus(pieChart);
                            TimeUnit.MICROSECONDS.sleep(500);
                        }
                    } catch (InterruptedException e) {
                        // Handle interruption
                        System.out.println("Exiting loop");
                    } catch (Exception e) {
                        e.printStackTrace(); // Handle the exception appropriately
                       // Platform.runLater(() -> startStopButton.setText("Start"));
                    }
                });
                statusThread.start();
            }
            else
            {
                try {
                    statusThread.stop();
                    server.closeConnection();
                    serverRunning=!serverRunning;
                    serverBtn.setText("Start Server");
                    
                } catch (IOException ex) {
                    Logger.getLogger(ServerUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }   
        });
        flowPane.getChildren().add(online_member);
        flowPane.getChildren().add(online_member_num);
        flowPane.getChildren().add(offline_member);
        flowPane.getChildren().add(offline_member_num);
        
    }
    private void updateStatus(PieChart pieChart) {
        if (true) {
            // Fetch online, offline, busy values from the database
            int onlineValue = dataAccessLayer.getCountOnlinePlayers();
            int offlineValue = dataAccessLayer.getOffLinePlayers();
//            System.out.println(onlineValue);
//            System.out.println(offlineValue);
            // int busyValue = dataAccessLayer.getbusyeRate();
            // Update the labels and pie chart on the JavaFX Application Thread
            Platform.runLater(() -> {
                online_member_num.setText(String.valueOf(onlineValue));
                offline_member_num.setText(String.valueOf(offlineValue));
                // busy.setText(String.valueOf(busyValue));
                
                updatePieChart(onlineValue, offlineValue, 0);
                
            });
        }
    }

    private void updatePieChart(int onlineValue, int offlineValue, int busyValue) {
        ObservableList<PieChart.Data> pieChartData = pieChart.getData();
        pieChartData.get(0).setPieValue(onlineValue);
        pieChartData.get(1).setPieValue(offlineValue);
        //pieChartData.get(2).setPieValue(busyValue);
        setCustomColors();
    }
    private void setCustomColors() {

        // Get the data slices
        PieChart.Data[] dataSlices = new PieChart.Data[pieChart.getData().size()];
        pieChart.getData().toArray(dataSlices);

        // Set custom colors for each data slice
        for (PieChart.Data dataSlice : dataSlices) {
            String style = "-fx-pie-color: #18317;"; // Default style
            if (dataSlice.getName().equals("Online")) {
                style = "-fx-pie-color: #1577FF;"; // Online color
            }
//            else if (dataSlice.getName().equals("Busy")) {
//                style = "-fx-pie-color: #8B91B5;"; // Busy color
//            } 
            else if (dataSlice.getName().equals("Offline")) {
                style = "-fx-pie-color: #FF8FDA;"; // Offline color
            }
            dataSlice.getNode().setStyle(style);
            // System.out.println("Style applied for " + dataSlice.getName() + ": " + style);
        }
    }
    private void initPieChart() {
        pieChart.setLayoutX(435.0);
        pieChart.setLayoutY(62.0);
        pieChart.setPrefHeight(258.0);
        pieChart.setPrefWidth(366.0);
        
        int onlineValue =Integer.parseInt(online_member_num.getText());
        int offlineValue =Integer.parseInt(offline_member_num.getText());
        //int busyValue = Integer.parseInt(busy.getText());

        pieChart.getData().addAll(
                new PieChart.Data("Online", onlineValue),
                new PieChart.Data("Offline", offlineValue)
               // new PieChart.Data("Busy", busyValue)
        );
        //traverseSceneGraph(pieChart, Color.WHITE);
        BorderPane.setAlignment(pieChart, Pos.CENTER_LEFT);
        setCustomColors();
    }
//private void traverseSceneGraph(PieChart chart, Color color) {
//        for (Node node : chart.lookupAll(".text.chart-pie-label")) {
//            if (node instanceof Text) {
//                ((Text) node).setFill(color);
//            }
//        }
//    }
    
}
