package com.uniupo.shared.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.UUID;

/**
 * Utility class for MQTT messaging between microservices
 */
public class MqttMessageBroker {
    
    private final String brokerUrl;
    private final String clientId;
    private MqttClient client;
    private final ObjectMapper objectMapper;

    /**
     * Create a new MQTT Message Broker
     * @param host MQTT broker host (default: localhost)
     * @param port MQTT broker port (default: 1883)
     * @param clientId Unique client identifier
     */
    public MqttMessageBroker(String host, int port, String clientId) {
        this.brokerUrl = "tcp://" + host + ":" + port;
        this.clientId = clientId != null ? clientId : "client-" + UUID.randomUUID().toString();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Create a new MQTT Message Broker with default localhost:1883
     * @param clientId Unique client identifier
     */
    public MqttMessageBroker(String clientId) {
        this("localhost", 1883, clientId);
    }

    /**
     * Connect to the MQTT broker
     * @throws MqttException if connection fails
     */
    public void connect() throws MqttException {
        if (client != null && client.isConnected()) {
            return;
        }

        client = new MqttClient(brokerUrl, clientId);
        
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(60);
        
        client.connect(options);
        System.out.println("[MQTT] Connected to broker: " + brokerUrl + " with clientId: " + clientId);
    }

    /**
     * Publish a message to a topic
     * @param topic The MQTT topic
     * @param message The message object (will be serialized to JSON)
     * @throws MqttException if publish fails
     * @throws JsonProcessingException if JSON serialization fails
     */
    public void publish(String topic, Object message) throws MqttException, JsonProcessingException {
        if (client == null || !client.isConnected()) {
            connect();
        }

        String jsonMessage = objectMapper.writeValueAsString(message);
        
        MqttMessage mqttMessage = new MqttMessage(jsonMessage.getBytes());
        mqttMessage.setQos(1); // At least once delivery
        mqttMessage.setRetained(false);
        
        client.publish(topic, mqttMessage);
        System.out.println("[MQTT] Published to topic '" + topic + "': " + jsonMessage);
    }

    /**
     * Subscribe to a topic and handle incoming messages
     * @param topic The MQTT topic (supports wildcards: + for single level, # for multi-level)
     * @param callback The callback to handle received messages
     * @throws MqttException if subscription fails
     */
    public void subscribe(String topic, MessageCallback callback) throws MqttException {
        if (client == null || !client.isConnected()) {
            connect();
        }

        client.subscribe(topic, (t, msg) -> {
            try {
                String message = new String(msg.getPayload());
                System.out.println("[MQTT] Received on topic '" + t + "': " + message);
                callback.onMessage(t, message);
            } catch (Exception e) {
                System.err.println("[MQTT] Error processing message on topic '" + t + "': " + e.getMessage());
                e.printStackTrace();
            }
        });

        System.out.println("[MQTT] Subscribed to topic: " + topic);
    }

    /**
     * Unsubscribe from a topic
     * @param topic The MQTT topic
     * @throws MqttException if unsubscribe fails
     */
    public void unsubscribe(String topic) throws MqttException {
        if (client != null && client.isConnected()) {
            client.unsubscribe(topic);
            System.out.println("[MQTT] Unsubscribed from topic: " + topic);
        }
    }

    /**
     * Check if the client is connected
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return client != null && client.isConnected();
    }

    /**
     * Disconnect from the MQTT broker
     * @throws MqttException if disconnect fails
     */
    public void disconnect() throws MqttException {
        if (client != null && client.isConnected()) {
            client.disconnect();
            System.out.println("[MQTT] Disconnected from broker");
        }
    }

    /**
     * Close the MQTT client and release resources
     * @throws MqttException if close fails
     */
    public void close() throws MqttException {
        disconnect();
        if (client != null) {
            client.close();
        }
    }

    /**
     * Callback interface for handling MQTT messages
     */
    @FunctionalInterface
    public interface MessageCallback {
        /**
         * Called when a message is received
         * @param topic The topic on which the message was received
         * @param message The message content as JSON string
         * @throws Exception if message processing fails
         */
        void onMessage(String topic, String message) throws Exception;
    }
}
