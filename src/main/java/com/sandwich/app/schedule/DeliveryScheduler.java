package com.sandwich.app.schedule;

import com.sandwich.app.service.CourierService;
import com.sandwich.app.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryScheduler {

    private final DeliveryService deliveryService;
    private final CourierService courierService;

    @Scheduled(initialDelay = 2000, fixedRate = 3000)
    public void processing() {
        var deliveries = deliveryService.startSearchCourier();
        log.info("Подготовлено заказов: {} для поиска курьеров", deliveries.size());
        deliveries.forEach(courierService::searchCourier);
    }
}
