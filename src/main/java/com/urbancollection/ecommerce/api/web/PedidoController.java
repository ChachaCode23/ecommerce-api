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

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // =========================
    // GET /api/pedidos
    // =========================
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
    // body:
    // {
    //   "usuarioId": 1,
    //   "direccionId": 1,
    //   "items": [
    //     { "productoId": 1, "cantidad": 2 }
    //   ],
    //   "cuponId": 1
    // }
    // =========================
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearPedidoRequest body) {
        try {
            if (body == null) {
                return badRequest("Body requerido");
            }

            // mapear request.items -> List<ItemPedido> de dominio
            List<ItemPedido> items = new ArrayList<>();
            if (body.items != null) {
                for (CrearPedidoItemRequest it : body.items) {
                    ItemPedido ip = new ItemPedido();

                    // producto con sólo el id
                    Producto prod = new Producto();
                    prod.setId(it.productoId);

                    ip.setProducto(prod);
                    ip.setCantidad(it.cantidad);

                    items.add(ip);
                }
            }

            OperationResult r = pedidoService.crearPedido(
                    body.usuarioId,
                    body.direccionId,
                    items,
                    body.cuponId
            );

            if (!r.isSuccess()) {
                return badRequest(r.getMessage(), null);
            }

            return ResponseEntity.ok(message("Pedido creado correctamente"));
        } catch (Exception e) {
            logger.error("Error creando pedido", e);
            return serverError("No se pudo crear el pedido");
        }
    }

    // =========================
    // POST /api/pedidos/{id}/pago
    // body:
    // {
    //   "metodo": "TARJETA",
    //   "monto": 107.98
    // }
    // =========================
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
    // body:
    // {
    //   "tracking": "TRACK-XYZ-123"
    // }
    // =========================
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
    // body vacío
    // =========================
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
    // DTOs
    // =========================

    public static class PedidoResumenDto {
        public Long id;
        public Long usuarioId;
        public Long direccionId;
        public String estado;
        public BigDecimal total;
    }

    public static class CrearPedidoRequest {
        public Long usuarioId;
        public Long direccionId;
        public List<CrearPedidoItemRequest> items;
        public Long cuponId; // opcional
    }

    public static class CrearPedidoItemRequest {
        public Long productoId;
        public int cantidad;
    }

    public static class ConfirmarPagoRequest {
        public MetodoDePago metodo;
        public BigDecimal monto;
    }

    public static class DespachoRequest {
        public String tracking;
    }

    public static class MessageResponse {
        public String message;
    }

    public static class ErrorResponse {
        public String error;
        public Object fields;
    }

    // =========================
    // helpers privados
    // =========================

    private PedidoResumenDto mapToResumen(Pedido p) {
        PedidoResumenDto dto = new PedidoResumenDto();
        dto.id = p.getId();
        dto.estado = (p.getEstado() != null) ? p.getEstado().name() : null;
        dto.total = p.getTotal();

        dto.usuarioId = (p.getUsuario() != null) ? p.getUsuario().getId() : null;
        dto.direccionId = (p.getDireccionEntrega() != null) ? p.getDireccionEntrega().getId() : null;

        return dto;
    }

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

    private MessageResponse message(String m) {
        MessageResponse r = new MessageResponse();
        r.message = m;
        return r;
    }
}
