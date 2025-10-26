package com.urbancollection.ecommerce.infrastructure.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.urbancollection.ecommerce.shared.logging.LoggerPort;

/**
 * Adaptador SLF4J que cumple el puerto LoggerPort.
 * Esta clase vive en el módulo API pero la interfaz LoggerPort vive en DOMAIN.
 */
@Component
public class Slf4jLoggerAdapter implements LoggerPort {

    private final Logger log = LoggerFactory.getLogger("APP");

    @Override
    public void info(String message, Object... args) {
        log.info(message, args);
    }

    @Override
    public void warn(String message, Object... args) {
        log.warn(message, args);
    }

    @Override
    public void error(String message, Object... args) {
        log.error(message, args);
    }

    @Override
    public void error(Throwable t, String message, Object... args) {
        // log.error(String msg, Throwable t) no soporta varargs directo,
        // así que formateamos nosotros
        if (args != null && args.length > 0) {
            log.error(String.format(message.replace("{}", "%s"), args), t);
        } else {
            log.error(message, t);
        }
    }

    // helpers opcionales SIN @Override
    public void debug(String message, Object... args) {
        log.debug(message, args);
    }

    public void trace(String message, Object... args) {
        log.trace(message, args);
    }
}
