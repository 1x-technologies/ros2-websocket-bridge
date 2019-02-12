package com.halodi.bridge.websocket;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

public class WebSocketBroadcastHandler
{
   private final ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
   private static final ChannelMatcher WRITABLE_CHANNEL_MATCHER = new WritableChannelMatcher();
   
   private static class WritableChannelMatcher implements ChannelMatcher
   {

      @Override
      public boolean matches(Channel channel)
      {
         if(channel.isWritable())
         {
            return true;
         }
         else
         {
            return false;
         }
      }
      
   }
   
   
   public void addClient(Channel channel)
   {

      System.out.println("Adding new channel {} to list of channels " + channel.remoteAddress());
      clients.add(channel);
      System.out.println(clients);
   }
   
   public void broadcast(String msg, boolean reliable)
   {
      TextWebSocketFrame frame = new TextWebSocketFrame(msg);
      if(reliable)
      {
         clients.writeAndFlush(frame);
      }
      else
      {
         clients.writeAndFlush(frame, WRITABLE_CHANNEL_MATCHER);
      }
   }
}
