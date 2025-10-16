package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.api.web.dto.ConfirmarPagoRequest;
import com.urbancollection.ecommerce.api.web.dto.DespachoRequest;
import com.urbancollection.ecommerce.api.web.dto.PedidoCreateRequest;
import com.urbancollection.ecommerce.application.service.PedidoService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import com.urbancollection.ecommerce.domain.entity.ventas.ItemPedido;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.domain.repository.PedidoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Controlador REST para el flujo de pedidos.

@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Flujo de pedidos: crear, pagar, despachar y entregar")
public class PedidoController {

    private final PedidoService pedidoService;          // Servicio con la lógica de negocio de pedidos.
    private final PedidoRepository pedidoRepository;    // Repositorio para lecturas rápidas.

    public PedidoController(PedidoService pedidoService, PedidoRepository pedidoRepository) {
        this.pedidoService = pedidoService;
        this.pedidoRepository = pedidoRepository;
    }

    
     //POST /api/pedidos
     // Crea un nuevo pedido a partir de un request JSON.
   
    @Operation(summary = "Crear pedido")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> crear(@Valid @RequestBody PedidoCreateRequest req) {
        // Mapeo manual de los items del request a la entidad ItemPedido (solo seteamos id del producto y cantidad).
        List<ItemPedido> items = req.getItems().stream().map(i -> {
            ItemPedido it = new ItemPedido();
            Producto p = new Producto();
            p.setId(i.getProductoId());   // Solo necesitamos el id; el servicio buscará el producto persistido.
            it.setProducto(p);
            it.setCantidad(i.getCantidad());
            return it;
        }).collect(Collectors.toList());

        // Ejecución del caso de uso en la capa de aplicación.
        OperationResult r = pedidoService.crearPedido(
                req.getUsuarioId(),
                req.getDireccionId(),
                items,
                req.getCuponId()
        );

        // Construir body estandar segun exito/fracaso.
        Map<String, Object> body = new LinkedHashMap<>();
        if (r.isSuccess()) {
            body.put("message", r.getMessage());
            return ResponseEntity.ok(body);
        } else {
            body.put("error", r.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }
    }

    /**
     * POST /api/pedidos/{id}/pago
     * Confirma el pago de un pedido (estado pasa a PAGADO si todo está bien).
     *
     * Entrada:
     * - Path variable: id del pedido.
     * - Body: ConfirmarPagoRequest con método de pago y monto.
     *
     * Salida:
     * - 200 OK con "message" si se confirmo.
     * - 400 Bad Request con "error" si falló, monto incorrecto, estado inválido, etc.
     */
    @Operation(summary = "Confirmar pago de un pedido")
    @PostMapping(path = "/{id}/pago", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> confirmarPago(@PathVariable("id") Long id,
                                           @Valid @RequestBody ConfirmarPagoRequest req) {
        OperationResult r = pedidoService.confirmarPago(id, req.getMetodo(), req.getMonto());
        Map<String, Object> body = new LinkedHashMap<>();
        if (r.isSuccess()) {
            body.put("message", r.getMessage());
            return ResponseEntity.ok(body);
        } else {
            body.put("error", r.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }
    }

    /**
     * POST /api/pedidos/{id}/despacho
     * Registra el envío (tracking) y cambia el estado del pedido a ENVIADO.
     *
     * Entrada:
     * - Path variable: id del pedido.
     * - Body: DespachoRequest con el código de tracking.
     */
    @Operation(summary = "Despachar pedido (crea envío con tracking)")
    @PostMapping(path = "/{id}/despacho", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> despachar(@PathVariable("id") Long id,
                                       @Valid @RequestBody DespachoRequest req) {
        OperationResult r = pedidoService.despacharPedido(id, req.getTracking());
        Map<String, Object> body = new LinkedHashMap<>();
        if (r.isSuccess()) {
            body.put("message", r.getMessage());
            return ResponseEntity.ok(body);
        } else {
            body.put("error", r.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }
    }

    /**
     * POST /api/pedidos/{id}/entrega
     * Marca el pedido como ENTREGADO (si actualmente está ENVIADO).
     */
    @Operation(summary = "Marcar pedido como ENTREGADO")
    @PostMapping(path = "/{id}/entrega", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> entregar(@PathVariable("id") Long id) {
        OperationResult r = pedidoService.marcarEntregado(id);
        Map<String, Object> body = new LinkedHashMap<>();
        if (r.isSuccess()) {
            body.put("message", r.getMessage());
            return ResponseEntity.ok(body);
        } else {
            body.put("error", r.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }
    }

    /**
     * GET /api/pedidos/{id}
     * Vista rápida de un pedido (sin anidar todo el grafo de objetos).
     *
     * Respuesta:
     * - 200 OK con un JSON simple (id, usuarioId, direccionId, estado, total).
     * - 404 Not Found si no existe el pedido.
     */
    @Operation(summary = "Obtener un pedido por id (vista rápida)")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> ver(@PathVariable("id") Long id) {
        Pedido p = pedidoRepository.findById(id);
        if (p == null) return ResponseEntity.notFound().build();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("id", p.getId());
        out.put("usuarioId", p.getUsuario() != null ? p.getUsuario().getId() : null);
        out.put("direccionId", p.getDireccionEntrega() != null ? p.getDireccionEntrega().getId() : null);
        out.put("estado", p.getEstado() != null ? p.getEstado().name() : null);
        out.put("total", p.getTotal());

        return ResponseEntity.ok(out);
    }

    /**
     * GET /api/pedidos
     * Lista pedidos en formato resumido. Permite limitar la cantidad con ?limit=N.
     *
     * Detalles:
     * - Carga todos desde el repositorio (findAll).
     * - Si llega "limit" y es > 0 y menor al tamaño, recorta la lista.
     * - Mapea cada pedido a un mapa {id, estado, total} para respuesta ligera.
     */
    @Operation(summary = "Listar pedidos (vista rapida)")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listar(@ParameterObject @RequestParam(required = false) Integer limit) {
        List<Pedido> all = pedidoRepository.findAll();
        if (limit != null && limit > 0 && limit < all.size()) {
            all = all.subList(0, limit);
        }
        List<Map<String, Object>> out = all.stream().map(p -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", p.getId());
            m.put("estado", p.getEstado() != null ? p.getEstado().name() : null);
            m.put("total", p.getTotal());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }
}
