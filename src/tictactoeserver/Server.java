package tictactoeserver;

import DataAccessLayer.DataAccessLayer;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import tictactoeserver.db.MyConnection;
import dto.DTOPlayer;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author user
 */
public class Server  {

    ServerSocket serverSocket;
    boolean isOpened;
    public static Vector<ServerHandler> myClients = new Vector<ServerHandler>();

    public Server() {

        try {
            serverSocket = new ServerSocket(4000);
            isOpened = true;
            acceptNewClient();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void closeConnection() throws IOException {
        isOpened = false;
        DataAccessLayer DB = new DataAccessLayer();
        for (int i = 0; i < myClients.size(); i++) {
            Socket socket = myClients.get(i).socket;
            Map<String, String> map = new HashMap<>();
            map.put("key", "serverStatus");
            map.put("msg", "close");
            String msg = new Gson().toJson(map);
            myClients.get(i).sendMessage(msg);
            DB.setPlayersOffline(myClients.get(i).getIp());
            socket.close();
        }

        serverSocket.close();
        try {
            DB.connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

        myClients.clear();
    }

    private void acceptNewClient() {
        new Thread() {
            @Override
            public void run() {
                while (!serverSocket.isClosed()) {
                    try {
                        Socket socket = serverSocket.accept();
                        myClients.add(new ServerHandler(socket));
                        
                    } catch (SocketException ex) {
                        System.out.println("client is down");

                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            }

        }.start();
    }

}
