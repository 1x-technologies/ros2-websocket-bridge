package com.halodi.bridge.websocket;

import com.halodi.bridge.BridgeController;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;

public class WebsocketCommunicatorInitializer extends ChannelInitializer<SocketChannel>
{
   private static final String WEBSOCKET_PATH = "/websocket";
   
   private final WebSocketBroadcastHandler webSocketBroadcastHandler;
   private final BridgeController controller;
   
   public WebsocketCommunicatorInitializer(WebSocketBroadcastHandler webSocketBroadcastHandler, BridgeController controller)
   {
      this.webSocketBroadcastHandler = webSocketBroadcastHandler;
      this.controller = controller;
   }

   @Override
   public void initChannel(SocketChannel ch) throws Exception
   {
      ChannelPipeline pipeline = ch.pipeline();

      pipeline.addLast(new HttpServerCodec());
      pipeline.addLast(new HttpObjectAggregator(65536));
      pipeline.addLast(new WebSocketServerCompressionHandler());
      pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
      pipeline.addLast(new WebSocketFrameHandler(webSocketBroadcastHandler, controller));
   }
}
