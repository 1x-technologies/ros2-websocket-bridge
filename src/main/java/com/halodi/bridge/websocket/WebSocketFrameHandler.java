package com.halodi.bridge.websocket;

import com.halodi.bridge.BridgeController;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame>
{
   private final WebSocketBroadcastHandler webSocketBroadcastHandler;
   private final BridgeController controller;

   public WebSocketFrameHandler(WebSocketBroadcastHandler webSocketBroadcastHandler, BridgeController controller)
   {
      this.webSocketBroadcastHandler = webSocketBroadcastHandler;
      this.controller = controller;
   }

   @Override
   protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception
   {
      if (frame instanceof TextWebSocketFrame)
      {
         String request = ((TextWebSocketFrame) frame).text();
         controller.receivedMessage(request);
      }
      else
      {
         String message = "unsupported frame type: " + frame.getClass().getName();
         throw new UnsupportedOperationException(message);
      }
   }

   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception
   {
      webSocketBroadcastHandler.addClient(ctx.channel());
   }
}
