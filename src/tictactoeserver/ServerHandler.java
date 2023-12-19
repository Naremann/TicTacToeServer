/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import network.Network;

/**
 *
 * @author HimaMarey
 */
public class ServerHandler 
    {
        DataInputStream dataInputStream;
        PrintStream printStream ;
        Network network = new Network();
        public ServerHandler(Socket request) {
            try {
                dataInputStream = new DataInputStream(request.getInputStream());
                printStream = new PrintStream(request.getOutputStream());
                String message = dataInputStream.readLine();
                handleClientMessage(message);
                if(handleServerMessage(message)){
                    printStream.println("user found");
                }else{
                     printStream.println("user not found");
                }
                    System.out.println(message);
                } catch (IOException io) {
//                try {
//                    request.close();
//                } catch (IOException ex) {
//                    Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
//                }
                    //closeResources();
                    System.out.println("can't take i/o stream");
                }
            finally{
                try {
                    if(request != null)
                    {
                        request.close();
                    }
                    
                } catch (IOException ex) {
                    Logger.getLogger(ServerHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
                closeResources();
            }
        }
        private boolean handleServerMessage(String mess) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new StringReader(mess)).getAsJsonObject();

         System.out.println("Message processed: " + json);
         return network.login(json);
    }
        private void handleClientMessage(String message) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(new StringReader(message)).getAsJsonObject();

         System.out.println("Message processed: " + json);
         network.login(json);
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