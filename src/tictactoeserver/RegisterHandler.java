/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tictactoeserver;

import dto.DTOPlayer;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import tictactoeserver.db.MyConnection;

/**
 *
 * @author user
 */
public class RegisterHandler extends Thread {

    ObjectInputStream in = null;
    ObjectOutputStream out = null;

    public RegisterHandler(Socket request) {
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
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM PLAYER WHERE USERNAME = ? and PASSWORD = ?");

            preparedStatement.setString(1, player.getUserName());
            preparedStatement.setString(2, player.getPassword());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                try {
                    out.writeObject("success");
                } catch (IOException ex) {
                    ex.printStackTrace();
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
}
