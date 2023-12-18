/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataAccessLayer;

import dto.DTOPlayer;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.jdbc.ClientDriver;
import tictactoeserver.Server;

/**
 *
 * @author HimaMarey
 */
public class DataAccessLayer 
{
     private Connection connection;
     boolean result =false;
     
    public DataAccessLayer() {
        try {
            DriverManager.registerDriver(new ClientDriver());
            //jdbc:derby://localhost:1527/XODB -> database on mar3y PC
            connection = DriverManager.getConnection("jdbc:derby://localhost:1527/XODB", "root", "root");

        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public boolean  login(DTOPlayer player) {
            try {
                String statement = "SELECT * FROM PLAYER WHERE username = ? AND PASSWORD = ?";
                PreparedStatement stat = connection.prepareStatement(statement);
                stat.setString(1, player.getUserName());
                stat.setString(2, player.getPassword());
                ResultSet rs = stat.executeQuery();
                if (rs.next()) {
                    System.out.println(rs.next());
                    result = true;
                } else {
                    result= false;
                }
            } catch (SQLException ex) {
                System.out.println("Databasecheck error: " + ex.getMessage());
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
           return result; 
        }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println("errooooooeeee!!!!");
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, "Error closing database connection", ex);
        }
    }
}
