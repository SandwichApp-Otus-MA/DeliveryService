package com.sandwich.app.service;

import com.sandwich.app.domain.entity.CourierEntity;
import com.sandwich.app.domain.entity.DeliveryEntity;
import org.springframework.stereotype.Component;

@Component
public class CourierAppAdapter {

    public boolean sendDeliveryOffer(CourierEntity courier, DeliveryEntity delivery) {
        // TODO: MOCK логики отправки заявки на доставку курьеру с получением от него ответа - взял или нет
        return true;
    }
}
