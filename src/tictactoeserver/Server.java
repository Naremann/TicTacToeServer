package tictactoeserver;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import tictactoeserver.db.MyConnection;
import tictactoeserver.dto.DTOPlayer;


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

    public Server() {
        try {
            socket1 = new ServerSocket(4000);
            System.out.println("server is now ready....");

            while (true) {
                Socket clientconnection = socket1.accept();
                /*waiting for clients to connect*/
                Handler handler1 = new Handler(clientconnection);
                handler1.start();
            }
        } catch (Exception ioe) {
            System.out.println("error");
        }
    }/*end of constructor*/


    class Handler extends Thread {

        ObjectInputStream in = null;
        ObjectOutputStream out = null;

        public Handler(Socket request) {
            try {
                in = new ObjectInputStream(request.getInputStream());
                out = new ObjectOutputStream(request.getOutputStream());
            } catch (IOException io) {
                System.out.println("can't take i/o stream");
            }
        }

        public void run() {
            try {
                while (true) {
                    DTOPlayer p1 = (DTOPlayer) in.readObject();

                    Connection con1 = MyConnection.getConnection();

                    PreparedStatement stat1 = con1.prepareStatement("INSERT INTO PLAYER(USERNAME,EMAIL,PASSWORD) VALUES (?,?,?)");

                    stat1.setString(1, p1.getUserName());
                    stat1.setString(2, p1.getEmail());
                    stat1.setString(3, p1.getPassword());
                    int k = stat1.executeUpdate();

                    if (k == 1) {
                        out.writeObject("success");
                    } else {
                        out.writeObject("failed");
                    }
                }/*end of while*/
            } catch (Exception ee) {
                try {
                    out.writeObject("failed");
                } catch (Exception eee) {
                }
                System.out.println("errrrrrrror");
            }
        }
    }/*end of Handler*/
    public static void main(String[] arg) {
        Server server1 = new Server();
    }
}
