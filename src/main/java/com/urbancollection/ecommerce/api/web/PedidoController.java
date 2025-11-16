package com.urbancollection.ecommerce.api.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.urbancollection.ecommerce.api.web.dto.PedidoCreateRequest;
import com.urbancollection.ecommerce.api.web.dto.PedidoMapper;
import com.urbancollection.ecommerce.api.web.dto.PedidoResponse;
import com.urbancollection.ecommerce.application.service.IPedidoService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import com.urbancollection.ecommerce.domain.entity.ventas.ItemPedido;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final IPedidoService pedidoService;

    public PedidoController(IPedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    // =========================
    // POST /api/pedidos
    // =========================
    @PostMapping
    @Transactional
    public ResponseEntity<?> crearPedido(@RequestBody @Valid PedidoCreateRequest request) {

        // 1) Mapear items del request a ItemPedido
        List<ItemPedido> items = new ArrayList<>();

        if (request.getItems() != null) {
            for (PedidoCreateRequest.ItemPedidoRequest itReq : request.getItems()) {
                if (itReq == null) continue;

                ItemPedido item = new ItemPedido();
                Producto p = new Producto();
                p.setId(itReq.getProductoId());
                item.setProducto(p);
                item.setCantidad(itReq.getCantidad());

                items.add(item);
            }
        }

        // 2) Llamar al servicio
        OperationResult result = pedidoService.crearPedido(
                request.getUsuarioId(),
                request.getDireccionId(),
                items,
                request.getCuponId()
        );

        // 3) Si falló la regla de negocio → 404 simple
        if (!result.isSuccess()) {
            String msg = result.getMessage() != null ? result.getMessage() : "Error al crear el pedido";
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(msg);
        }

        // 4) Tomar el último pedido creado
        List<Pedido> todos = pedidoService.listarTodos();
        if (todos == null || todos.isEmpty()) {
            return ResponseEntity
                    .internalServerError()
                    .body("El pedido se creó, pero no se pudo recuperar");
        }

        Pedido ultimo = todos.get(todos.size() - 1);
        PedidoResponse response = PedidoMapper.toResponse(ultimo);

        return ResponseEntity.ok(response);
    }

    // =========================
    // GET /api/pedidos
    // =========================
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<PedidoResponse>> listar() {
        List<Pedido> pedidos = pedidoService.listarTodos();

        List<PedidoResponse> responses = pedidos.stream()
                .map(PedidoMapper::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    // =========================
    // GET /api/pedidos/{id}
    // =========================
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {

        Pedido pedido = pedidoService.obtenerPorId(id);

        if (pedido == null) {
            Map<String, Object> body = Map.of(
                    "error", "Recurso no encontrado",
                    "details", List.of("No existe un pedido con id " + id)
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }

        PedidoResponse response = PedidoMapper.toResponse(pedido);
        return ResponseEntity.ok(response);
    }

    // =========================
    // GET /api/pedidos/test/error500
    // (solo para la Prueba 15 - Error 500)
    // =========================
    @GetMapping("/test/error500")
    public void forzarError500() {
        // Fuerza un RuntimeException para que lo capture ApiExceptionHandler
        throw new RuntimeException("Error interno de prueba");
    }
}
