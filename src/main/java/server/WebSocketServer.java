package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebSocketServer {

    private int port;
    private String webSocketPath;

    public WebSocketServer(int port, String webSocketPath) {
        this.setPort(port);
        this.setWebSocketPath(webSocketPath);
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
                            ch.pipeline().addLast(
                                    //new LoggingHandler(LogLevel.TRACE),
                                    //new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, Delimiters.lineDelimiter()),
                                    new StringEncoder(),
                                    new StringDecoder(),
                                    new EchoServerHandler());
                        }
                    });

            System.out.println("Server is listening on port " + port);

            // Start the server.
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

            // Wait until the server socket is closed.
            channelFuture.channel().closeFuture().sync();


        } catch(Exception ex) {
            Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
        
        
        
        
        
        
//        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//        try {
//            ServerBootstrap b = new ServerBootstrap();
//            b.group(bossGroup, workerGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .handler(new LoggingHandler(LogLevel.INFO))
//                    .childHandler(
//                                    new StringEncoder(),
//                                    new StringDecoder(),
//                                    new EchoServerHandler());
//                            //new WebSocketServerChannelInitializer(this)
//                    );
//
//            Channel ch = b.bind(port).sync().channel();
//            ch.closeFuture().sync();
//
//        } catch (InterruptedException ex) {
//            Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            System.err.println("SHUTDOWN SERVER");
//            bossGroup.shutdownGracefully();
//            workerGroup.shutdownGracefully();
//        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    public String getWebSocketPath() {
        return webSocketPath;
    }

    public void setWebSocketPath(String webSocketPath) {
        this.webSocketPath = webSocketPath;
    }
}
