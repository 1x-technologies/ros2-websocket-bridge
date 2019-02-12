package com.halodi.bridge;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;

import us.ihmc.pubsub.TopicDataType;
import us.ihmc.pubsub.subscriber.Subscriber;
import us.ihmc.ros2.NewMessageListener;

public class BridgeTranslator<T> implements NewMessageListener<T>
{
   private final TopicDataType<T> topicDataType;
   private final BridgeSerializer<T> serializer;
   private final BridgeController controller;
   private final T message;
   private final String topic;
   
   private final boolean reliable;
   
   public BridgeTranslator(String topic, BridgeController controller, TopicDataType<T> topicDataType, boolean reliable)
   {
      this.topic = topic;
      this.topicDataType = topicDataType.newInstance();
      this.serializer = new BridgeSerializer<T>(this.topicDataType);
      this.controller = controller;
      this.message = topicDataType.createData();
      this.reliable = reliable;
   }

   @Override
   public void onNewDataMessage(Subscriber<T> subscriber)
   {
      if(subscriber.takeNextData(message, null))
      {
         try
         {
            String data = serializer.serializeToString(message);
            controller.send(topic, topicDataType.getName(), data, reliable);
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
   }
   
   public T deserialize(JsonNode source) throws IOException
   {
      return serializer.deserialize(source);
   }

}
