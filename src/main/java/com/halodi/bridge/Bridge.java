package com.halodi.bridge;

import java.io.IOException;

import com.halodi.bridge.websocket.WebsocketCommunicator;

import us.ihmc.commons.thread.ThreadTools;
import us.ihmc.ros2.Ros2Node;

/**
 * This is an example class to create a ROS2 bridge.
 * 
 * What you want to change
 *    - name
 *    - namespace
 *    - PacketRegistrion
 *    
 * Implement your own PacketRegistrationInterface and add all packets you want to register
 * 
 * @author Jesper Smith
 *
 */
public class Bridge
{
   public static final String name = "bridge";
   public static final String namespace = "";
   
   public static void create() throws IOException
   {
      create(null);
   }
   
   public static void create(Ros2Node node) throws IOException
   {
      PacketRegistrationInterface packetRegistration = new DefaultPacketRegistration();
      WebsocketCommunicator communicator = new WebsocketCommunicator();
      new BridgeController(node, communicator, packetRegistration, name, namespace);
      
      communicator.runOnAThread();
   }
   
   
   public static void main(String[] args) throws IOException
   {
      Bridge.create();
      ThreadTools.sleepForever();
   }
}
