package com.uniupo.casello.mqtt;

import com.uniupo.shared.mqtt.MqttMessageBroker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {

    @Value("${mqtt.broker.host:localhost}")
    private String mqttHost;

    @Value("${mqtt.broker.port:1883}")
    private int mqttPort;

    @Bean
    public MqttMessageBroker mqttMessageBroker() {
        return new MqttMessageBroker(mqttHost, mqttPort, "casello-service");
    }
}
