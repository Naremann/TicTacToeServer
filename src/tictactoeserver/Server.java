package tictactoeserver;

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
import java.sql.SQLException;
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
public class Server {

    ServerSocket socket1;
    DataInputStream ear;
    PrintStream mouth;
    Socket clientconnection;
    static boolean turnServer=false ;
    public Server() {

        try {
            socket1 = new ServerSocket(4000);

        } catch (IOException ex) {
            System.out.println("socket: " + ex.getLocalizedMessage());
        }
        System.out.println("server is now ready....");

        while (turnServer) {

            try {
                String msg=null;
                clientconnection = socket1.accept();
                //Socket clientconnection = socket1.accept();

                ear = new DataInputStream(clientconnection.getInputStream());
                mouth = new PrintStream(clientconnection.getOutputStream());
                msg = ear.readLine();
                System.out.println("The client says: " + msg);
                mouth.println("Are you hearing me?? ");
                ear.close();
                mouth.close();
               

                clientconnection = socket1.accept();

                if (msg.equals("login")) {
                    RegisterHandler registerHandler = new RegisterHandler(clientconnection);
                    registerHandler.start();
                } else {
                    Handler handler1 = new Handler(clientconnection);
                    handler1.start();
                }
                
                

                /*waiting for clients to connect*/
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }/*end of constructor*/


    class Handler extends Thread {

        ObjectInputStream in = null;
        ObjectOutputStream out = null;

        public Handler(Socket request) {
            try {
                in = new ObjectInputStream(request.getInputStream());
                out = new ObjectOutputStream(request.getOutputStream());

                ear = new DataInputStream(request.getInputStream());
                mouth = new PrintStream(request.getOutputStream());

            } catch (IOException io) {
                System.out.println("can't take i/o stream");
            }
        }

        public void run() {
            try {

                while (true) {
                    DTOPlayer player = (DTOPlayer) in.readObject();
                    Connection con = MyConnection.getConnection();

                    login(con, player);

                }
                /*end of while*/
            } catch (Exception ee) {
                try {
                    out.writeObject("failed");
                } catch (Exception eee) {
                }

                System.out.println("error: " + ee.getLocalizedMessage());
                ee.printStackTrace();
                System.out.println("errrrrrrror");
            }
        }

        private void login(Connection con, DTOPlayer player) {
            try {
                PreparedStatement stat1 = con.prepareStatement("INSERT INTO PLAYER(USERNAME,EMAIL,PASSWORD) VALUES (?,?,?)");

                stat1.setString(1, player.getUserName());
                stat1.setString(2, player.getEmail());
                stat1.setString(3, player.getPassword());
                int k = stat1.executeUpdate();

                if (k == 1) {
                    try {
                        out.writeObject("success");
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {
                        out.writeObject("failed");
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }/*end of Handler*/
    
}
