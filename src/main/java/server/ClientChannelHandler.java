/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
        System.out.println("handlerRemoved");
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
                client.clientInfo = sm.getSessionInfo();
                listener.onClientAdded(this);
                listener.updateListOnline();
                if (sm.getSessionInfo().getRole().equals("sv")) {
                    listener.addUserSessionToDB(sm.getSessionInfo());
                }
            } else if (SocketMessage.GET_LIST_ONINE.equals(sm.getId())) {
                listener.sendListOnline(ctx);
            } else if (sm.getId().startsWith("CTL_")) {
                sm.getSessionInfo().getIpAddress();
                listener.controlPC(sm);
            } else if (SocketMessage.GET_VIEWER.equals(sm.getId())) {
                listener.shareScreen(sm);
            } else if (SocketMessage.SET_VIEWER.equals(sm.getId())) {
                listener.updateSharingScreen(sm);
            } else if (SocketMessage.SEND_NOTIFICATION.equals(sm.getId())) {
                listener.sendNotification(sm);
            } else if (SocketMessage.GET_LIST_SESSION.equals(sm.getId())) {
                listener.sendListSession(ctx, sm.getCapture());
            } else if (SocketMessage.GET_LIST_STUDENT.equals(sm.getId())) {
                listener.sendListStudent(ctx);
            } else if (SocketMessage.DISCONNECT.equals(sm.getId())) {
                listener.disconnect(sm.getSessionInfo());
            } else {
                System.out.println("[channelRead0] but SocketMessage null");
            }
        }
    }

    public String getClientIp() {
        return client.socketContext.channel().remoteAddress().toString();
    }

}
