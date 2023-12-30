package tictactoeserver.db;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.derby.jdbc.ClientDriver;

/**
 *
 * @author user
 */
public class MyConnection {

    public static Connection getConnection() {
        Connection connection = null;
        try {

            DriverManager.deregisterDriver(new ClientDriver());
            connection = DriverManager.getConnection("jdbc:derby://localhost:1527/TicTacToe", "root", "root");

        } catch (SQLException ex) {
            ex.printStackTrace();
           // AlertMessage.infoBox(ex.getLocalizedMessage(), "Error", null);
        }
        return connection;
    }

    public static boolean isDbConnected(Connection con) {
        try {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
                        e.printStackTrace();

           // AlertMessage.infoBox(e.getLocalizedMessage(), "Error!", null);

        }

        return false;
    }

}
