package com.example.delivery.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Define o nome da nossa fila
    public static final String QUEUE_PEDIDO_STATUS = "pedido.status.v1";

    @Bean
    public Queue queuePedidoStatus() {
        // Cria uma fila durável (sobrevive a reinicializações do RabbitMQ)
        return QueueBuilder.durable(QUEUE_PEDIDO_STATUS).build();
    }
}
