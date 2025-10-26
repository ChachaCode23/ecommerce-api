package com.urbancollection.ecommerce.api.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.urbancollection.ecommerce.application.service.PedidoService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import com.urbancollection.ecommerce.domain.entity.ventas.ItemPedido;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.domain.enums.MetodoDePago;

/**
 * Controlador REST para manejar todo lo relacionado con pedidos.
 * Aquí expongo los endpoints para:
 *  listar pedidos
 *  ver un pedido específico
 *  crear un pedido nuevo (carrito -> pedido)
 *  confirmar el pago de un pedido
 *  marcar el pedido como despachado (le doy tracking)
 *  marcar el pedido como entregado/completado
 *
 * este controlador es la capa web (presentación),
 * no tiene la lógica pesada del negocio. Esa lógica vive en PedidoService.
 * Aquí lo que pasa es:
 *   recibir el request
 *   validarlo rapidito
 *   armar los objetos necesarios
 *   llamar al servicio
 *   devolver la respuesta
 */
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    // Logger para registrar errores o info útil en el servidor.
    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);

    // Inyecto mi servicio de negocio para pedidos.
    // Este servicio es el que sabe crear pedidos, confirmar pagos, etc.
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // =========================
    // GET /api/pedidos
    // =========================
    /**
     * Este endpoint lista todos los pedidos.
     *
     * Flujo:
     * 1. Llamo al servicio para traer todos los pedidos.
     * 2. Convierto cada Pedido (entidad del dominio) a un DTO resumido,
     *    porque yo no quiero exponer todo el objeto completo directamente.
     *    (Por ejemplo, así controlo qué datos salen hacia afuera.)
     * 3. Devuelvo la lista en el response.
     *
     * Respuesta: HTTP 200 + lista de PedidoResumenDto.
     */
    @GetMapping
    public ResponseEntity<List<PedidoResumenDto>> listar() {

        List<Pedido> pedidos = pedidoService.listarTodos();

        List<PedidoResumenDto> dtoList = new ArrayList<>();
        for (Pedido p : pedidos) {
            dtoList.add(mapToResumen(p));
        }

        return ResponseEntity.ok(dtoList);
    }

    // =========================
    // GET /api/pedidos/{id}
    // =========================
    /**
     * Este endpoint devuelve un pedido específico por su id.
     *
     * Ejemplo:
     * GET /api/pedidos/5
     *
     * Si existe -> devuelvo 200 con el resumen del pedido.
     * Si no existe -> devuelvo 404 con un body que dice "Pedido no encontrado".
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Pedido p = pedidoService.obtenerPorId(id);
        if (p == null) {
            return notFound("Pedido no encontrado");
        }
        return ResponseEntity.ok(mapToResumen(p));
    }

    // =========================
    // POST /api/pedidos
    //
    // Crea un nuevo pedido (todavia no esta pagado).
    //
    // Body esperado (ejemplo):
    // {
    //   "usuarioId": 1,
    //   "direccionId": 1,
    //   "items": [
    //     { "productoId": 1, "cantidad": 2 }
    //   ],
    //   "cuponId": 1
    // }
    //
    // El cuponId es opcional.
    // =========================
    /**
     * Flujo para crear pedido:
     * 1. Recibo los datos del cliente (usuario, dirección de entrega, lista de productos, cupón).
     * 2. Con esos datos creo la lista de ItemPedido del dominio.
     *    OJO: aqui no cargo el producto entero, solo seteo el id del producto.
     *    De la validación real (si existe el producto, si hay stock, etc.) se encarga el servicio.
     * 3. Llamo pedidoService.crearPedido(...) para que aplique las reglas de negocio.
     * 4. Si todo va bien, respondo 200 con un mensajito.
     *    Si falla alguna validación del negocio, respondo 400 con el mensaje.
     *
     * Nota:
     * Uso try/catch porque si algo explota adentro (runtime, null raro, etc.),
     * devuelve 500 controlado en vez de tumbar la app.
     */
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearPedidoRequest body) {
        try {
            if (body == null) {
                return badRequest("Body requerido");
            }

            // Mapeo los items del request (DTO) a ItemPedido (entidad del dominio).
            List<ItemPedido> items = new ArrayList<>();
            if (body.items != null) {
                for (CrearPedidoItemRequest it : body.items) {
                    ItemPedido ip = new ItemPedido();

                    // Creo un Producto solo con el ID (no cargo todo el objeto aquí).
                    Producto prod = new Producto();
                    prod.setId(it.productoId);

                    ip.setProducto(prod);
                    ip.setCantidad(it.cantidad);

                    items.add(ip);
                }
            }

            // Llamo al servicio de dominio para que cree el pedido.
            // El servicio devuelve un OperationResult con success/fail y mensaje.
            OperationResult r = pedidoService.crearPedido(
                    body.usuarioId,
                    body.direccionId,
                    items,
                    body.cuponId
            );

            if (!r.isSuccess()) {
                // Si el servicio dice que no se pudo (por ejemplo cupón inválido,
                // dirección no pertenece al usuario, etc.), devuelvo 400.
                return badRequest(r.getMessage(), null);
            }

            // Éxito
            return ResponseEntity.ok(message("Pedido creado correctamente"));
        } catch (Exception e) {
            logger.error("Error creando pedido", e);
            return serverError("No se pudo crear el pedido");
        }
    }

    // =========================
    // POST /api/pedidos/{id}/pago
    //
    // Confirma el pago de un pedido.
    // Esto normalmente:
    // - marca el pedido como PAGADO
    // - registra la transacción
    // - descuenta el stock real del inventario
    //
    // Body ejemplo:
    // {
    //   "metodo": "TARJETA",
    //   "monto": 107.98
    // }
    // =========================
    /**
     * Flujo para confirmar pago:
     * 1. Valido que me mandaron método de pago y monto.
     * 2. Llamo al servicio pedidoService.confirmarPago(id, metodo, monto).
     *    Ese servicio debería encargarse de:
     *      - validar que el pedido existe
     *      - validar que todavía no estaba pagado
     *      - registrar el pago
     *      - actualizar el estado del pedido
     *      - actualizar el inventario
     * 3. Si todo ok -> devuelvo 200 con mensaje.
     *    Si hay error de negocio -> devuelvo 400 (por ejemplo ya estaba pagado).
     *    Si explota algo inesperado -> devuelvo 500.
     */
    @PostMapping("/{id}/pago")
    public ResponseEntity<?> confirmarPago(
            @PathVariable Long id,
            @RequestBody ConfirmarPagoRequest body
    ) {
        try {
            if (body == null) {
                return badRequest("Body requerido");
            }
            if (body.metodo == null) {
                return badRequest("Metodo de pago requerido");
            }
            if (body.monto == null) {
                return badRequest("Monto requerido");
            }

            OperationResult r = pedidoService.confirmarPago(
                    id,
                    body.metodo,
                    body.monto
            );

            if (!r.isSuccess()) {
                return badRequest(r.getMessage(), null);
            }

            return ResponseEntity.ok(message("Pago confirmado y stock actualizado"));
        } catch (Exception e) {
            logger.error("Error confirmando pago", e);
            return serverError("No se pudo confirmar el pago");
        }
    }

    // =========================
    // POST /api/pedidos/{id}/despacho
    //
    // Marca el pedido como DESPACHADO / ENVIADO.
    // Aquí normalmente se genera el tracking del envío.
    //
    // Body ejemplo:
    // {
    //   "tracking": "TRACK-XYZ-123"
    // }
    // =========================
    /**
     * Este endpoint se usa cuando ya el pedido salió para el cliente.
     *
     * 1. Valido que me mandaron el tracking (código del envío).
     * 2. Llamo pedidoService.despacharPedido(id, tracking).
     *    Ahí el servicio debería:
     *      - guardar info de envío
     *      - cambiar el estado del pedido (por ej. A_DESPACHO -> DESPACHADO)
     * 3. Devuelvo 200 con mensaje si salió bien.
     *    Devuelvo 400 si hay alguna violación de regla (por ejemplo
     *    intentar despachar un pedido que ni siquiera está pagado).
     */
    @PostMapping("/{id}/despacho")
    public ResponseEntity<?> despachar(
            @PathVariable Long id,
            @RequestBody DespachoRequest body
    ) {
        try {
            if (body == null || body.tracking == null || body.tracking.isBlank()) {
                return badRequest("Tracking requerido");
            }

            OperationResult r = pedidoService.despacharPedido(id, body.tracking);

            if (!r.isSuccess()) {
                return badRequest(r.getMessage(), null);
            }

            return ResponseEntity.ok(message("Pedido despachado"));
        } catch (Exception e) {
            logger.error("Error despachando pedido", e);
            return serverError("No se pudo despachar el pedido");
        }
    }

    // =========================
    // POST /api/pedidos/{id}/entrega
    //
    // Marca el pedido como ENTREGADO/COMPLETADO.
    // Body vacío.
    // =========================
    /**
     * Este endpoint se usa cuando el pedido ya le llegó al cliente.
     * Cambiamos el estado a COMPLETADO / ENTREGADO.
     *
     * 1. Llamo pedidoService.marcarEntregado(id)
     * 2. Ese servicio valida que el pedido exista y que ya fue despachado.
     * 3. Devuelvo 200 con mensaje si todo bien.
     *    Si hay una regla rota (ej. intentar marcar entregado algo que no se despachó),
     *    devuelvo 400.
     */
    @PostMapping("/{id}/entrega")
    public ResponseEntity<?> marcarEntregado(@PathVariable Long id) {
        try {
            OperationResult r = pedidoService.marcarEntregado(id);

            if (!r.isSuccess()) {
                return badRequest(r.getMessage(), null);
            }

            return ResponseEntity.ok(message("Pedido marcado como COMPLETADO"));
        } catch (Exception e) {
            logger.error("Error marcando entrega", e);
            return serverError("No se pudo marcar como COMPLETADO");
        }
    }

    // =========================
    // DTOs (clases internas para request/response)
    // =========================

    /**
     * PedidoResumenDto:
     * Esto es lo que devuelvo al cliente cuando consultan pedidos.
     * No devuelvo el objeto Pedido entero directo, porque:
     *  - controlo qué campos expongo
     *  - evito exponer estructuras internas del dominio
     */
    public static class PedidoResumenDto {
        public Long id;
        public Long usuarioId;
        public Long direccionId;
        public String estado;
        public BigDecimal total;
    }

    /**
     * CrearPedidoRequest:
     * Body que espero cuando el cliente quiere crear un pedido nuevo.
     */
    public static class CrearPedidoRequest {
        public Long usuarioId;
        public Long direccionId;
        public List<CrearPedidoItemRequest> items;
        public Long cuponId; // opcional, puede venir null
    }

    /**
     * CrearPedidoItemRequest:
     * Representa 1 producto que el usuario quiere comprar,
     * con la cantidad que pidió.
     */
    public static class CrearPedidoItemRequest {
        public Long productoId;
        public int cantidad;
    }

    /**
     * ConfirmarPagoRequest:
     * Body para confirmar el pago de un pedido existente.
     *
     * metodo -> tipo de pago (TARJETA, TRANSFERENCIA, etc.)
     * monto  -> cuánto se pagó
     */
    public static class ConfirmarPagoRequest {
        public MetodoDePago metodo;
        public BigDecimal monto;
    }

    /**
     * DespachoRequest:
     * Body usado al despachar un pedido.
     * tracking = código que se le da al cliente para seguimiento.
     */
    public static class DespachoRequest {
        public String tracking;
    }

    /**
     * MessageResponse:
     * Respuesta básica de éxito, solo con un mensaje.
     */
    public static class MessageResponse {
        public String message;
    }

    /**
     * ErrorResponse:
     * Respuesta estándar de error.
     * "error"  = mensaje general
     * "fields" = detalles específicos si aplica (por ejemplo errores de validación)
     */
    public static class ErrorResponse {
        public String error;
        public Object fields;
    }

    // =========================
    // Helpers privados
    // =========================

    /**
     * mapToResumen:
     * Convierto un objeto Pedido del dominio a PedidoResumenDto
     * para no mandar toda la entidad entera al cliente.
     */
    private PedidoResumenDto mapToResumen(Pedido p) {
        PedidoResumenDto dto = new PedidoResumenDto();
        dto.id = p.getId();
        dto.estado = (p.getEstado() != null) ? p.getEstado().name() : null;
        dto.total = p.getTotal();

        dto.usuarioId = (p.getUsuario() != null) ? p.getUsuario().getId() : null;
        dto.direccionId = (p.getDireccionEntrega() != null) ? p.getDireccionEntrega().getId() : null;

        return dto;
    }

    /**
     * Helpers para respuestas de error 400, 404, 500.
     * Estos métodos me ayudan a no repetir el mismo bloque
     * en todos los endpoints.
     */

    private ResponseEntity<?> badRequest(String msg) {
        return badRequest(msg, null);
    }

    private ResponseEntity<?> badRequest(String msg, Object fields) {
        ErrorResponse err = new ErrorResponse();
        err.error = (msg != null && !msg.isBlank()) ? msg : "Datos inválidos";
        err.fields = fields;
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }

    private ResponseEntity<?> notFound(String msg) {
        ErrorResponse err = new ErrorResponse();
        err.error = (msg != null && !msg.isBlank()) ? msg : "No encontrado";
        err.fields = null;
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
    }

    private ResponseEntity<?> serverError(String msg) {
        ErrorResponse err = new ErrorResponse();
        err.error = (msg != null && !msg.isBlank()) ? msg : "Internal Server Error";
        err.fields = null;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }

    /**
     * Para respuestas de éxito simples con un mensaje.
     */
    private MessageResponse message(String m) {
        MessageResponse r = new MessageResponse();
        r.message = m;
        return r;
    }
}
