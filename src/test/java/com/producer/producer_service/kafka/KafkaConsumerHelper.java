package com.producer.producer_service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.producer.producer_service.model.TransactionMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;


@Service
@Slf4j
public class KafkaConsumerHelper {

    ConcurrentHashMap<String, TransactionMessage> messageConcurrentHashMap = new ConcurrentHashMap<>();


    @KafkaListener(topics = "transaction_topic", containerFactory = "kafkaListenerContainerFactoryStringTransactionMessage")
    public void readMessage(String message) {

        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            TransactionMessage transactionMessage = objectMapper.readValue(message, TransactionMessage.class);
            messageConcurrentHashMap.put("transaction_topic", transactionMessage);
            log.info("MESSAGE READING: {}", transactionMessage);
        } catch (JsonProcessingException e) {
            log.error("MESSAGE NOT READING");
            throw new RuntimeException(e);
        }
    }
}
