package com.example.consumer.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoStatusListener {

    private static final Logger log = LoggerFactory.getLogger(PedidoStatusListener.class);

    // O nome da fila deve ser EXATAMENTE o mesmo definido no produtor
    public static final String QUEUE_NAME = "pedido.status.v1";

    // A anotação @RabbitListener faz toda a mágica.
    // O Spring vai automaticamente criar um consumidor para esta fila
    // e chamar este método sempre que uma nova mensagem chegar.
    @RabbitListener(queues = QUEUE_NAME)
    public void onPedidoStatusChanged(String mensagem) {
        log.info("====================================================");
        log.info("MENSAGEM RECEBIDA DA FILA '{}'", QUEUE_NAME);
        log.info("Conteúdo: {}", mensagem);
        log.info("Processando a mensagem (ex: enviando notificação, atualizando outro sistema, etc.)...");
        log.info("====================================================");

        // Aqui você poderia adicionar a lógica para enviar um email,
        // uma notificação push, chamar outra API, etc.
    }
}
