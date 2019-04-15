package com.halodi.bridge;

import java.util.HashMap;

import diagnostic_msgs.msg.dds.DiagnosticArrayPubSubType;
import geometry_msgs.msg.dds.TwistPubSubType;
import geometry_msgs.msg.dds.TwistStampedPubSubType;
import geometry_msgs.msg.dds.Vector3PubSubType;
import sensor_msgs.msg.dds.JointStatePubSubType;
import std_msgs.msg.dds.Float64PubSubType;
import trajectory_msgs.msg.dds.JointTrajectoryPubSubType;
import us.ihmc.pubsub.TopicDataType;

public class DefaultPacketRegistration implements PacketRegistrationInterface
{
   private final HashMap<String, TopicDataType<?>> topicDataTypes = new HashMap<>();
   
   public DefaultPacketRegistration()
   {
      topicDataTypes.put(JointStatePubSubType.name, new JointStatePubSubType());
      topicDataTypes.put(JointTrajectoryPubSubType.name, new JointTrajectoryPubSubType());
      topicDataTypes.put(TwistPubSubType.name, new TwistPubSubType());
      topicDataTypes.put(TwistStampedPubSubType.name, new TwistStampedPubSubType());
      topicDataTypes.put(Float64PubSubType.name, new Float64PubSubType());
      topicDataTypes.put(DiagnosticArrayPubSubType.name, new DiagnosticArrayPubSubType());
      topicDataTypes.put(Vector3PubSubType.name, new Vector3PubSubType());
   }
   
   @Override
   public TopicDataType<?> getPubSubType(String name)
   {
      return topicDataTypes.get(name);
   }
}
