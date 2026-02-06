package com.uniupo.autostrada.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 1. Coda per ricevere messaggi dalla Regione
    public static final String REGIONE_DELETED_QUEUE = "regione.deleted.queue";

    // 2. Exchange per inviare messaggi ai Caselli
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

    // Specifichiamo il Binding: agganciamo la nostra coda all'exchange della Regione
    @Bean
    public Binding bindingRegione(Queue regioneDeletedQueue) {
        // "regione.exchange" deve corrispondere a quello definito nel servizio Regione
        return BindingBuilder.bind(regioneDeletedQueue)
                .to(new TopicExchange("regione.exchange"))
                .with("regione.deleted");
    }
}