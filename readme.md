# ROS2 to Websocket bridge

## Quick overview

Look at "com.halodi.bridge.Bridge" to setup your own bridge. Create your own PacketRegistrationInterface.

## Protocol

The bridge listens on ws://[hostname]:8008/websocket

All messages are text based websocket frames with JSON data.

All messages are wrapped in a simple JSON wrapper with the following structure (see BridgeMessage.java)


```
{
   "type":"MESSAGE_TYPE",
   "topicName":"topic_name",
   "topicDataType":"us.ihmc.pubsub.TopicDataType.getName()",
   "reliable":true,
   "data": < Optional JSON Data >
}
```


MESSAGE_TYPE can be one of the following (see BridgeMessageType.java)

```
   CREATE_SUBSCRIBER,
   CREATE_PUBLISHER,
   DATA
```

To subscribe to a topic, send a message with message type CREATE_SUBSCRIBER to the bridge, with the correct topicName and topicDataType set. Data can be empty or leave it out altogether.
- To get all messages, set reliable to true. However, this can bog up if the network cannot keep up so for frequent messages set reliable to false.

To start publishing on a topic, send a message with message type CREATE_PUBLISHER to the bridge, with the correct topicName and topicDataType set. Data can be empty or leave it out altogether.

Messages received on the ROS2 side are send with type "DATA", topicName and topicDataType set based on the topic and data set to the JSON serialized data.

To publish a message, set type to "DATA", topicName and topicDataType correctly and the serialized data in JSON format in "data". Make sure you registered the publisher first. 
- Easiest way to get the correct JSON format is to subscribe to a topic.


#Notes

- Test using "Bridge" and "BridgeTester"
- BridgeTester publishes "JointState" messages on /joint_states. You can subscribe using the Bridge.
- Can only subscribe to topics with topic data types registered in PacketRegistrationInterface.
- Use IHMCRTPSVisualizer to debug subscriptions.
- Test connection by using your browser to go to "http://127.0.0.1:8080/websocket". It'll say "not a WebSocket handshake request: missing upgrade"

#TODO

- Add option to serve HTTP pages
- Documentation
- Write sample client