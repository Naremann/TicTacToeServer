/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import dto.DTOPlayer;
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import network.Network;

/**
 *
 * @author HimaMarey
 */
public class ServerHandler 
    {
        DataInputStream dataInputStream;
        PrintStream printStream ;
        Network network;
        Socket socket;
        BufferedReader bufferReader;
        String IP;
        int portNum;
        public ServerHandler(Socket socket) 
        {
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

        public void readMessages() 
        {
            new Thread() {
                @Override
                public void run() {

                    while (!socket.isClosed() && socket.isConnected()) {
                        try {

                            String message = bufferReader.readLine();
                            if (message == null) {
                                socket.close();
                                continue;
                            }
                            JsonReader jsonReader = (JsonReader) Json.createReader(new StringReader(message));
                            JsonObject object = jsonReader.readObject();
                            switch(object.getString("key"))
                            {
                                case "login":
                                {
                                    DTOPlayer player = new DTOPlayer(object.getString("username"), object.getString("password"),null);
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
                            }
                        } catch (SocketException ex) {
                            System.out.println("Client disconect");
                        } catch (IOException ex) {
                            ex.printStackTrace();

                        } 
                    }
                }
            }.start();
        }
        public void sendMessage(String message) 
        {
            new Thread() {
                @Override
                public void run() {
                    printStream.println(message);
                }

            }.start();
        }
        public String getIp() 
        {
            return IP;
        }
        public void closeResources()
        {
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
}