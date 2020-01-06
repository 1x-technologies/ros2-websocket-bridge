package com.halodi.bridge;

import java.util.HashSet;

public class BridgeClient<T>
{  
   private final HashSet<String> subscribedTopics = new HashSet<>();
   private final HashSet<String> publishingTopics = new HashSet<>();
   
   private T implementation;
   
   public BridgeClient()
   {
   }
   
   public void addSubscribedTopic(String topicName)
   {
      subscribedTopics.add(topicName);
   }
   
   public void addPublishedTopic(String topicName)
   {
      publishingTopics.add(topicName);
   }
   
   
   public boolean isSubscribedTo(String topicName)
   {
      return subscribedTopics.contains(topicName);
   }
   
   public boolean isPublishingTo(String topicName)
   {
      return publishingTopics.contains(topicName);
   }

   public T getImplementation()
   {
      return implementation;
   }

   public void setImplementation(T implementation)
   {
      this.implementation = implementation;
   }
   
}
