package com.producer.producer_service.controller;

import com.producer.producer_service.controller.request.PaymentDetails;
import com.producer.producer_service.controller.response.TransferResponse;
import com.producer.producer_service.service.transaction.TransactionServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user/transfer")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    @PostMapping("/{userSourceId}")
    public TransferResponse createTransaction(@PathVariable UUID userSourceId, @RequestBody @Valid PaymentDetails paymentDetails) {
        return transactionService.createTransaction(userSourceId, paymentDetails);
    }

}
