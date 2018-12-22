/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
    private PreparedStatement ps;

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
            Class.forName("com.mysql.cj.jdbc.Driver");
            mConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/datn", "datn", "123456");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<SessionInfo> getListSessionByStudentId(String id) {
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
            e.printStackTrace();
        }
        return lst;
    }
    
    public void addUserSession(SessionInfo s) {
        String query = "insert into user_sessions (userId , pcName, pcIp, dtLogin) VALUES (?,?,?,?)";
        try {
            ps = mConnection.prepareStatement(query);
            ps.setString(1, s.getUserId());
            ps.setString(2, s.getPcName());
            ps.setString(3, s.getIpAddress());
            ps.setString(4, s.getDtLogin());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void updateUserSession(SessionInfo s){
        String query = "update user_sessions set dtLogout = ?, reasonLogout = ? where userId = ?";
        try {
            ps = mConnection.prepareStatement(query);
            ps.setString(1, s.getDtLogout());
            ps.setString(2, s.getReasonLogout());
            ps.setString(3, s.getUserId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public ArrayList<Student> getListStudent(){
        ArrayList<Student> lst = new ArrayList<>();
        String strSQL = "select * from users where role = sv";
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
            e.printStackTrace();
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
