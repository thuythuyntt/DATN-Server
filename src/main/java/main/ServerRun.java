/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import server.WebSocketServer;

/**
 *
 * @author thuy
 */
public class ServerRun {
    public static void main(String[] args) {
        new WebSocketServer(8080).run();
    }
}
