package com.halodi.bridge;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;

import us.ihmc.log.LogTools;
import us.ihmc.ros2.Ros2Node;

public class BridgeController
{
   private final BridgeCommunicator communicator;
   private final BridgeNode node;
   
   private final BridgeMessageSerializer serializer = new BridgeMessageSerializer();
   
   public BridgeController(Ros2Node node, BridgeCommunicator communicator, PacketRegistrationInterface packetRegistration, String name, String namespace) throws IOException
   {
      this.communicator = communicator;
      this.node = new BridgeNode(node, packetRegistration, name, namespace);
      
      this.communicator.setup(this);
   }
   
   private void createPublisher(BridgeMessage messageToReceive, BridgeClient<?> bridgeClient) throws IOException
   {
      node.createPublisher(messageToReceive.getTopicName(), messageToReceive.getTopicDataType(), this);
      bridgeClient.addPublishedTopic(messageToReceive.getTopicName());
   }
   
   private void createSubscriber(BridgeMessage messageToReceive, BridgeClient<?> bridgeClient) throws IOException
   {
      node.createSubscriber(messageToReceive.getTopicName(), messageToReceive.getTopicDataType(), this, messageToReceive.isReliable());
      bridgeClient.addSubscribedTopic(messageToReceive.getTopicName());
   }
   
   private void receivedData(BridgeMessage messageToReceive, BridgeClient<?> bridgeClient) throws IOException
   {
      if(!bridgeClient.isPublishingTo(messageToReceive.getTopicName()))
      {
         System.err.println("Received message for publishing to unmatched topic " + messageToReceive.getTopicName());
         return;
      }
      
      node.publish(messageToReceive.getTopicName(), messageToReceive.getTopicDataType(), messageToReceive.getData());
   }
   
   public void receivedMessage(BridgeClient<?> bridgeClient, String msg)
   {
      try
      {
         BridgeMessage messageToReceive = new BridgeMessage(); 
         serializer.parseMessage(msg, messageToReceive);
         
         switch(messageToReceive.getType())
         {
         case CREATE_PUBLISHER:
            createPublisher(messageToReceive, bridgeClient);
            break;
         case CREATE_SUBSCRIBER:
            createSubscriber(messageToReceive, bridgeClient);
            break;
         case DATA:
            receivedData(messageToReceive, bridgeClient);
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
      BridgeMessage messageToSend = new BridgeMessage();
      messageToSend.setTopicName(topicName);
      messageToSend.setTopicDataType(topicDataType);
      messageToSend.setType(BridgeMessageType.DATA);
      messageToSend.setData(data);
      
      try
      {
         communicator.send(topicName, serializer.createMessage(messageToSend), reliable);
      }
      catch (JsonProcessingException e)
      {
         e.printStackTrace();
      }
   }
}
