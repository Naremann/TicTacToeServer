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
     PreparedStatement pst;
     boolean result =false;
     int onlinePlayers;
     int offLinePlayers;
    public DataAccessLayer() {
        try {
            DriverManager.registerDriver(new ClientDriver());
            //jdbc:derby://localhost:1527/XODB -> database on mar3y PC
            connection = DriverManager.getConnection("jdbc:derby://localhost:1527/XODB", "root", "root");

        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public String  login(DTOPlayer player,String IP) {
                try
                {
                    System.out.println(String.valueOf(getOnlinePlayers()));
                    PreparedStatement pst = connection.prepareStatement("Select password FROM player where username = ?");
                    pst.setString(1, player.getUserName().toString());
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        if (rs.getString(1).equals(player.getPassword())) {
                            pst = connection.prepareStatement("update player set isavilable = 'online' where username = ?");
                            pst.setString(1, player.getUserName());
                            pst.executeUpdate();
                            pst = connection.prepareStatement("update player set ip = ? where username = ?");
                            pst.setString(1, IP);
                            pst.setString(2, player.getUserName());
                            pst.executeUpdate();
                            return "login successfully";
                        } else {
                            return "Invalid Password please Try again";
                        }
                    } else {
                        return "Invalid username please Sign up";
                    }
                    
            } catch (SQLException ex) {
                Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null; 
        }
     public int getOnlinePlayers() {

        String sql = "select count(username) AS count FROM  player Where isavilable = ? ";
        
         try {
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setString(1, "online");
                onlinePlayers = 0;
               ResultSet rs = pst.executeQuery();
               while (rs.next()) {
                   onlinePlayers = rs.getInt("count");
               }
         } catch (SQLException ex) {
             Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
         }
        
        return onlinePlayers;
    }
     public int getOffLinePlayers() {

        String sql = "select count(username) AS count FROM  player Where isavilable = ? ";
        
         try {
                PreparedStatement pst = connection.prepareStatement(sql);
                pst.setString(1, "offline");
                offLinePlayers = 0;
               ResultSet rs = pst.executeQuery();
               while (rs.next()) {
                   offLinePlayers = rs.getInt("count");
               }
         } catch (SQLException ex) {
             Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
         }
        
        return offLinePlayers;
    }
       
     private void updateIp(String Ip, String userName) throws SQLException {
        String sqlUpdate = "Update player set ip = ? where username = ?";
        pst.setString(1, Ip);
        pst.setString(2, userName);
        PreparedStatement pst = connection.prepareStatement(sqlUpdate);
        int rs = pst.executeUpdate();
    }    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println("Data Access Layer conection Error");
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, "Error closing database connection", ex);
        }
    }
}
