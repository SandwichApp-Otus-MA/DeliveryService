package com.sandwich.app.schedule;

import com.sandwich.app.service.CourierService;
import com.sandwich.app.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryScheduler {

    private final DeliveryService deliveryService;
    private final CourierService courierService;

    @Scheduled(initialDelay = 2000, fixedRate = 3000)
    public void processing() {
        deliveryService.startSearchCourier().forEach(courierService::searchCourier);
    }
}
