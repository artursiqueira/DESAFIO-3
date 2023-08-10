package br.com.compass.Desafio3.component;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    @JmsListener(destination = "queueName")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
    }
}
