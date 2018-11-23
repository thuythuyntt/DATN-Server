/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.google.gson.Gson;

/**
 *
 * @author thuy
 */
public class SocketMessage {

    private String id;
    private String text;

    public SocketMessage() {
    }

    public SocketMessage(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String toString() {
        return "id: " + id + ", text: " + text;
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static SocketMessage fromJsonString(String json) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(json, SocketMessage.class);
        } catch (Exception e) {
            return null;
        }
    }
}
