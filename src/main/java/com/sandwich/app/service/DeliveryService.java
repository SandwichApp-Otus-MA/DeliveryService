package com.sandwich.app.service;

import static com.sandwich.app.models.utils.PageUtil.createPage;
import static com.sandwich.app.models.utils.PageUtil.createPageable;
import static com.sandwich.app.models.utils.PageUtil.createSort;

import com.sandwich.app.domain.entity.DeliveryEntity;
import com.sandwich.app.domain.repository.DeliveryRepository;
import com.sandwich.app.kafka.OrderEventProducer;
import com.sandwich.app.mapper.DeliveryMapper;
import com.sandwich.app.models.model.delivery.DeliveryDto;
import com.sandwich.app.models.model.delivery.DeliveryFilter;
import com.sandwich.app.models.model.enums.CourierStatus;
import com.sandwich.app.models.model.enums.DeliveryStatus;
import com.sandwich.app.models.model.enums.OrderStatus;
import com.sandwich.app.models.pagination.PageData;
import com.sandwich.app.models.pagination.PaginationRequest;
import com.sandwich.app.query.builder.DeliveryQueryBuilder;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository repository;
    private final DeliveryMapper mapper;
    private final DeliveryQueryBuilder queryBuilder;
    private final TransactionTemplate transactionTemplate;
    private final OrderEventProducer orderEventProducer;

    @Transactional(readOnly = true)
    public PageData<DeliveryDto> search(PaginationRequest<DeliveryFilter> request) {
        var predicate = queryBuilder.createPredicate(request.getFilter());
        var sort = createSort(request.getSorting());
        var pageable = createPageable(request.getPagination(), sort);
        var all = repository.findAll(predicate, pageable);

        return createPage(
            all.getTotalPages(),
            all.getTotalElements(),
            all.getContent().stream()
                .map(mapper::convert)
                .collect(Collectors.toList()),
            request.getPagination());
    }

    @Transactional
    public UUID create(DeliveryDto delivery) {
        repository.findByOrderId(delivery.getOrderId()).ifPresent(existDelivery -> {
                throw new IllegalArgumentException("Delivery with orderId: %s already exists! id: %s"
                    .formatted(delivery.getOrderId(), existDelivery.getId()));
            }
        );

        var newDelivery = mapper.convert(new DeliveryEntity(), delivery);
        newDelivery.setStatus(DeliveryStatus.CREATED);
        return repository.save(newDelivery).getId();
    }

    public void changeStatus(UUID deliveryId, DeliveryStatus status) {
        var changedDelivery = transactionTemplate.execute(ts -> {
            var delivery = repository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery with id: %s not found!".formatted(deliveryId)));

            if (status == DeliveryStatus.COMPLETED || status == DeliveryStatus.CANCELED) {
                delivery.getCourier().setStatus(CourierStatus.ONLINE);
            }

            return delivery.setStatus(status);
        });

        assert changedDelivery != null;

        Optional.ofNullable(getOrderStatusOrNull(status)).ifPresent(orderStatus ->
            orderEventProducer.send(changedDelivery.getOrderId(), orderStatus));
    }

    public void change(DeliveryEntity delivery) {
        repository.save(delivery);
        Optional.ofNullable(getOrderStatusOrNull(delivery.getStatus())).ifPresent(orderStatus ->
            orderEventProducer.send(delivery.getOrderId(), orderStatus));
    }

    @Transactional
    public List<DeliveryEntity> startSearchCourier() {
        var sorting = Sort.by(Sort.Order.desc("createdAt"));
        return repository.findAllByStatus(DeliveryStatus.CREATED, PageRequest.of(0, 50, sorting)).stream()
            .map(d -> d.setStatus(DeliveryStatus.SEARCH_COURIER))
            .toList();
    }

    public void cancel(UUID deliveryId, UUID orderId) {
        transactionTemplate.executeWithoutResult(status ->
            repository.findByOrderId(orderId).ifPresentOrElse(existDelivery -> {
                    if (Objects.equals(deliveryId, existDelivery.getId())) {
                        existDelivery.setStatus(DeliveryStatus.CANCELED);
                    } else {
                        throw new IllegalArgumentException("Неверно указан deliveryId!");
                    }
                }, () -> {
                    throw new IllegalArgumentException("Не найдена запись о доставке для orderId: %s и deliveryId: %s".formatted(orderId, deliveryId));
                }
            ));

        orderEventProducer.send(orderId, OrderStatus.DELIVERY_FAILED);
    }

    public void cancel(DeliveryEntity delivery) {
        delivery.setStatus(DeliveryStatus.CANCELED);
        repository.save(delivery);
        orderEventProducer.send(delivery.getOrderId(), OrderStatus.DELIVERY_FAILED);
    }

    private OrderStatus getOrderStatusOrNull(DeliveryStatus status) {
        return switch (status) {
            case COURIER_TO_RESTAURANT -> OrderStatus.DELIVERY_ASSIGNED;
            case COMPLETED -> OrderStatus.DELIVERY_COMPLETED;
            case CANCELED, FAILED -> OrderStatus.DELIVERY_FAILED;
            default -> null;
        };
    }
}
