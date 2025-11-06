package com.example.designation.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_PRINCIPAL = "lote.exchange.v1";
    public static final String QUEUE_LOTE_FAIXAS = "lote.faixas.v1";
    public static final String DEAD_LETTER_QUEUE = QUEUE_LOTE_FAIXAS + ".dlq";

    @Bean
    public DirectExchange exchangePrincipal() {
        return new DirectExchange(EXCHANGE_PRINCIPAL);
    }

    // --- Filas ---
    @Bean
    public Queue queueLoteFaixas() {
        return QueueBuilder.durable(QUEUE_LOTE_FAIXAS)
                .withArgument("x-dead-letter-exchange", "") // Usa o exchange padrão
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_QUEUE) // Roteia para a DLQ
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        // A DLQ é apenas uma fila normal que recebe as mensagens rejeitadas
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    // --- Bindings ---
    @Bean
    public Binding bindingLoteFaixas() {
        return BindingBuilder.bind(queueLoteFaixas()).to(exchangePrincipal()).with(QUEUE_LOTE_FAIXAS);
    }
    
    // --- Message Converter ---
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
