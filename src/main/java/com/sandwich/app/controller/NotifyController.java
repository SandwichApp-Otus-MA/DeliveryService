package com.sandwich.app.controller;

import com.sandwich.app.models.model.event.NotificationEvent;
import com.sandwich.app.service.NotifyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/v1")
@RequiredArgsConstructor
public class NotifyController {

    private final NotifyService service;

    @PostMapping("/notify")
    public ResponseEntity<Void> notify(@Valid @RequestBody NotificationEvent event) {
        // todo: обработка событий по готовности заказа
        service.notify(event);
        return ResponseEntity.ok().build();
    }
}
