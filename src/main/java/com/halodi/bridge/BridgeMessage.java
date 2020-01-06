package com.halodi.bridge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * This is a simple POJO describing the encapsulation for the bridge.
 * 
 * It exists purely for documentation reasons, and is not actually used 
 * 
 * @author Jesper Smith
 *
 */
public class BridgeMessage
{

   /**
    * Type of this message
    */
   private BridgeMessageType type;
   
   /**
    * Which topic name this message refers to
    */
   private String topicName;
   
   /**
    * Which topic type this message refers to
    */
   private String topicDataType;
   
   /**
    * If the message is reliable. 
    * 
    * This means an message gets cached if the connection is busy, instead of dropped
    * 
    *  The ROS2 subscription probably will still be reliable, to guarantee in-order delivery
    */
   private boolean reliable;
   
   /**
    *  Raw data as string for sending. Same as "nodeData" but not parsed by the JSON parser
    */
   private String rawData;
   
   /**
    * Raw data parsed, used on receive. Same as "rawData" but in an intermediate format.
    */
   private JsonNode nodeData;

   public void setType(BridgeMessageType type)
   {
      this.type = type;
   }

   public void setTopicName(String topicName)
   {
      this.topicName = topicName;
   }

   public void setTopicDataType(String topicDataType)
   {
      this.topicDataType = topicDataType;
   }

   public void setData(String data)
   {
      this.rawData = data;
   }
   
   public String getRawData()
   {
      return rawData;
   }

   public BridgeMessageType getType()
   {
      return type;
   }

   public String getTopicName()
   {
      return topicName;
   }

   public String getTopicDataType()
   {
      return topicDataType;
   }

   public JsonNode getData()
   {
      return nodeData;
   }

   public boolean isReliable()
   {
      return reliable;
   }

   public void setReliable(boolean reliable)
   {
      this.reliable = reliable;
   }
   

   public void setNodeData(JsonNode jsonNode)
   {
      this.nodeData = jsonNode;
   }
   
   public static void main(String[] args) throws JsonProcessingException
   {
      
      BridgeMessage msg = new BridgeMessage();
      msg.setType(BridgeMessageType.CREATE_PUBLISHER);
      msg.setReliable(true);
      msg.setTopicName("topic_name");
      msg.setTopicDataType("us.ihmc.pubsub.TopicDataType.getName()");
      msg.setData("[ JSON DATA ]");
      
      BridgeMessageSerializer serializer = new BridgeMessageSerializer();
      
      System.out.println(serializer.createMessage(msg));
   }

   

}
