package com.producer.producer_service.persist.repository;

import com.producer.producer_service.persist.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {

    @Query("FROM UserEntity u LEFT JOIN FETCH u.accounts")
    List<UserEntity> findAllByFetch();
}
