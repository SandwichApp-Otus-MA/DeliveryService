package com.sandwich.app.query.builder;

import com.querydsl.core.BooleanBuilder;
import com.sandwich.app.domain.entity.QDeliveryEntity;
import com.sandwich.app.models.model.delivery.DeliveryFilter;
import com.sandwich.app.models.utils.AdvancedFilterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryQueryBuilder {

    private static final QDeliveryEntity DELIVERY = QDeliveryEntity.deliveryEntity;

    public BooleanBuilder createPredicate(DeliveryFilter filter) {
        BooleanBuilder predicate = new BooleanBuilder();

        if (filter.getId() != null) {
            predicate.and(AdvancedFilterUtil.getExpressionByAdvancedFilter(DELIVERY.id, filter.getId()));
        }

        if (filter.getOrderId() != null) {
            predicate.and(AdvancedFilterUtil.getExpressionByAdvancedFilter(DELIVERY.orderId, filter.getOrderId()));
        }

        if (filter.getRestaurantId() != null) {
            predicate.and(AdvancedFilterUtil.getExpressionByAdvancedFilter(DELIVERY.restaurantId, filter.getRestaurantId()));
        }

        return predicate;
    }
}