package com.uniupo.dispositivi.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CORSIA_DELETED_QUEUE = "corsia.deleted.queue";

    @Bean
    public Queue corsiaDeletedQueue() {
        return new Queue(CORSIA_DELETED_QUEUE, true);
    }

    @Bean
    public Binding bindingCorsia(Queue corsiaDeletedQueue) {
        // Ci colleghiamo all'exchange creato dal microservizio Corsia
        return BindingBuilder.bind(corsiaDeletedQueue)
                .to(new TopicExchange("corsia.exchange"))
                .with("corsia.deleted");
    }
}