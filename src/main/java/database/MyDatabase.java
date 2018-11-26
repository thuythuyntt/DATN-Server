/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author thuy
 */
public class MyDatabase {
    private int serverPort = 8888;
    
    private static String hostName = "localhost";
    private static String sqlInstanceName = "SQLEXPRESS";
    private static String database = "datn";
    private static String userName = "sa";
    private static String password = "123456";
    private static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    
    private Connection mConnection;
    private ServerSocket mServerSocket;
    
    private static MyDatabase instance = null;
    
    public static MyDatabase getInstance(){
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public MyDatabase() {
        mConnection = getDBConnection();
        mServerSocket = openServer(serverPort);
    }

    public static Connection getDBConnection() {
        try {
            Class.forName(driver);
            
            String connectionUrl = "jdbc:sqlserver://" + hostName + ":1433;"
                    + "databaseName=" + database + ";user=" + userName + ";password=" + password + ";";
            try {
                return DriverManager.getConnection(connectionUrl);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static ServerSocket openServer(int portNumber) {
        try {
            return new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
