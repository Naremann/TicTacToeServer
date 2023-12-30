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
import dto.DTORequest;
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

    public Connection connection;
    PreparedStatement pst;
    boolean result = false;
    int onlinePlayers;
    int offLinePlayers;
    int onGamePlayers;
    int resul;

    public DataAccessLayer() {
        
         // DriverManager.registerDriver(new ClientDriver());
            //jdbc:derby://localhost:1527/XODB -> database on mar3y PC
            //jdbc:derby://localhost:1527/player -> database on abo abdo PC

           connection = MyConnection.getConnection();
   
    }

    public String register(DTOPlayer player, String ip) {
        String message = null;
        try {
            String statement = "INSERT INTO PLAYER(USERNAME,EMAIL,PASSWORD,SCORE) VALUES (?,?,?,?)";
            PreparedStatement stat = connection.prepareStatement(statement);
            stat.setString(1, player.getUserName());
            stat.setString(2, player.getEmail());
            stat.setString(3, player.getPassword());
            stat.setInt(4, 0);
            if (stat.executeUpdate() <= 0) {
                message = "can't register";
            } else {
                message = "registed successfully";
            }

        } catch (SQLException ex) {
            message = "Error: " + ex.getLocalizedMessage();
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

    public String getOnlinePlayers() {

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
                        resultSet.getString("ISAVILABLE"),
                        resultSet.getInt("SCORE")
                ));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        Gson gson = new GsonBuilder().create();
        JsonObject setJson = new JsonObject();
        setJson.addProperty("key", "onlinePlayers");
        JsonArray playersArray = gson.toJsonTree(onlinePlayers).getAsJsonArray();
        setJson.add("onlinePlayersList", playersArray);
        String jsonString = gson.toJson(setJson);
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
    
    public int getOnGamePlayers() {

        String sql = "select count(username) AS count FROM  player Where isavilable = ? ";

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            pst.setString(1, "onGame");
            onGamePlayers = 0;
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                onGamePlayers = rs.getInt("count");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }

        return onGamePlayers;
    }

    private void updateIp(String Ip, String userName) throws SQLException {
        String sqlUpdate = "Update player set ip = ? where username = ?";
        pst.setString(1, Ip);
        pst.setString(2, userName);
        PreparedStatement pst = connection.prepareStatement(sqlUpdate);
        int rs = pst.executeUpdate();
    }

    public boolean setPlayersStatus(String userName,String status) {
        try {
            String sqlUpdate = "Update player set ISAVILABLE = ? where username = ?";
            PreparedStatement pst = connection.prepareStatement(sqlUpdate);
            pst.setString(1,status );
            pst.setString(2, userName);
            int rs = pst.executeUpdate();
            return rs != 0;
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return false;
        }

    }
    
    public boolean updatePlayerScore(String userName,int score) {
        try {
             score +=getPlayerScore(userName);
             System.out.println(score);
             System.out.println(getPlayerScore(userName));
            String sqlUpdate = "Update player set score = ? where username = ?";
            PreparedStatement pst = connection.prepareStatement(sqlUpdate);
            pst.setInt(1,score );
            pst.setString(2, userName);
            int rs = pst.executeUpdate();
            return rs != 0;
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return false;
        }

    }
    
    public int getPlayerScore(String userName)
    {
        int score=0;
        try {
            String sqlGetScore = "select score from player where username = ? ";
            PreparedStatement psScore = connection.prepareStatement(sqlGetScore);
            psScore.setString(1, userName);
            ResultSet rs = psScore.executeQuery();
            while(rs.next())
            {
                return rs.getInt("score");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DataAccessLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return score;  
    }

    public String insertRequests(DTORequest requset, String ip) {
        String message = null;
        try {
            String statement = "INSERT INTO REQUEST(USERNAMESENDER,USERNAMERECIVER) VALUES (?,?)";
            PreparedStatement stat = connection.prepareStatement(statement);
            stat.setString(1, requset.getUserNameSender());
            stat.setString(2, requset.getUserNameReceiver());

            if (stat.executeUpdate() <= 0) {
                message = "can't send invite";
            } else {
                message = "Invite Sent Successfully";
            }

        } catch (SQLException ex) {
            message = "Error: " + ex.getLocalizedMessage();
        }
        return message;

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
