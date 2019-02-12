package com.halodi.bridge;

import java.io.IOException;

import sensor_msgs.msg.dds.JointStatePubSubType;
import trajectory_msgs.msg.dds.JointTrajectory;
import trajectory_msgs.msg.dds.JointTrajectoryPubSubType;
import us.ihmc.commons.thread.ThreadTools;

public class DummyCommunicator implements BridgeCommunicator
{

   private BridgeController controller;

   public DummyCommunicator()
   {
   }

   @Override
   public void send(String msg, boolean reliable)
   {
      System.out.println(msg);
   }

   void start()
   {
      BridgeMessage subscribeToJointState = new BridgeMessage();
      subscribeToJointState.setTopicName("/jointState");
      subscribeToJointState.setTopicDataType(JointStatePubSubType.name);
      subscribeToJointState.setType(BridgeMessageType.CREATE_SUBSCRIBER);

      BridgeMessage publishToJointTrajectory = new BridgeMessage();
      publishToJointTrajectory.setTopicName("/jointTrajectory");
      publishToJointTrajectory.setTopicDataType(JointTrajectoryPubSubType.name);
      publishToJointTrajectory.setType(BridgeMessageType.CREATE_PUBLISHER);

      try
      {
         controller.receivedMessage(subscribeToJointState.createMessage());
         controller.receivedMessage(publishToJointTrajectory.createMessage());

         JointTrajectoryPubSubType jointTrajectoryPubSubType = new JointTrajectoryPubSubType();
         BridgeSerializer<JointTrajectory> serializer = new BridgeSerializer<>(jointTrajectoryPubSubType);

         double d = 0.0;
         while (true)
         {
            JointTrajectory trajectory = new JointTrajectory();
            trajectory.getJointNames().add("j_hip_z");
            trajectory.getPoints().add().getPositions().add(d);

            String msg = serializer.serializeToString(trajectory);
            
            BridgeMessage encapsulatedMessage = new BridgeMessage();
            encapsulatedMessage.setTopicName("/jointTrajectory");
            encapsulatedMessage.setTopicDataType(JointTrajectoryPubSubType.name);
            encapsulatedMessage.setType(BridgeMessageType.DATA);
            encapsulatedMessage.setData(msg);

            controller.receivedMessage(encapsulatedMessage.createMessage());

            d += 1.0;
            ThreadTools.sleep(1000);
         }

      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Override
   public void setup(BridgeController bridgeController)
   {
      this.controller = bridgeController;
   }

}
