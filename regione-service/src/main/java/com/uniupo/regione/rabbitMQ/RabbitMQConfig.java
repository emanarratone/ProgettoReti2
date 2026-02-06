package com.uniupo.regione.rabbitMQ;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String REGIONE_EXCHANGE = "regione.exchange";
    public static final String REGIONE_ROUTING_KEY = "regione.deleted";

    @Bean
    public TopicExchange regioneExchange() {
        return new TopicExchange(REGIONE_EXCHANGE);
    }
}