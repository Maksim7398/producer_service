package com.producer.producer_service.controller;

import com.producer.producer_service.controller.request.CreateTransaction;
import com.producer.producer_service.kafka.service.KafkaProducerService;
import com.producer.producer_service.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final KafkaProducerService kafkaProducerService;

    @PostMapping
    public String createTransaction(@RequestBody CreateTransaction createTransaction) throws Exception {
        final Transaction transaction = Transaction.builder()
                .accountIdFrom(createTransaction.getAccountIdFrom())
                .amount(createTransaction.getAmount())
                .okvedCode(createTransaction.getOkvedCode())
                .accountIdTo(createTransaction.getAccountIdTo())
                .bik(createTransaction.getBik())
                .build();
        kafkaProducerService.sendTransaction(transaction);
        return "Message send";
    }
}
