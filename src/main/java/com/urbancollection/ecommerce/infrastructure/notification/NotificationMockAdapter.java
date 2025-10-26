package com.urbancollection.ecommerce.infrastructure.notification;

import com.urbancollection.ecommerce.shared.logging.LoggerPort;
import com.urbancollection.ecommerce.shared.notification.NotificationPort;

/**
 * NotificationMockAdapter
 *
 * Este adaptador es una implementación "fake" (mock) del puerto NotificationPort.
 * En vez de mandar una notificación real (correo, push, etc.), solo la escribe en el log.
 *
 * Esto sirve en desarrollo y pruebas para no depender de un servicio externo de notificaciones.
 */
public class NotificationMockAdapter implements NotificationPort {

    private final LoggerPort logger;

    public NotificationMockAdapter(LoggerPort logger) {
        this.logger = logger;
    }

    /**
     * sendInfo:
     * Recibe a quién va dirigida la notificación ("to")
     * y el mensaje, y lo imprime en el log con prefijo MOCK-NOTIFY.
     *
     * En producción aquí iría la integración real (email, SMS, push...).
     */
    @Override
    public void sendInfo(String to, String message) {
        // simulamos envío
        logger.warn("MOCK-NOTIFY to={} msg={}", to, message);
    }
}
