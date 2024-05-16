package com.producer.producer_service.service.transaction;

import com.producer.producer_service.exception.InsufficientFundsOnTheCardException;
import com.producer.producer_service.exception.UserNotFoundException;
import com.producer.producer_service.kafka.service.KafkaProducerService;
import com.producer.producer_service.controller.request.PaymentDetails;
import com.producer.producer_service.model.TransactionMessage;
import com.producer.producer_service.controller.response.TransferResponse;
import com.producer.producer_service.persist.entity.Account;
import com.producer.producer_service.persist.entity.PaymentTable;
import com.producer.producer_service.persist.entity.StatusPayment;
import com.producer.producer_service.persist.entity.UserEntity;
import com.producer.producer_service.persist.repository.AccountRepository;
import com.producer.producer_service.persist.repository.PaymentTableRepository;
import com.producer.producer_service.persist.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl {

    private final AccountRepository accountRepository;

    private final UserEntityRepository userRepository;

    private final PaymentTableRepository paymentTableRepository;

    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public TransferResponse createTransaction(final UUID userSourceId,final PaymentDetails paymentDetails) {
        final UserEntity userEntitySource = userRepository.findById(userSourceId).orElseThrow(() ->
                new UserNotFoundException("user source with this id, not found"));

        final UserEntity userEntityDestination = userRepository.findById(paymentDetails.getUserDestinationId()).orElseThrow(() ->
                new UserNotFoundException("user dest with this id, not found"));

        final Account accountSource = userEntitySource.getAccounts().stream()
                .filter(a -> a.getId().equals(paymentDetails.getAccountSourceId())).findAny().orElseThrow(
                        () -> new UserNotFoundException("This user does not have such an account")
                );

        final Account accountDestination = userEntityDestination.getAccounts().stream()
                .filter(a -> a.getId().equals(paymentDetails.getAccountDestinationId())).findAny().orElseThrow(
                        () -> new UserNotFoundException("This user does not have such an account")
                );
        final PaymentTable paymentTable = paymentTable(userEntitySource, userEntityDestination, accountSource, accountDestination);

        if (accountSource.getBalance().doubleValue() < paymentDetails.getAmount().doubleValue()) {
            throw new InsufficientFundsOnTheCardException("НЕДОСТАТОЧНО ДЕНЕГ НА СЧЁТЕ");
        }
        paymentTable.setCommission(new BigDecimal("0"));
        if (accountSource.getBikCode().equals(accountDestination.getBikCode())) {
            accountSource.setBalance(accountSource.getBalance().subtract(paymentDetails.getAmount()));
            accountDestination.setBalance(accountDestination.getBalance().add(paymentDetails.getAmount()));
        }

        final BigDecimal amountWithTransferFee = paymentDetails.getAmount()
                .multiply(new BigDecimal(String.valueOf(0.1))).add(paymentDetails.getAmount());
        if (accountSource.getBalance().doubleValue() < amountWithTransferFee.doubleValue()) {
            throw new InsufficientFundsOnTheCardException("НЕДОСТАТОЧНО ДЕНЕГ НА СЧЁТЕ");
        }
        paymentTable.setCommission(amountWithTransferFee.subtract(paymentDetails.getAmount()));
        accountSource.setBalance(accountSource.getBalance().subtract(amountWithTransferFee));
        accountDestination.setBalance(accountDestination.getBalance().add(paymentDetails.getAmount()));

        paymentTable.setAmount(paymentDetails.getAmount());

        paymentTable.setStatusPayment(StatusPayment.PASSED);

        accountRepository.save(accountSource);
        accountRepository.save(accountDestination);

        paymentTableRepository.save(paymentTable);

        createMessage(paymentTable, paymentDetails);

        return TransferResponse.builder()
                .message("Перевод выполнен")
                .statusPayment(StatusPayment.PASSED)
                .amount(paymentDetails.getAmount())
                .build();
    }

    private PaymentTable paymentTable(final UserEntity userEntitySource, final UserEntity userEntityDestination,
                                     final Account accountSource, final Account accountDestination) {
        PaymentTable paymentTable = new PaymentTable();
        paymentTable.setUserIdSource(userEntitySource.getId());
        paymentTable.setAccountIdSource(accountSource.getId());

        paymentTable.setUserIdDestination(userEntityDestination.getId());
        paymentTable.setAccountIdDestination(accountDestination.getId());

        paymentTable.setBikSource(accountSource.getBikCode().getBikCode());
        paymentTable.setBikDestination(accountDestination.getBikCode().getBikCode());

        return paymentTable;
    }

    private void createMessage(final PaymentTable paymentTable, final PaymentDetails paymentDetails) {
        final TransactionMessage transactionMessage = TransactionMessage.builder()
                .accountIdSource(paymentTable.getAccountIdSource())
                .accountIdDestination(paymentTable.getAccountIdDestination())
                .bikSource(paymentTable.getBikSource())
                .bikDestination(paymentTable.getBikDestination())
                .totalSum(paymentTable.getAmount())
                .okvedCode(paymentDetails.getOkvedCode())
                .userIdSource(paymentTable.getUserIdSource())
                .userIdDestination(paymentDetails.getUserDestinationId())
                .createDate(LocalDate.now())
                .build();
        try {
            kafkaProducerService.sendTransaction(transactionMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}