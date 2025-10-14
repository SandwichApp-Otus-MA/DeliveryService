package com.sandwich.app.controller;

import com.sandwich.app.models.model.delivery.courier.CourierDto;
import com.sandwich.app.service.CourierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/v1/courier")
@RequiredArgsConstructor
public class CourierController {

    private final CourierService service;

    @PostMapping("/create")
    public ResponseEntity<UUID> create(@Valid @RequestBody CourierDto courier) {
        return ResponseEntity.ok(service.create(courier));
    }
}
