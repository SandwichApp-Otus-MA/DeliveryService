package com.sandwich.app.service;

import com.sandwich.app.domain.entity.CourierEntity;
import com.sandwich.app.domain.entity.DeliveryEntity;
import com.sandwich.app.domain.repository.CourierRepository;
import com.sandwich.app.mapper.CourierMapper;
import com.sandwich.app.models.model.delivery.courier.CourierDto;
import com.sandwich.app.models.model.enums.CourierStatus;
import com.sandwich.app.models.model.enums.DeliveryStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourierService {

    private final CourierRepository repository;
    private final CourierAppAdapter courierAppAdapter;
    private final CourierMapper mapper;
    private final DeliveryService deliveryService;

    @Transactional
    public UUID create(CourierDto courier) {
        repository.findByPhoneNumber(courier.getPhoneNumber()).ifPresent(existCourier -> {
                if (Objects.equals(existCourier.getId(), courier.getId())) {
                    throw new IllegalArgumentException("Account already exists!");
                }
            }
        );

        var newCourier = mapper.convert(new CourierEntity(), courier);
        newCourier.setStatus(CourierStatus.OFFLINE);
        return repository.save(newCourier).getId();
    }

    @Transactional
    public void searchCourier(DeliveryEntity delivery) {
        repository.findAvailable()
            .stream()
            .filter(c -> courierAppAdapter.sendDeliveryOffer(c, delivery))
            .findFirst()
            .ifPresentOrElse(courier -> {
                    delivery.setCourier(courier);
                    delivery.setStatus(DeliveryStatus.COURIER_TO_RESTAURANT);
                    courier.setStatus(CourierStatus.TAKING_ORDER);
                    deliveryService.change(delivery);
                }, () -> {
                    log.info("Courier for orderId: {} not found", delivery.getOrderId());
                    deliveryService.cancel(delivery);
                    // TODO: в 99.9 % случаев курьер должен быть найден
                    //  иначе отменяем заказ
                }
            );
    }
}
