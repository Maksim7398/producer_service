package com.producer.producer_service.controller.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetails {

    @NotNull(message = "account source id from must not be null")
    private UUID accountSourceId;
    @NotNull(message = "account user destination id from must not be null")
    private UUID userDestinationId;
    @NotNull(message = "account destination id from must not be null")
    private UUID accountDestinationId;

    @NotNull(message = "amount must not be null")
    private BigDecimal amount;


    private String okvedCode;
}
