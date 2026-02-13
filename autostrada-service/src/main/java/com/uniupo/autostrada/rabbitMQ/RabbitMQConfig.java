package com.uniupo.autostrada.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


    public static final String REGIONE_DELETED_QUEUE = "regione.deleted.queue";
    public static final String AUTOSTRADA_EXCHANGE = "autostrada.exchange";
    public static final String AUTOSTRADA_ROUTING_KEY = "autostrada.deleted";

    @Bean
    public Queue regioneDeletedQueue() {
        return new Queue(REGIONE_DELETED_QUEUE, true);
    }

    @Bean
    public TopicExchange autostradaExchange() {
        return new TopicExchange(AUTOSTRADA_EXCHANGE);
    }


    @Bean
    public Binding bindingRegione(Queue regioneDeletedQueue) {

        return BindingBuilder.bind(regioneDeletedQueue)
                .to(new TopicExchange("regione.exchange"))
                .with("regione.deleted");
    }
}