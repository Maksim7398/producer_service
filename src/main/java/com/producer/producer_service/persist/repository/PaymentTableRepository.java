package com.producer.producer_service.persist.repository;

import com.producer.producer_service.persist.entity.PaymentTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PaymentTableRepository extends JpaRepository<PaymentTable, UUID> {
}
