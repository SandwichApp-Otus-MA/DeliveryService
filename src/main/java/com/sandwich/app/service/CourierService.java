package com.sandwich.app.service;

import com.sandwich.app.domain.entity.CourierEntity;
import com.sandwich.app.domain.entity.DeliveryEntity;
import com.sandwich.app.domain.repository.CourierRepository;
import com.sandwich.app.mapper.CourierMapper;
import com.sandwich.app.models.model.delivery.DeliveryDto;
import com.sandwich.app.models.model.delivery.DeliveryFilter;
import com.sandwich.app.models.model.delivery.courier.CourierDto;
import com.sandwich.app.models.model.enums.AdvancedFieldFilterType;
import com.sandwich.app.models.model.enums.CourierStatus;
import com.sandwich.app.models.model.enums.DeliveryStatus;
import com.sandwich.app.models.pagination.AdvancedFieldFilter;
import com.sandwich.app.models.pagination.Pagination;
import com.sandwich.app.models.pagination.PaginationRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    @Transactional
    public void changeStatus(UUID courierId, CourierStatus status) {
        var delivery = getFirstActiveDelivery(courierId);

        if (status == CourierStatus.OFFLINE && !delivery.isEmpty()) {
            throw new IllegalArgumentException("Требуется завершить активную доставку!");
        }

        repository.findById(courierId)
            .orElseThrow(() -> new EntityNotFoundException("Courier  not found!"))
            .setStatus(status);
    }

    private List<DeliveryDto> getFirstActiveDelivery(UUID courierId) {
        var request = new PaginationRequest<DeliveryFilter>()
            .setFilter(new DeliveryFilter()
                .setCourierId(new AdvancedFieldFilter<UUID>()
                    .setType(AdvancedFieldFilterType.EQUALS)
                    .setSingleValue(courierId))
                .setStatus(new AdvancedFieldFilter<DeliveryStatus>()
                    .setType(AdvancedFieldFilterType.IN)
                    .setMultipleValue(List.of(DeliveryStatus.COURIER_TO_RESTAURANT, DeliveryStatus.COURIER_TO_CLIENT))))
            .setPagination(Pagination.of(0, 1));
        return deliveryService.search(request).getContent();
    }
}
