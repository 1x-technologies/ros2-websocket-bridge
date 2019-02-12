package com.halodi.bridge;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;

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
   private final ObjectMapper mapper = BridgeSerializer.createMapper();

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

   public String createMessage() throws JsonProcessingException
   {
      ObjectNode root = mapper.createObjectNode();
      root.put("type", type.toString());
      root.put("topicName", topicName);
      root.put("topicDataType", topicDataType);
      root.put("reliable", reliable);
      
      if (rawData != null)
      {
         root.putRawValue("data", new RawValue(rawData));
      }
      return mapper.writeValueAsString(root);
   }
   
   public void parseMessage(String msg) throws IOException
   {
      JsonNode node = mapper.readTree(msg);
      
      if(node.has("type"))
      {
         try
         {
            type = BridgeMessageType.valueOf(node.get("type").asText());
         }
         catch (IllegalArgumentException e)
         {
            System.out.println(msg);
            throw new IOException(e);
         }
      }
      else
      {
         throw new IOException("No field type");
      }
      
      if(node.has("topicName"))
      {
         topicName = node.get("topicName").asText();
      }
      else
      {
         throw new IOException("No field topicName");
      }
      
      if(node.has("topicDataType"))
      {
         topicDataType = node.get("topicDataType").asText();
      }
      else
      {
         throw new IOException("No field topicDataType");
      }
      
      if(node.has("reliable"))
      {
         reliable = node.get("reliable").asBoolean();
      }
      else
      {
         throw new IOException("No field reliable");
      }
      
      if(node.has("data"))
      {
         nodeData = node.get("data");
      }
      else
      {
         nodeData = null;
      }
      
   }

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
   
   public static void main(String[] args) throws JsonProcessingException
   {
      
      BridgeMessage msg = new BridgeMessage();
      msg.setType(BridgeMessageType.CREATE_PUBLISHER);
      msg.setReliable(true);
      msg.setTopicName("topic_name");
      msg.setTopicDataType("us.ihmc.pubsub.TopicDataType.getName()");
      msg.setData("[ JSON DATA ]");
      System.out.println(msg.createMessage());
   }
   

}
