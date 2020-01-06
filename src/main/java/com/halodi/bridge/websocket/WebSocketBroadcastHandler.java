package com.halodi.bridge.websocket;

import java.util.HashMap;

import com.halodi.bridge.BridgeClient;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

public class WebSocketBroadcastHandler
{
   private final ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
   private HashMap<Channel, BridgeClient<Channel>> clientMap = new HashMap<>(); 

   
   private class TopicChannelMatcher implements ChannelMatcher
   {
      private final boolean reliable;
      private final String topicName;
      
      public TopicChannelMatcher(boolean reliable, String topicName)
      {
         this.reliable = reliable;
         this.topicName = topicName;
      }
            
      
      @Override
      public boolean matches(Channel channel)
      {
         BridgeClient<Channel> client = clientMap.get(channel);
         
         if(client == null)
         {
            System.err.println("Unmatched client " + channel);
            return false;
         }
         
         return client.isSubscribedTo(topicName);         
         
      }
      
   }
   
   
   
   public void addClient(BridgeClient<Channel> bridgeClient)
   {

      System.out.println("Adding new channel {} to list of channels " + bridgeClient.getImplementation().remoteAddress());
      clients.add(bridgeClient.getImplementation());
      clientMap.put(bridgeClient.getImplementation(), bridgeClient);
      System.out.println(clients);
   }
   
   public void broadcast(String topicName, String msg, boolean reliable)
   {
      TextWebSocketFrame frame = new TextWebSocketFrame(msg);
      clients.writeAndFlush(frame, new TopicChannelMatcher(reliable, topicName));
   }
}
