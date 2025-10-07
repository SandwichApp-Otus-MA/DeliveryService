package com.sandwich.app.domain.repository;

import com.sandwich.app.domain.entity.DeliveryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity, UUID>,
    JpaSpecificationExecutor<DeliveryEntity>,
    QuerydslPredicateExecutor<DeliveryEntity> {

    Optional<DeliveryEntity> findByOrderId(UUID orderId);
}