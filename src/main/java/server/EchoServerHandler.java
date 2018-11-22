/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.ArrayList;
import java.util.List;
import model.SocketMessage;

/**
 *
 * @author nguyen.thi.thu.thuy
 */
 public class EchoServerHandler extends SimpleChannelInboundHandler<String> {
    private List<Channel> channels = new ArrayList<>();
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //String message = "message from server.";
        //System.out.println("Sending message: " + message);
        //ctx.writeAndFlush(message + System.lineSeparator());
    }
    
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel ch = ctx.channel();
        System.err.println("[handlerAdded] remoteAddress " + ch.remoteAddress().toString());
        System.err.println("[handlerAdded] localAddress " + ch.localAddress().toString());
       // ctx.writeAndFlush("123456");
        channels.add(ch);
        
//        SocketMessage sm = new SocketMessage();
//        sm.setId("xxx");
//        sm.setText("yyy");
//        ctx.channel().writeAndFlush(sm).sync();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channels.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Error in receiving message.");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws Exception {
        //System.out.println("Received message: " + message);
        //ctx.writeAndFlush(message + System.lineSeparator());
        SocketMessage sm = SocketMessage.fromJsonString(message);
        System.out.println("channelRead0: " + sm.toString());
    }
}