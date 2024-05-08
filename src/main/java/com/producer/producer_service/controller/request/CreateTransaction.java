package com.producer.producer_service.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateTransaction {

    private UUID accountIdFrom;

    private BigDecimal amount;

    private String okvedCode;

    private UUID accountIdTo;

    private Integer bik;

}
