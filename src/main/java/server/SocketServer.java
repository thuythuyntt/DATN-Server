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
import io.netty.util.CharsetUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.SessionInfo;
import model.SocketMessage;
import model.Student;

public class SocketServer {
    
    public static final String ROLE_TEACHER = "gv";

    public interface Listener {

        void onClientAdded(ClientChannelHandler handler);

        void onClientRemoved(ClientChannelHandler handler);
        
        void sendNotification(SocketMessage sm);

        void sendListOnline(ChannelHandlerContext ctx);

        void updateListOnline();

        void controlPC(SocketMessage sm);

        void shareScreen(SocketMessage sm);

        void updateSharingScreen(SocketMessage sm);
        
        void sendListSession(ChannelHandlerContext ctx, String studentId);
        
        void sendListStudent(ChannelHandlerContext ctx);
        
        void addUserSessionToDB(SessionInfo s);
        
        void disconnect(SessionInfo s);
    }

    private int port;
    private HashMap<String, Client> clients = new HashMap<>();
    private ChannelHandlerContext teacherCtx;

    public SocketServer(int port) {
        this.port = port;
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
                            ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8), new StringDecoder(CharsetUtil.UTF_8), new ClientChannelHandler(new Listener() {
                                @Override
                                public void onClientAdded(ClientChannelHandler handler) {
                                    clients.put(handler.getClientIp(), handler.getClient());
                                    if (handler.getClient().clientInfo.getRole().equals(ROLE_TEACHER)) {
                                        teacherCtx = handler.getClient().socketContext;
                                    }
                                    System.out.println("onClientAdded " + handler.getClientIp());
                                }

                                @Override
                                public void onClientRemoved(ClientChannelHandler handler) {
                                    clients.remove(handler.getClientIp());
                                    updateListConnecting();
                                }

                                @Override
                                public void sendListOnline(ChannelHandlerContext ctx) {
                                    SocketMessage m = new SocketMessage(SocketMessage.SET_LIST_ONINE);
                                    List<SessionInfo> list = SocketServer.this.getListOnline();
                                    m.setListOnline(list);
                                    ctx.writeAndFlush(m.toJsonString());
                                }

                                @Override
                                public void updateListOnline() {
                                    updateListConnecting();

                                }

                                @Override
                                public void controlPC(SocketMessage sm) {
                                    System.out.println("controlPC " + sm.getSessionInfo().getIpAddress());
                                    ChannelHandlerContext ctx = clients.get(sm.getSessionInfo().getIpAddress()).socketContext;
                                    ctx.writeAndFlush(sm.toJsonString());
                                }

                                @Override
                                public void shareScreen(SocketMessage sm) {
                                    System.out.println("SocketServer shareScreen");
                                    ChannelHandlerContext ctx = clients.get(sm.getSessionInfo().getIpAddress()).socketContext;
                                    ctx.writeAndFlush(sm.toJsonString());
                                }

                                @Override
                                public void updateSharingScreen(SocketMessage sm) {
                                    System.out.println("SocketServer updateSharingScreen");
                                    if (teacherCtx != null) {
                                        teacherCtx.writeAndFlush(sm);
                                    }
                                }

                                @Override
                                public void sendNotification(SocketMessage sm) {
                                    for (Client c: clients.values()){
                                        if (!(c.clientInfo.getRole().equals(ROLE_TEACHER))) {
                                            System.out.println(c.clientInfo.getFullName());
                                            c.socketContext.writeAndFlush(sm);
                                        }
                                    }
                                }

                                @Override
                                public void sendListSession(ChannelHandlerContext ctx, String studentId) {
                                    try {
                                        SocketMessage m = new SocketMessage(SocketMessage.SET_LIST_SESSION);
                                        List<SessionInfo> list = MyDatabase.getInstance().getListSessionByStudentId(studentId);
                                        m.setListOnline(list);
                                        ctx.writeAndFlush(m.toJsonString());
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                @Override
                                public void sendListStudent(ChannelHandlerContext ctx) {
                                    try {
                                        SocketMessage m = new SocketMessage(SocketMessage.SET_LIST_STUDENT);
                                        List<Student> list = MyDatabase.getInstance().getListStudent();
                                        m.setListStudent(list);
                                        ctx.writeAndFlush(m.toJsonString());
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                @Override
                                public void addUserSessionToDB(SessionInfo s) {
                                    MyDatabase.getInstance().addUserSession(s);
                                }

                                @Override
                                public void disconnect(SessionInfo s) {
                                    MyDatabase.getInstance().updateUserSession(s);
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

    private List<SessionInfo> getListOnline() {
        List<SessionInfo> list = new ArrayList<>();
        for (Client c : clients.values()) {
            list.add(c.clientInfo);
        }
        return list;
    }

    private void updateListConnecting() {
        if (teacherCtx != null) {
            SocketMessage m = new SocketMessage(SocketMessage.SET_LIST_ONINE);
            List<SessionInfo> list = SocketServer.this.getListOnline();
            m.setListOnline(list);
            teacherCtx.writeAndFlush(m.toJsonString());
        }
    }

}
