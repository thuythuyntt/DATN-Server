/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.SessionInfo;
import model.Student;

/**
 *
 * @author thuy
 */
public class MyDatabase {
 
    private Connection mConnection;
    private ResultSet rs;

    private static MyDatabase instance = null;

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public MyDatabase() {
        connectMySQL();
    }

    public void connectMySQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            mConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/datn", "datn", "123456");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<SessionInfo> getListSessionByStudentId(String id) throws Exception {
        ArrayList<SessionInfo> lst = new ArrayList<>();
        String strSQL = "select * from user_sessions where userId = '" + id +"'";
        try {
            rs = mConnection.createStatement().executeQuery(strSQL);
            while (rs.next()) {
                String pcName = rs.getString("pcName");
                String login = rs.getString("dtLogin");
                String logout = rs.getString("dtLogout");
                String reason = rs.getString("reasonLogout");
                SessionInfo s = new SessionInfo(pcName, login, logout, reason);
                lst.add(s);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage() + " Error at : " + strSQL);
        }
        return lst;
    }
    
    public ArrayList<Student> getListStudent() throws Exception {
        ArrayList<Student> lst = new ArrayList<>();
        String strSQL = "select * from users";
        try {
            rs = mConnection.createStatement().executeQuery(strSQL);
            while (rs.next()) {
                String id = rs.getString("firebaseId");
                String userName = rs.getString("userName");
                String fullName = rs.getString("fullName");
                String group = rs.getString("class");
                String code = rs.getString("code");
                Student u = new Student(id, userName, group, code, fullName);
                lst.add(u);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage() + " Error at : " + strSQL);
        }
        return lst;
    }
    
    public void disconnectMySQL(){
        try {
            mConnection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
