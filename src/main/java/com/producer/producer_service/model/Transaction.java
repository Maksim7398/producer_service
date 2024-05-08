package com.producer.producer_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    private UUID requestId;

    private UUID accountIdFrom;

    private BigDecimal amount;

    private String okvedCode;

    private UUID accountIdTo;

    private Integer bik;
}
