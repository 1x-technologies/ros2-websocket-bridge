package com.halodi.bridge;

import java.io.IOException;

import sensor_msgs.msg.dds.JointState;
import sensor_msgs.msg.dds.JointStatePubSubType;
import us.ihmc.commons.thread.ThreadTools;
import us.ihmc.pubsub.DomainFactory.PubSubImplementation;
import us.ihmc.ros2.ROS2Distro;
import us.ihmc.ros2.ROS2Node;
import us.ihmc.ros2.ROS2Publisher;

public class BridgeTester
{
   public static void main(String[] args) throws IOException
   {
      ROS2Node node = new ROS2Node(PubSubImplementation.FAST_RTPS, ROS2Distro.BOUNCY, Bridge.name, Bridge.namespace);

      ROS2Publisher<JointState> pub = node.createPublisher(new JointStatePubSubType(), Bridge.namespace + "/joint_states");

      
      double q = 0;
      while(true)
      {
         JointState jointState = new JointState();
         jointState.getName().add().append("j_hip_z");
         jointState.getPosition().add(q);
         
         pub.publish(jointState);
         
         q = q + 1.0;
         
         ThreadTools.sleep(1000);
      }
      
   }
}
