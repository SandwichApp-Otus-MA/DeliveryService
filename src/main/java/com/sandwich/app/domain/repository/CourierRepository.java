package com.sandwich.app.domain.repository;

import com.sandwich.app.domain.entity.CourierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourierRepository extends JpaRepository<CourierEntity, UUID>,
    JpaSpecificationExecutor<CourierEntity>,
    QuerydslPredicateExecutor<CourierEntity> {

    @Query(value = "select c.* from couriers c where c.status = 'ONLINE' limit 10", nativeQuery = true)
    List<CourierEntity> findAvailable();

    Optional<CourierEntity> findByPhoneNumber(String phoneNumber);
}