package com.sandwich.app.service;

import com.sandwich.app.models.model.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyService {

    public void notify(NotificationEvent event) {
        log.info("Получено уведомление: {}", event);
    }
}
