package com.producer.producer_service.kafka;

import com.producer.producer_service.controller.request.PaymentDetails;
import com.producer.producer_service.model.TransactionMessage;
import com.producer.producer_service.persist.entity.Account;
import com.producer.producer_service.persist.entity.UserEntity;
import com.producer.producer_service.persist.repository.UserEntityRepository;
import com.producer.producer_service.service.transaction.TransactionServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;


@Testcontainers
@SpringBootTest
@ActiveProfiles("kafka")
@Sql("classpath:db/init.sql")
public class KafkaProducerTest {

    @Autowired
    protected TransactionServiceImpl transactionService;

    @Autowired
    protected KafkaConsumerHelper kafkaConsumerHelper;

    @Autowired
    protected UserEntityRepository userEntityRepository;

    @Container
    protected static KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:6.1.1")
    ).withReuse(true)
            .withEmbeddedZookeeper();

    @DynamicPropertySource
    public static void registryProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrapAddress", kafkaContainer::getBootstrapServers);
    }

    @BeforeEach
    public void before() {
        kafkaContainer.addEnv("KAFKA_CREATE_TOPICS", "transaction_topic");
    }

    @AfterEach
    public void after() {
        kafkaContainer.stop();
    }

    @Test
    public void kafkaProducerTest() {
        UserEntity userFrom = userEntityRepository.findAllByFetch().get(0);
        UserEntity userTo = userEntityRepository.findAllByFetch().get(1);

        Account accountFrom = userFrom.getAccounts().get(0);
        Account accountTo = userTo.getAccounts().get(0);

        PaymentDetails paymentDetails = PaymentDetails.builder()
                .accountDestinationId(accountTo.getId())
                .accountSourceId(accountFrom.getId())
                .okvedCode("10.1")
                .userDestinationId(userTo.getId())
                .amount(new BigDecimal("100")).build();

        transactionService.createTransaction(userFrom.getId(), paymentDetails);

        await()
                .atMost(10, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    TransactionMessage actual = kafkaConsumerHelper.messageConcurrentHashMap.get("transaction_topic");
                    Assertions.assertEquals(paymentDetails.getAmount().intValue(),actual.getTotalSum().intValue());
                    Assertions.assertEquals(paymentDetails.getUserDestinationId(),actual.getUserIdDestination());
                    Assertions.assertEquals(paymentDetails.getAccountDestinationId(),actual.getAccountIdDestination());
                    Assertions.assertEquals(paymentDetails.getAccountSourceId(),actual.getAccountIdSource());
                });
    }
}
