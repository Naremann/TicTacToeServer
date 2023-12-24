/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import dto.DTOPlayer;
import dto.DTORequest;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.Socket;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import network.Network;
import tictactoeserver.Constants;


/**
 *
 * @author HimaMarey
 */
public class ServerHandler {

    DataInputStream dataInputStream;
    PrintStream printStream;
    Network network;
    Socket socket;
    BufferedReader bufferReader;
    String IP;
    int portNum;

    public ServerHandler(Socket socket) {
        this.socket = socket;
        IP = socket.getInetAddress().getHostAddress();
        portNum = socket.getPort();
        network = new Network();
        try {
            bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printStream = new PrintStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
            readMessages();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void readMessages() {
        new Thread() {
            @Override
            public void run() {

                while (!socket.isClosed() && socket.isConnected()) {
                    try {

                        String message = bufferReader.readLine();
                        if (message == null) {
                            socket.close();
                            Server.myClients.remove(ServerHandler.this);
                            continue;
                        }
                        JsonReader jsonReader = (JsonReader) Json.createReader(new StringReader(message));
                        JsonObject object = jsonReader.readObject();
                        switch (object.getString("key")) {
                            case "login": {
                                DTOPlayer player = new DTOPlayer(object.getString("username"), object.getString("password"), null);
                                String loginResponse = network.login(player, IP);
                                System.out.println(loginResponse);

                                Map<String, String> map = new HashMap<>();
                                map.put("key", "login");
                                map.put("msg", loginResponse);
                                message = new GsonBuilder().create().toJson(map);
                                if (loginResponse.equals("login successfully")) {
                                    Map<String, String> mapl = new HashMap<>();
                                    map.put("key", "login");
                                    map.put("username", object.getString("username"));
                                    map.put("msg", loginResponse);
                                    message = new GsonBuilder().create().toJson(map);
                                    sendMessage(message);
                                } else {
                                    sendMessage(message);
                                }
                            }
                            break;
                            case "register":
                                handleRegisterMessage(message, object);
                                break;
                            
                            case "invite":
                            {
                                handleInviteMessage(message, object);
                            }
                            break;
                            
                            case"onlinePlayers":
                            {
                                String onlinePlayerss = network.onlinePlayers();
                                sendMessage(onlinePlayerss);
                            }
                                break;
                        }
                    } catch (SocketException ex) {
                        Server.myClients.remove(ServerHandler.this);
                        System.out.println("Client disconect");
                    } catch (IOException ex) {
                        ex.printStackTrace();

                    }
                }
            }
        }.start();
    }

    public void sendMessage(String message) {
        new Thread() {
            @Override
            public void run() {
                printStream.println(message);
            }

        }.start();
    }

    public String getIp() {
        return IP;
    }

    public void closeResources() {
        try {
            System.out.println("Client disconnected");

            if (dataInputStream != null) {
                dataInputStream.close();
            }
            if (printStream != null) {
                printStream.close();
            }

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void handleRegisterMessage(String message, JsonObject jsonObject) {
        DTOPlayer player = new DTOPlayer(jsonObject.getString("username"),
        jsonObject.getString("email"), jsonObject.getString("password"),"");
        String registerResponse = network.register(player, IP);
        System.out.println("register response" + registerResponse);
        Map<String,String> map = new HashMap<>();
        map.put("key", Constants.REGISTER);
        map.put("msg", registerResponse);
        message = new GsonBuilder().create().toJson(map);
        if (registerResponse.equals("registed successfully")) {
            map.put("key", Constants.REGISTER);
            map.put("username", jsonObject.getString("username"));
            map.put("msg", registerResponse);
            message = new GsonBuilder().create().toJson(map);
            sendMessage(message);
        } else {
            sendMessage(message);
        }
    }
    
    void handleInviteMessage(String message, JsonObject jsonObject){

    String senderUsername = jsonObject.getString("senderUsername");
    String receiverUsername = jsonObject.getString("receiverUsername");
    
    DTORequest Request = new DTORequest(senderUsername, receiverUsername);
    
    //String requestResponse = network.register(Request, IP);

    // Process the invite request, check if the receiver is online, etc.
    // You can add your business logic here...

    // Constructing a response message
    Map<String, String> map = new HashMap<>();
    map.put("key", Constants.INVITE_RESPONSE);

    // Assuming you have a method to check if the receiver is online
   /* if (isReceiverOnline(receiverUsername)) {
        // Receiver is online and can respond to the invite
        map.put("status", "online");
    } else {
        // Receiver is offline, cannot respond to the invite
        map.put("status", "offline");
    }*/

    // Converting the map to a JSON string using Gson
    String responseMessage = new GsonBuilder().create().toJson(map);
    sendMessage(responseMessage);
} 

   
}
    
   
