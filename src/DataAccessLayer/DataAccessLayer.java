/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataAccessLayer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dto.DTOPlayer;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.jdbc.ClientDriver;
import tictactoeserver.Server;
import tictactoeserver.db.MyConnection;

/**
 *
 * @author HimaMarey
 */
public class DataAccessLayer {

    private Connection connection;
    PreparedStatement pst;
    boolean result = false;
    int onlinePlayers;
    int offLinePlayers;
    int resul;

    public DataAccessLayer() {
        try {
            DriverManager.registerDriver(new ClientDriver());
            //jdbc:derby://localhost:1527/XODB -> database on mar3y PC
            //jdbc:derby://localhost:1527/player -> database on abo abdo PC
        connection = DriverManager.getConnection("jdbc:derby://localhost:1527/player", "root", "root");
       // connection = MyConnection.getConnection();
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public String register(DTOPlayer player,String ip) {
        String message = null;
        try {
                String statement = "INSERT INTO PLAYER(USERNAME,EMAIL,PASSWORD) VALUES (?,?,?)";
                PreparedStatement stat = connection.prepareStatement(statement);
                stat.setString(1, player.getUserName());
                stat.setString(2, player.getEmail());
                stat.setString(3, player.getPassword());
                if (stat.executeUpdate() <= 0) {
                    message= "can't register";
                } else {
                    message= "registed successfully";
                }
            
        } catch (SQLException ex) {
            message="Error: "+ex.getLocalizedMessage();
        }
        return message;
        
    }

    public String login(DTOPlayer player, String IP) {
        try {
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
    }//***********************
   /*  public String online_player() {
         System.out.println("online play");
         List<DTOPlayer>onlineplayer=null;
             
        // private static final String ONLINE_QUERY = "SELECT * FROM PLAYER WHERE ISAVILABLE = 'online' ";

        try {
          if(connection !=null){
               onlineplayer=new ArrayList<>();
            if (MyConnection.isDbConnected(connection)) {
                String ONLINE_QUERY="SELECT FROM ROOT PLAYER WHERE ISAVAILABLE='online'";
               try ( pst  selectOnline= connection.prepareStatement(ONLINE_QUERY);
                 ResultSet resul =selectOnline.executeUpdate()){
                while( resul.next()){
                String userName= resul.getString("USERNAME");
                String email=resul.getString("EMAIL");
                String password=resul.getString("PASSWORD");
                int scoure=resul.getInt("SCOURE");
                String isavilable=resul.getString("ISAVILABLE");
                String ip=resul.getString("IP");
                System.out.println(isavilable);
                DTOPlayer player=new DTOPlayer (userNam,email,password,scoure,isavilable,ip);
                online
               
               
                   }
                }
                
                }
                preparedStatement.close();
                connection.close();

            }
        } catch (SQLException ex) {
            AlertMessage.infoBox(ex.getLocalizedMessage(), "Error!", null);

        }

        return result;  }*/
    
     public String getOnlinePlayers()  {

        ArrayList<DTOPlayer> onlinePlayers = new ArrayList<>();

        String sql = " SELECT * FROM player where ISAVILABLE ='online' ";
        PreparedStatement pst;
        try {
            pst = connection.prepareStatement(sql);
             ResultSet resultSet = pst.executeQuery();
        Gson gson = new GsonBuilder().create();
        while (resultSet.next()) {
            onlinePlayers.add(new DTOPlayer(
                    
                    resultSet.getString("username"),
                    resultSet.getString("email"),
                    resultSet.getString("password"),
                    resultSet.getString("ISAVILABLE")
            ));
        }
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }

       Gson gson = new GsonBuilder().create();
            JsonObject setJson =new JsonObject();
            setJson.addProperty("key","onlinePlayers");
            JsonArray playersArray = gson.toJsonTree(onlinePlayers).getAsJsonArray();
            setJson.add("onlinePlayersList", playersArray);

            String jsonString = gson.toJson(setJson);
            
           // System.out.println("Result: " + setJson);
         System.out.println("******************"+onlinePlayers.size());
        return jsonString;
    }

    
    
//*************************
    public int getCountOnlinePlayers() {

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
