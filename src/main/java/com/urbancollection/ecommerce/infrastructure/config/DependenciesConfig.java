package com.urbancollection.ecommerce.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.urbancollection.ecommerce.application.service.PedidoService;
import com.urbancollection.ecommerce.application.service.ProductoService;
import com.urbancollection.ecommerce.domain.repository.CuponRepository;
import com.urbancollection.ecommerce.domain.repository.DireccionRepository;
import com.urbancollection.ecommerce.domain.repository.EnvioRepository;
import com.urbancollection.ecommerce.domain.repository.ItemPedidoRepository;
import com.urbancollection.ecommerce.domain.repository.PedidoRepository;
import com.urbancollection.ecommerce.domain.repository.ProductoRepository;
import com.urbancollection.ecommerce.domain.repository.TransaccionPagoRepository;
import com.urbancollection.ecommerce.domain.repository.UsuarioRepository;
import com.urbancollection.ecommerce.infrastructure.notification.NotificationMockAdapter;
import com.urbancollection.ecommerce.shared.logging.LoggerPort;
import com.urbancollection.ecommerce.shared.notification.NotificationPort;
import com.urbancollection.ecommerce.shared.tasks.TaskListPort;

@Configuration
public class DependenciesConfig {

    // ========== TaskListPort ==========
    @Bean
    public TaskListPort taskListPort(LoggerPort logger) {
        return new TaskListPort() {
            @Override
            public void enqueue(String taskType, String description) {
                // implementación mock: solo loguea
                logger.warn("TASK ENQUEUE [{}] {}", taskType, description);
            }
        };
    }

    // ========== NotificationPort (mock) ==========
    // usa el LoggerPort que Spring ya tiene (Slf4jLoggerAdapter @Component)
    @Bean
    public NotificationPort notificationPort(LoggerPort logger) {
        return new NotificationMockAdapter(logger);
    }

    // ========== ProductoService ==========
    @Bean
    public ProductoService productoService(
            ProductoRepository productoRepository,
            LoggerPort logger,
            TaskListPort taskListPort
    ) {
        return new ProductoService(
                productoRepository,
                logger,
                taskListPort
        );
    }

    // ========== PedidoService ==========
    @Bean
    public PedidoService pedidoService(
            UsuarioRepository usuarioRepository,
            DireccionRepository direccionRepository,
            ProductoRepository productoRepository,
            PedidoRepository pedidoRepository,
            ItemPedidoRepository itemPedidoRepository,
            CuponRepository cuponRepository,
            TransaccionPagoRepository transaccionPagoRepository,
            EnvioRepository envioRepository,
            NotificationPort notificationPort
    ) {
        return new PedidoService(
                usuarioRepository,
                direccionRepository,
                productoRepository,
                pedidoRepository,
                itemPedidoRepository,
                cuponRepository,
                transaccionPagoRepository,
                envioRepository,
                notificationPort
        );
    }
}
