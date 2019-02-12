package com.halodi.bridge;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;

import us.ihmc.log.LogTools;
import us.ihmc.ros2.Ros2Node;

public class BridgeController
{
   private final BridgeCommunicator communicator;
   private final BridgeNode node;
   private final BridgeMessage messageToSend = new BridgeMessage();
   private final BridgeMessage messageToReceive = new BridgeMessage();
   
   public BridgeController(Ros2Node node, BridgeCommunicator communicator, PacketRegistrationInterface packetRegistration, String name, String namespace) throws IOException
   {
      this.communicator = communicator;
      this.node = new BridgeNode(node, packetRegistration, name, namespace);
      
      this.communicator.setup(this);
   }
   
   private void createPublisher() throws IOException
   {
      node.createPublisher(messageToReceive.getTopicName(), messageToReceive.getTopicDataType(), this);
   }
   
   private void createSubscriber() throws IOException
   {
      node.createSubscriber(messageToReceive.getTopicName(), messageToReceive.getTopicDataType(), this, messageToReceive.isReliable());
   }
   
   private void receivedData() throws IOException
   {
      node.publish(messageToReceive.getTopicName(), messageToReceive.getTopicDataType(), messageToReceive.getData());
   }
   
   public void receivedMessage(String msg)
   {
      try
      {
         messageToReceive.parseMessage(msg);
         
         switch(messageToReceive.getType())
         {
         case CREATE_PUBLISHER:
            createPublisher();
            break;
         case CREATE_SUBSCRIBER:
            createSubscriber();
            break;
         case DATA:
            receivedData();
            break;
         }
      }
      catch (IOException e)
      {
         e.printStackTrace();
         
         LogTools.error(e.getMessage());
      }
   }
   
   public void send(String topicName, String topicDataType, String data, boolean reliable)
   {
      messageToSend.setTopicName(topicName);
      messageToSend.setTopicDataType(topicDataType);
      messageToSend.setType(BridgeMessageType.DATA);
      messageToSend.setData(data);
      
      try
      {
         communicator.send(messageToSend.createMessage(), reliable);
      }
      catch (JsonProcessingException e)
      {
         e.printStackTrace();
      }
   }
}
