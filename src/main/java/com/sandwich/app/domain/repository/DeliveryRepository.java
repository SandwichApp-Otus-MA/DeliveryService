package com.sandwich.app.domain.repository;

import com.sandwich.app.domain.entity.DeliveryEntity;
import com.sandwich.app.models.model.enums.DeliveryStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity, UUID>,
    JpaSpecificationExecutor<DeliveryEntity>,
    QuerydslPredicateExecutor<DeliveryEntity> {

    Optional<DeliveryEntity> findByOrderId(UUID orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<DeliveryEntity> findAllByStatus(DeliveryStatus status, Pageable pageable);
}