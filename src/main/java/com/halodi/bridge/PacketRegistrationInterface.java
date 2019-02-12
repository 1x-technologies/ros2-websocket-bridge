package com.halodi.bridge;

import us.ihmc.pubsub.TopicDataType;

public interface PacketRegistrationInterface
{

   TopicDataType<?> getPubSubType(String name);

}