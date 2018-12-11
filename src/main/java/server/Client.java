/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import io.netty.channel.ChannelHandlerContext;
import model.SessionInfo;

/**
 *
 * @author thuy
 */
public class Client {
    public SessionInfo clientInfo;
    public ChannelHandlerContext socketContext;
}
