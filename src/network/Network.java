/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import DataAccessLayer.DataAccessLayer;
import com.google.gson.JsonObject;
import dto.DTOPlayer;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HimaMarey
 */
public class Network 
{
    DataAccessLayer dataAccessLayer;
     boolean found = false ;
    public Network()
    {
        dataAccessLayer =new DataAccessLayer();
        
    }
    
    public boolean login(JsonObject json) 
    {
       

        if (json.has("username")&&json.has("password")) {
            
            String username = json.get("username").getAsString();
            String password = json.get("password").getAsString();
            DTOPlayer player = new DTOPlayer(username, password);
            System.out.println("Done with signUp");
            System.out.println(player);
            System.out.println(player.getUserName());
            System.out.println(username + password);
            found = dataAccessLayer.login(player);
        }else {
            System.out.println("Incomplete or malformed JSON payload for signup");
            System.out.println("Received JSON payload: " + json.toString());
            
        }
        return found;
    }
    
    
}
