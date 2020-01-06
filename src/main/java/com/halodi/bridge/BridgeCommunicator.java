package com.halodi.bridge;

public interface BridgeCommunicator
{   
   void setup(BridgeController bridgeController);
   
   void send(String topicName, String msg, boolean reliable);
}
