package com.halodi.bridge;

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import us.ihmc.log.LogTools;
import us.ihmc.pubsub.DomainFactory.PubSubImplementation;
import us.ihmc.pubsub.TopicDataType;
import us.ihmc.ros2.ROS2Distro;
import us.ihmc.ros2.ROS2Node;
import us.ihmc.ros2.ROS2Publisher;
import us.ihmc.ros2.Ros2Subscription;

public class BridgeNode
{
   private final class SubscriptionHolder
   {
      @SuppressWarnings("unused")
      private final Ros2Subscription<?> subscription;
      private final String dataType;

      public SubscriptionHolder(Ros2Subscription<?> subscription, String dataType)
      {
         this.subscription = subscription;
         this.dataType = dataType;
      }

   }

   private final class PublisherHolder<T>
   {
      private final ROS2Publisher<T> publisher;
      private final TopicDataType<T> dataType;
      private final BridgeTranslator<T> bridgeTranslator;

      public PublisherHolder(String topic, BridgeController controller, ROS2Publisher<T> publisher, TopicDataType<T> dataType)
      {
         super();
         this.publisher = publisher;
         this.dataType = dataType.newInstance();
         this.bridgeTranslator = new BridgeTranslator<>(topic, controller, this.dataType, true);
      }

   }

   private final PacketRegistrationInterface registration;
   private final ROS2Node node;

   private final HashMap<String, SubscriptionHolder> subscriptions = new HashMap<>();
   @SuppressWarnings("rawtypes")
   private final HashMap<String, PublisherHolder> publishers = new HashMap<>();

   public BridgeNode(ROS2Node node, PacketRegistrationInterface registration, String name, String namespace) throws IOException
   {
      if(node != null)
      {
         this.node = node;
      }
      else
      {
         this.node = new ROS2Node(PubSubImplementation.FAST_RTPS, ROS2Distro.BOUNCY, name, namespace);
      }
      this.registration = registration;
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public void createSubscriber(String name, String dataTypeName, BridgeController controller, boolean reliable) throws IOException
   {
      if (subscriptions.containsKey(name))
      {
         if (!subscriptions.get(name).dataType.equals(dataTypeName))
         {
            throw new IOException("Already subscribed to " + name + " but not for data type " + dataTypeName);
         }
      }
      else
      {

         TopicDataType<?> topicDataType = getTopicDataType(dataTypeName);
         Ros2Subscription<?> subscription = node.createSubscription(topicDataType, new BridgeTranslator(name, controller, topicDataType, reliable), name);

         subscriptions.put(name, new SubscriptionHolder(subscription, dataTypeName));
         
         LogTools.info("Created subscriber " + name);
      }
   }

   private TopicDataType<?> getTopicDataType(String dataTypeName) throws IOException
   {
      TopicDataType<?> topicDataType = registration.getPubSubType(dataTypeName);
      if (topicDataType == null)
      {
         throw new IOException("Unknown data type " + dataTypeName);
      }
      return topicDataType;
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   public void createPublisher(String name, String dataTypeName, BridgeController controller) throws IOException
   {
      if (publishers.containsKey(name))
      {
         if (!publishers.get(name).dataType.getName().equals(dataTypeName))
         {
            throw new IOException("Publisher with name " + name + " exists, but not for data type " + dataTypeName);
         }
      }
      else
      {
         TopicDataType<?> topicDataType = getTopicDataType(dataTypeName);
         ROS2Publisher<?> publisher = node.createPublisher(topicDataType, name);
         publishers.put(name, new PublisherHolder(name, controller, publisher, topicDataType));
         
         LogTools.info("Created publisher " + name);
      }
   }

   @SuppressWarnings("unchecked")
   public void publish(String name, String dataTypeName, JsonNode message) throws IOException
   {

      PublisherHolder<Object> publisherHolder = publishers.get(name);
      if (publisherHolder != null && publisherHolder.dataType.getName().equals(dataTypeName))
      {
         Object ddsMessage = publisherHolder.bridgeTranslator.deserialize(message);
         publisherHolder.publisher.publish(ddsMessage);
      }
      else
      {
         throw new IOException("No publisher for name " + name + "  and datatype " + dataTypeName);
      }

   }
}
