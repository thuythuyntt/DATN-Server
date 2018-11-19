package server.handler;

//import test.handler.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import com.google.gson.Gson;
import model.SocketMessage;

public class TextFrameToMessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>{

	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
		Gson gson = new Gson();
		SocketMessage msg = gson.fromJson(frame.text(), SocketMessage.class);
		ctx.fireChannelRead(msg);
	}

}
