/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import com.google.gson.Gson;

/**
 *
 * @author thuy
 */
public class OnlineMessage {
    private String username;
    private String pcname; 
    private String login;
    private String logout;
    private int online;

    public OnlineMessage(String username, String pcname, String login, String logout, int online) {
        this.username = username;
        this.pcname = pcname;
        this.login = login;
        this.logout = logout;
        this.online = online;
    }
    
    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static OnlineMessage fromJsonString(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, OnlineMessage.class);
    }
}
