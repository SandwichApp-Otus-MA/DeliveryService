package com.sandwich.app.domain.entity;


import com.sandwich.app.models.model.enums.DeliveryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@FieldNameConstants
@NoArgsConstructor
@Entity
@Table(name = "deliveries")
public class DeliveryEntity extends DomainObject {

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "restaurant_id")
    private UUID restaurantId;

    @Column(name = "address")
    private String address;

    @Column(name = "comment")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeliveryStatus status = DeliveryStatus.CREATED;

    @JoinColumn(name = "courier_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CourierEntity courier;

    @CreationTimestamp
    @Column(name = "created_at")
    private Instant createdAt;

}
