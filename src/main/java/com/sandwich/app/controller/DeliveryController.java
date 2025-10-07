package com.sandwich.app.controller;

import com.sandwich.app.models.model.delivery.DeliveryDto;
import com.sandwich.app.models.model.delivery.DeliveryFilter;
import com.sandwich.app.models.model.enums.DeliveryStatus;
import com.sandwich.app.models.pagination.PageData;
import com.sandwich.app.models.pagination.PaginationRequest;
import com.sandwich.app.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService service;

    @GetMapping("/search")
    public ResponseEntity<PageData<DeliveryDto>> search(@Valid @RequestBody PaginationRequest<DeliveryFilter> request) {
        return ResponseEntity.ok(service.search(request));
    }

    @PostMapping("/create")
    public ResponseEntity<UUID> create(@Valid @RequestBody DeliveryDto delivery) {
        return ResponseEntity.ok(service.create(delivery));
    }

    @PostMapping("/change-status/{id}")
    public ResponseEntity<Void> create(@PathVariable UUID id, @RequestParam DeliveryStatus status) {
        service.changeStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel/{deliveryId}/{orderId}")
    public ResponseEntity<Void> cancel(@PathVariable UUID deliveryId, @PathVariable UUID orderId) {
        service.cancel(deliveryId, orderId);
        return ResponseEntity.ok().build();
    }
}
