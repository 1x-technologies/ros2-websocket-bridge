package com.halodi.bridge;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import us.ihmc.idl.serializers.extra.JSONSerializer;
import us.ihmc.pubsub.TopicDataType;

public class  BridgeSerializer<T> extends JSONSerializer<T>
{

   public BridgeSerializer(TopicDataType<T> topicDataType)
   {
      super(topicDataType);
      setAddTypeAsRootNode(false);
   }
   
   public T deserialize(JsonNode root) throws IOException
   {
      return super.deserialize(root);
      
   }

   public static ObjectMapper createMapper()
   {
      return new ObjectMapper(new JsonFactory());
   }

}
