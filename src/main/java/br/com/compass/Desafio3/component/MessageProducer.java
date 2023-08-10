package br.com.compass.Desafio3.component;

import jakarta.jms.ConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    private final JmsTemplate jmsTemplate;

    public MessageProducer(ConnectionFactory connectionFactory) {
        this.jmsTemplate = new JmsTemplate(connectionFactory);
    }

    public void sendMessage(String message) {
        jmsTemplate.convertAndSend("queueName", message);
    }
}
