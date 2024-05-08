package com.producer.producer_service.kafka.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.producer.producer_service.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplateString;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.kafka.topic}")
    private String TRANSACTIONAL_TOPIC;

    public void sendTransaction(final Transaction transaction) throws Exception {
        final String message = objectMapper.writeValueAsString(transaction);
        final CompletableFuture<SendResult<String, String>> sendMessage =
                kafkaTemplateString.send(TRANSACTIONAL_TOPIC, message);
        log.info(sendMessage.get().getRecordMetadata().topic());
        log.info("Send message: {}",sendMessage.get().getProducerRecord().value());
    }
}
