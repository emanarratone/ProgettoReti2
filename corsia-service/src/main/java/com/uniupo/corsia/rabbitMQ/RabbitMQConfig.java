package com.uniupo.corsia.rabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CASELLO_DELETED_QUEUE = "casello.deleted.queue";
    public static final String CORSIA_EXCHANGE = "corsia.exchange";
    public static final String CORSIA_ROUTING_KEY = "corsia.deleted";

    @Bean
    public Queue caselloDeletedQueue() {
        return new Queue(CASELLO_DELETED_QUEUE, true);
    }

    @Bean
    public TopicExchange corsiaExchange() {
        return new TopicExchange(CORSIA_EXCHANGE);
    }

    @Bean
    public Binding bindingCasello(Queue caselloDeletedQueue) {
        return BindingBuilder.bind(caselloDeletedQueue)
                .to(new TopicExchange("casello.exchange"))
                .with("casello.deleted");
    }
}