package com.example.delivery.service.impl;

import com.example.delivery.config.RabbitMQConfig;
import com.example.delivery.service.MessagingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQMessagingServiceImpl implements MessagingService {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQMessagingServiceImpl.class);

    // O Spring Boot configura automaticamente este template para nós.
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMQMessagingServiceImpl(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void enviarMensagemStatusPedido(String mensagem) {
        try {
            // Envia a mensagem para a fila especificada
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_PEDIDO_STATUS, mensagem);
            log.info("Mensagem enviada para a fila '{}': {}", RabbitMQConfig.QUEUE_PEDIDO_STATUS, mensagem);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem para a fila: {}", e.getMessage());
        }
    }
}
