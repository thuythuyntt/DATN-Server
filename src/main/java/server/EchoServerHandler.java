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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.ClientInfo;
import model.SocketMessage;

/**
 *
 * @author nguyen.thi.thu.thuy
 */
public class EchoServerHandler extends SimpleChannelInboundHandler<String> {

    //private List<Channel> channels = new ArrayList<>();
    private HashMap<String, Client> clients = new HashMap();
    
    private List<ClientInfo> getListOnline() {
        List<ClientInfo> list = new ArrayList<>();
        for (Client c : clients.values()) {
            list.add(c.clientInfo);
        }
        return list;
    }

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
        
        broadcastListOnline();
        
        //SocketMessage sm = new SocketMessage();
        //sm.setId(SocketMessage.SET_LIST_ONINE);
        //sm.setListOnline((List<ClientInfo>)clients.values());
        
       // ctx.writeAndFlush(sm.toJsonString());
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
            System.out.println("[channelRead0]: " + sm.getId());
            if (SocketMessage.CONNECT.equals(sm.getId())) {
                String username = sm.getClient().getUsername();
                List<Client> list = findClientByUserName(username);
                for (Client c: list) {
                    sendToClient( new SocketMessage(SocketMessage.FORCE_LOGOUT), c);
                    clients.remove(getClientIp(c.socketContext));
                }
                Client client = new Client();
                client.socketContext = ctx;
                client.clientInfo = sm.getClient();
                clients.put(getClientIp(ctx), client);
                broadcastListOnline();
            } else if (SocketMessage.GET_LIST_ONINE.equals(sm.getId())) {
                sendListOnline(ctx);
            } else if (sm.getId().startsWith("CTL_")) {
                //
            }
        } else {
            System.out.println("[channelRead0] but SocketMessage null");
        }
    }
    
    private String getClientIp(ChannelHandlerContext ctx) {
        return ctx.channel().remoteAddress().toString();
    }
    
    private List<Client> findClientByUserName(String username) {
        List<Client> list = new ArrayList<>();
        for (Client c : clients.values()) {
            if (c.clientInfo.getUsername().equals(username)) {
                list.add(c);
            }
        }
        return list;
    }
    
    private void sendToClient(SocketMessage sm, Client client) {
        client.socketContext.writeAndFlush(sm.toJsonString());
    }
    
    private void sendListOnline(ChannelHandlerContext ctx) {
        SocketMessage m = new SocketMessage(SocketMessage.SET_LIST_ONINE);
        List<ClientInfo> list = getListOnline();
        m.setListOnline(list);
        ctx.writeAndFlush(m.toJsonString());
    }
    
    private void broadcastListOnline() {
        List<ClientInfo> listOnline = getListOnline();
        for (Client c : clients.values()) {
            SocketMessage sm = new SocketMessage(SocketMessage.SET_LIST_ONINE);
            sm.setListOnline(listOnline);
            sendToClient(sm, c);
        }
    }
    
}
