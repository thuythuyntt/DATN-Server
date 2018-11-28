/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import model.ClientInfo;
import model.SocketMessage;

/**
 *
 * @author nguyen.thi.thu.thuy
 */
public class EchoServerHandler extends SimpleChannelInboundHandler<String> {

    //private List<Channel> channels = new ArrayList<>();
    private HashMap<String, ClientInfo> clients = new HashMap();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[channelActive]");
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        System.err.println("[handlerAdded] remoteAddress " + ch.remoteAddress().toString());
        System.err.println("[handlerAdded] localAddress: " + ch.localAddress().toString() + " Host name: " + InetAddress.getLocalHost().getHostName());
        //channels.add(ch);

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("[handlerRemoved]");
        String ip = ctx.channel().remoteAddress().toString();
        clients.remove(ip);
        
        SocketMessage sm = new SocketMessage();
        sm.setId(SocketMessage.SET_LIST_ONINE);
        sm.setListOnline((List<ClientInfo>)clients.values());
        
        ctx.writeAndFlush(sm.toJsonString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Error in receiving message.");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        SocketMessage sm = SocketMessage.fromJsonString(message);
        if (sm != null) {
            System.out.println("[channelRead0]: " + sm.toString());
            if (SocketMessage.CONNECT.equals(sm.getId())) {
                broadcastListOnline();
                
                String ip = ctx.channel().remoteAddress().toString();
                clients.put(ip, sm.getClient());
                sendListOnline(ctx);
                
                
            } else if (SocketMessage.GET_LIST_ONINE.equals(sm.getId())) {
                sendListOnline(ctx);
            } else if (sm.getId().startsWith("CTL_")) {
                //
            }
        }
    }
    
    private void sendListOnline(ChannelHandlerContext ctx) {
        SocketMessage m = new SocketMessage();
        m.setId(SocketMessage.SET_LIST_ONINE);
        m.setListOnline((List<ClientInfo>)clients.values());
        ctx.writeAndFlush(m.toJsonString());
    }
    
    private void broadcastListOnline() {
        //TODO
    }
    
}
