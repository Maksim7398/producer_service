package com.producer.producer_service.service;

import com.producer.producer_service.controller.request.PaymentDetails;
import com.producer.producer_service.controller.response.TransferResponse;
import com.producer.producer_service.kafka.service.KafkaProducerService;
import com.producer.producer_service.persist.entity.Account;
import com.producer.producer_service.persist.entity.Bank;
import com.producer.producer_service.persist.entity.UserEntity;
import com.producer.producer_service.persist.repository.AccountRepository;
import com.producer.producer_service.persist.repository.PaymentTableRepository;
import com.producer.producer_service.persist.repository.UserEntityRepository;
import com.producer.producer_service.service.transaction.TransactionServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionServiceImpl underTest;

    @Mock
    private PaymentTableRepository paymentTableRepositoryMock;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserEntityRepository userRepository;

    @Mock
    private KafkaProducerService kafkaProducerServiceMock;

    @Test
    public void createTransactionTest() {

        Account accountFrom = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber(12345)
                .createAt(LocalDate.now())
                .bikCode(new Bank(UUID.randomUUID(), "Bank", 111111))
                .balance(new BigDecimal("1000"))
                .build();

        UserEntity userFrom = UserEntity.builder()
                .id(UUID.randomUUID())
                .phone("796033333333")
                .firstName("userFrom")
                .lastName("userFrom")
                .email("userFrom@mail.ru")
                .accounts(List.of(accountFrom))
                .build();


        Account accountTO = Account.builder()
                .id(UUID.randomUUID())
                .accountNumber(11111)
                .createAt(LocalDate.now())
                .bikCode(new Bank(UUID.randomUUID(), "Bank", 111111))
                .balance(new BigDecimal("1000"))
                .build();

        UserEntity userTo = UserEntity.builder()
                .id(UUID.randomUUID())
                .phone("79601111111")
                .firstName("userTO")
                .lastName("userTO")
                .email("userTO@mail.ru")
                .accounts(List.of(accountTO))
                .build();

        when(userRepository.findById(userTo.getId())).thenReturn(Optional.of(userTo));
        when(userRepository.findById(userFrom.getId())).thenReturn(Optional.of(userFrom));


        PaymentDetails paymentDetails = PaymentDetails.builder()
                .accountDestinationId(accountTO.getId())
                .accountSourceId(accountFrom.getId())
                .okvedCode("10.1")
                .userDestinationId(userTo.getId())
                .amount(new BigDecimal("100")).build();
        TransferResponse transaction = underTest.createTransaction(userFrom.getId(), paymentDetails);

        verify(paymentTableRepositoryMock).save(any());
        verify(accountRepository).save(accountTO);
        verify(accountRepository).save(accountFrom);

        Assertions.assertEquals(transaction.getAmount(),paymentDetails.getAmount());

    }
}
