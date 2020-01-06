package com.halodi.bridge.websocket;

import com.halodi.bridge.BridgeCommunicator;
import com.halodi.bridge.BridgeController;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class WebsocketCommunicator implements BridgeCommunicator, Runnable
{
   private static final int DEFAULT_PORT = 8080;
   
   private BridgeController bridgeController = null;
   private final WebSocketBroadcastHandler webSocketBroadcastHandler = new WebSocketBroadcastHandler();

   private int port = DEFAULT_PORT;

   public static void main(String[] args) throws Exception
   {
      new WebsocketCommunicator().run();
   }

   @Override
   public synchronized void setup(BridgeController bridgeController)
   {
      if(this.bridgeController != null)
      {
         throw new RuntimeException("Setup has already been called");
      }
      this.bridgeController = bridgeController;
      
   }

   @Override
   public void send(String topicName, String msg, boolean reliable)
   {
      webSocketBroadcastHandler.broadcast(topicName, msg, reliable);
   }
   
   public void run()
   {
      if(this.bridgeController == null)
      {
         throw new RuntimeException("Setup has not been called");
      }
      
      EventLoopGroup bossGroup = new NioEventLoopGroup(1);
      EventLoopGroup workerGroup = new NioEventLoopGroup();
      try
      {
         ServerBootstrap b = new ServerBootstrap();
         b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).handler(new LoggingHandler(LogLevel.INFO))
          .childHandler(new WebsocketCommunicatorInitializer(webSocketBroadcastHandler, bridgeController));

         Channel ch = b.bind(port).sync().channel();

         System.out.println("Open your web browser and navigate to http://127.0.0.1:" + port + '/');

         ch.closeFuture().sync();
      }
      catch (InterruptedException e)
      {
         e.printStackTrace();
      } 
      finally
      {
         bossGroup.shutdownGracefully();
         workerGroup.shutdownGracefully();
      }
   }

   
   public void runOnAThread()
   {
      new Thread(this).start();
   }

   public void setPort(int port)
   {
      this.port = port;
   }
}
