package com.uniupo.casello.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 1. Coda per ricevere messaggi dall'Autostrada
    public static final String AUTOSTRADA_DELETED_QUEUE = "autostrada.deleted.queue";

    // 2. Exchange per inviare messaggi alle Corsie
    public static final String CASELLO_EXCHANGE = "casello.exchange";
    public static final String CASELLO_ROUTING_KEY = "casello.deleted";

    @Bean
    public Queue autostradaDeletedQueue() {
        return new Queue(AUTOSTRADA_DELETED_QUEUE, true);
    }

    @Bean
    public TopicExchange caselloExchange() {
        return new TopicExchange(CASELLO_EXCHANGE);
    }

    // Binding: colleghiamo la coda del Casello all'exchange dell'Autostrada
    @Bean
    public Binding bindingAutostrada(Queue autostradaDeletedQueue) {
        return BindingBuilder.bind(autostradaDeletedQueue)
                .to(new TopicExchange("autostrada.exchange"))
                .with("autostrada.deleted");
    }
}