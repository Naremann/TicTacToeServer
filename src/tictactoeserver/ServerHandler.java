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
import java.util.Random;
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
    String userName;

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
                            network.setOfflinePlayer(userName);
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
                                    userName = object.getString("username");
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

                            case "invite": {
                                handleInviteMessage(message, object);
                            }
                            break;

                            case "onlinePlayers": {
                                String onlinePlayerss = network.onlinePlayers();
                                sendMessage(onlinePlayerss);
                            }
                            break;
                            case "saveMove":
                                handleSaveMoveResponse(message, object);
                            break;
                            case "IGNORE":
                            {
                                Map<String, String> map = new HashMap<>();
                                map.put("key", "IGNORE");
                                map.put("reciverName", object.getString("reciverName"));
                                message = new GsonBuilder().create().toJson(map);
                                for (int i = 0; i < Server.myClients.size(); i++) {
                                    if (Server.myClients.get(i).userName.equals(object.getString("senderName"))) {
                                        Server.myClients.get(i).sendMessage(message);

                                    }
                                }
                            }
                            break;
                            case "ACCEPT":
                            {
                                Map<String, String> map = new HashMap<>();
                                map.put("key", "ACCEPT");
                                map.put("reciverName", object.getString("reciverName"));
                                map.put("msg", "hima ma3i hena ************************************");
                                message = new GsonBuilder().create().toJson(map);
                                for (int i = 0; i < Server.myClients.size(); i++) {
                                    if (Server.myClients.get(i).userName.equals(object.getString("senderName"))) {
                                        Server.myClients.get(i).sendMessage(message);

                                    }
                                }
                            }
                            break;
                        }
                    } catch (SocketException ex) {
                        network.setOfflinePlayer(userName);
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
                jsonObject.getString("email"), jsonObject.getString("password"), "");
        String registerResponse = network.register(player, IP);
        System.out.println("register response" + registerResponse);
        Map<String, String> map = new HashMap<>();
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

    void handleInviteMessage(String message, JsonObject jsonObject) {

        DTORequest request = new DTORequest(jsonObject.getString("senderUsername"),
                jsonObject.getString("receiverUsername"));
        // String senderIndex = jsonObject.getString("index");
        //int index = Integer.parseInt(senderIndex); 
        //System.out.println(jsonObject.getString("senderUsername"));
        //System.out.println(jsonObject.getString("receiverUsername"));
        String requestResponse = network.request(request, IP);
        System.out.println("requestResponse" + requestResponse);
        Map<String, String> map = new HashMap<>();
        map.put("key", "invite");
        map.put("msg", requestResponse);
        
        message = new GsonBuilder().create().toJson(map);
        // if (requestResponse.equals("Invite Sent Successfully")) {
        map.put("key", "invite");
        map.put("senderUsername", jsonObject.getString("senderUsername"));

        map.put("receiverUsername", jsonObject.getString("receiverUsername"));
        map.put("msg", "Invite Sent Successfully");
        message = new GsonBuilder().create().toJson(map);

        for (int i = 0; i < Server.myClients.size(); i++) {
            if (Server.myClients.get(i).userName.equals(jsonObject.getString("receiverUsername"))) {
                Server.myClients.get(i).sendMessage(message);

            }
        }

        /*} else {
            Server.myClients.get(index).sendMessage(message);

        }*/
    }
    
    
    
        void handleReceiveMessage(String message, JsonObject jsonObject) {

       
        String username=jsonObject.getString("reciever");
        String response=jsonObject.getString("response");
        // String senderIndex = jsonObject.getString("index");
        //int index = Integer.parseInt(senderIndex); 
        //System.out.println(jsonObject.getString("senderUsername"));
        //System.out.println(jsonObject.getString("receiverUsername"));
       
        Map<String, String> map = new HashMap<>();
        map.put("key", "recieverResponse");
        map.put("response", response);
        message = new GsonBuilder().create().toJson(map);
        // if (requestResponse.equals("Invite Sent Successfully")) {
       

        for (int i = 0; i < Server.myClients.size(); i++) {
            if (Server.myClients.get(i).userName.equals(username)) {
                Server.myClients.get(i).sendMessage(message);

            }
        }

        /*} else {
            Server.myClients.get(index).sendMessage(message);

        }*/
    }

    public void randomStart() {
        Boolean rand = new Random().nextBoolean();
        if (rand) {
            // gameOwner.move = "X";
            //opponent.move = "O";
        } else {
            //gameOwner.move = "O";
            //opponent.move = "X";
        }
    }

    void handleSaveMoveResponse(String message, JsonObject jsonObject) {
        Map<String, String> map = new HashMap<>();
        String opponent = jsonObject.getString("opponentUserName");
        String row = jsonObject.getString("row");
        String col = jsonObject.getString("col");

        map.put("key", "saveMove");
        map.put("opponentUserName", opponent);
        map.put("row", row);
        map.put("col", col);
        message = new GsonBuilder().create().toJson(map);
        for (int i = 0; i < Server.myClients.size(); i++) {
            if (Server.myClients.get(i).userName.equals(opponent)) {
                sendMessage(message);
            }
        }
    }

}