package com.topleague.predict.infrastructure.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;

@Configuration
public class RabbitMQConfig {
    private static final String GAME_EXCHANGE = "topleague.game.exchange";
    private static final String GAME_UPDATED_QUEUE = "topleague.game.updated.top-predict-queue";
    private static final String GAME_UPDATED_ROUTING_KEY = "topleague.game.updated";

    @Bean
    public TopicExchange gameExchange() {
        return ExchangeBuilder.topicExchange(GAME_EXCHANGE).durable(true).build();
    }
    @Bean
    public Queue gameUpdatedQueue() {
        return QueueBuilder.durable(GAME_UPDATED_QUEUE).build();
    }
    @Bean
    public Binding gameUpdatedBinding(Queue gameUpdatedQueue, TopicExchange gameExchange) {
        return BindingBuilder.bind(gameUpdatedQueue).to(gameExchange).with(GAME_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter consumerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
