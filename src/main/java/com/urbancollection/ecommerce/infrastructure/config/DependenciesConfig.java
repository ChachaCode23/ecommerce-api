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

/**
 * DependenciesConfig
 *
 * Esta clase es una configuración de Spring donde defino beans manualmente.
 * Aquí estoy diciendo básicamente “cómo se crean” ciertos servicios y puertos,
 * y con qué dependencias.
 *
 * La idea es inyectar las dependencias correctas en cada servicio de aplicación
 * (ProductoService, PedidoService) sin tener que usar @Service directamente ahí.
 * También creo implementaciones simples (mock) para cosas externas como notificaciones o tareas.
 */
@Configuration
public class DependenciesConfig {

    // ========== TaskListPort ==========
    /**
     * TaskListPort:
     * Esta interfaz representa una cola de tareas en background.
     * Aquí le doy una implementación simple (mock) que en vez de mandar la tarea
     * a una cola real, solamente la escribe en el log.
     *
     * logger.warn(...) solo para ver que se pidió ejecutar una tarea.
     */
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
    /**
     * NotificationPort:
     * Puerto para enviar notificaciones (por ejemplo email, push, etc.).
     *
     * Aquí uso NotificationMockAdapter, que es una versión de prueba.
     * En vez de mandar correos reales, simplemente loguea.
     *
     * Esto es útil en desarrollo y pruebas, para no depender de servicios externos.
     */
    @Bean
    public NotificationPort notificationPort(LoggerPort logger) {
        return new NotificationMockAdapter(logger);
    }

    // ========== ProductoService ==========
    /**
     * Defino el bean ProductoService a mano.
     *
     * Le inyecto:
     * - ProductoRepository → acceso a la tabla de productos.
     * - LoggerPort         → log interno del dominio (no usar directamente slf4j).
     * - TaskListPort       → para registrar tareas en background si hace falta.
     *
     * Al devolver el new ProductoService(...), Spring ya sabe cómo inyectar
     * este servicio en los controladores.
     */
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
    /**
     * Defino el bean PedidoService.
     *
     * PedidoService necesita varias cosas del dominio:
     * - UsuarioRepository: para validar el usuario dueño del pedido.
     * - DireccionRepository: para confirmar la dirección de envío válida.
     * - ProductoRepository: para validar stock, etc.
     * - PedidoRepository / ItemPedidoRepository: para guardar el pedido y sus items.
     * - CuponRepository: para aplicar cupones de descuento.
     * - TransaccionPagoRepository: para registrar pagos.
     * - EnvioRepository: para guardar info del envío/tracking.
     * - NotificationPort: para notificar (por ejemplo, "tu pedido fue despachado").
     *
     * Todo eso se lo paso al constructor de PedidoService y Spring lo registra como bean.
     */
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
