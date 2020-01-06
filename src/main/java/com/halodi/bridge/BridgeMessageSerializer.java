package com.halodi.bridge;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.RawValue;

public class BridgeMessageSerializer
{
   private final ObjectMapper mapper = BridgeSerializer.createMapper();

   public String createMessage(BridgeMessage message) throws JsonProcessingException
   {
      ObjectNode root = mapper.createObjectNode();
      root.put("type", message.getType().toString());
      root.put("topicName", message.getTopicName());
      root.put("topicDataType", message.getTopicDataType());
      root.put("reliable", message.isReliable());

      if (message.getRawData() != null)
      {
         root.putRawValue("data", new RawValue(message.getRawData()));
      }
      return mapper.writeValueAsString(root);
   }

   public void parseMessage(String msg, BridgeMessage message) throws IOException
   {
      JsonNode node = mapper.readTree(msg);

      if (node.has("type"))
      {
         try
         {
            message.setType(BridgeMessageType.valueOf(node.get("type").asText()));
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

      if (node.has("topicName"))
      {
         message.setTopicName(node.get("topicName").asText());
      }
      else
      {
         throw new IOException("No field topicName");
      }

      if (node.has("topicDataType"))
      {
         message.setTopicDataType(node.get("topicDataType").asText());
      }
      else
      {
         throw new IOException("No field topicDataType");
      }

      if (node.has("reliable"))
      {
         message.setReliable(node.get("reliable").asBoolean());
      }
      else
      {
         throw new IOException("No field reliable");
      }

      if (node.has("data"))
      {
         message.setNodeData(node.get("data"));
      }
      else
      {
         message.setNodeData(null);
      }

   }

}
