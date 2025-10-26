package com.urbancollection.ecommerce.infrastructure.notification;

import com.urbancollection.ecommerce.shared.logging.LoggerPort;
import com.urbancollection.ecommerce.shared.notification.NotificationPort;

public class NotificationMockAdapter implements NotificationPort {

    private final LoggerPort logger;

    public NotificationMockAdapter(LoggerPort logger) {
        this.logger = logger;
    }

    @Override
    public void sendInfo(String to, String message) {
        // simulamos envío (correo, push, lo que sea)
        logger.warn("MOCK-NOTIFY to={} msg={}", to, message);
    }
}
