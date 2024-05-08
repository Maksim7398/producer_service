package com.producer.producer_service.persist.repository;

import com.producer.producer_service.persist.entity.Bank;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

@Registered
public interface BankRepository extends JpaRepository<Bank, UUID> {
}
