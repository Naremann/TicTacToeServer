/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author user
 */
public class TicTacToeServer extends Application  {

   
    public static void main(String[] args) 
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
         Parent root = new ServerUI(); 
            String image = TicTacToeServer.class.getResource("app.jpg").toExternalForm();
        root.setStyle("-fx-background-image: url('" + image + "'); "+
                 "-fx-background-size: 100% 100%;"+
                 "-fx-background-position: center center;"
                );
        Scene scene = new Scene(root);
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
}
