package com.producer.producer_service.controller.response;

import com.producer.producer_service.persist.entity.StatusPayment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {

    private String message;

    private StatusPayment statusPayment;

    private BigDecimal amount;
}
