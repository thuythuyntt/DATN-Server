/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import com.google.gson.Gson;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 *
 * @author thuy
 */
public class SocketMessage {
	private String id;
	private String text;
	
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
        
        public TextWebSocketFrame toSocketFrame() {
            Gson gson = new Gson();
            String json = gson.toJson(this);
            return new TextWebSocketFrame(json);
        }
        
        public static SocketMessage fromSocketFrame(TextWebSocketFrame frame){
            Gson gson = new Gson();
            return gson.fromJson(frame.text(), SocketMessage.class);
        }
}
