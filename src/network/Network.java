/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network;

import DataAccessLayer.DataAccessLayer;
import com.google.gson.JsonObject;
import dto.DTOPlayer;
import dto.DTORequest;
import java.sql.SQLException;
import java.util.ArrayList;
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
    
    public String login(DTOPlayer player, String IP) 
    {
        return dataAccessLayer.login(player, IP );
    }
    
    public String register(DTOPlayer player,String ip){
        return dataAccessLayer.register(player, ip);
    }
    
    public String onlinePlayers(){
        return dataAccessLayer.getOnlinePlayers();
    }
    
    public String request(DTORequest req,String ip){
        return dataAccessLayer.insertRequests(req,ip);
    }
    
    
}
