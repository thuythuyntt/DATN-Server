/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.List;
import model.ClientInfo;
import model.SocketMessage;

/**
 *
 * @author nguyen.thi.thu.thuy
 */
public class ClientChannelHandler extends SimpleChannelInboundHandler<String> {

    private Client client;
    private final SocketServer.Listener listener;

    public ClientChannelHandler(SocketServer.Listener serveListener) {
        super();
        this.listener = serveListener;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        listener.onClientRemoved(this);
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
                client = new Client();
                client.socketContext = ctx;
                client.clientInfo = sm.getClientInfo();
                listener.onClientAdded(this);
//                broadcastListOnline();
            } else if (SocketMessage.GET_LIST_ONINE.equals(sm.getId())) {
//                sendListOnline(ctx);
                listener.sendListOnline(ctx);
            } else if (sm.getId().startsWith("CTL_")) {
                listener.controlPC(sm);
            }
        } else {
            System.out.println("[channelRead0] but SocketMessage null");
        }
    }

    public String getClientIp() {
        return client.socketContext.channel().remoteAddress().toString();
    }

//    private void sendListOnline(ChannelHandlerContext ctx) {
//        SocketMessage m = new SocketMessage(SocketMessage.SET_LIST_ONINE);
//        List<ClientInfo> list = listener.getListOnline();
//        m.setListOnline(list);
//        ctx.writeAndFlush(m.toJsonString());
//    }
}
