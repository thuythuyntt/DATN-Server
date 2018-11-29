package server;

import database.MyDatabase;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ClientInfo;

public class SocketServer {
    
    public interface Listener {
        void onClientAdded(ClientChannelHandler handler);
        void onClientRemoved(ClientChannelHandler handler);
        List<ClientInfo> getListOnline();
        ChannelHandlerContext getHandlerByIPAddress(String ip);
    }

    private int port;
    private HashMap<String, Client> clients = new HashMap<>();

    public SocketServer(int port) {
        this.setPort(port);
    }

    public void run() {

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.TRACE))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new StringEncoder(), new StringDecoder(), new ClientChannelHandler(new Listener() {
                                @Override
                                public void onClientAdded(ClientChannelHandler handler) {
                                    clients.put(handler.getClientIp(), handler.getClient());
                                    System.out.println("onClientAdded " + handler.getClientIp());
                                }
                                
                                @Override
                                public void onClientRemoved(ClientChannelHandler handler) {
                                    clients.remove(handler.getClientIp());
                                }

                                @Override
                                public List<ClientInfo> getListOnline() {
                                    return SocketServer.this.getListOnline();
                                }

                                @Override
                                public ChannelHandlerContext getHandlerByIPAddress(String ip) {
                                    return clients.get(ip).socketContext;
                                }
                                
                            }));
                        }

                        @Override
                        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                            super.exceptionCaught(ctx, cause);
                            System.out.println("exceptionCaught " + cause.getMessage());
                        }
                        
                    });

            System.out.println("Server is listening on port " + port);

            // Start the server.
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            // Wait until the server socket is closed.
            channelFuture.channel().closeFuture().sync();

        } catch (Exception ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    private List<ClientInfo> getListOnline() {
        List<ClientInfo> list = new ArrayList<>();
        for (Client c : clients.values()) {
            list.add(c.clientInfo);
        }
        return list;
    }
}
