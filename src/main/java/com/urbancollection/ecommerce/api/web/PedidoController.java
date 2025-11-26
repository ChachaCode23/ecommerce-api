package com.urbancollection.ecommerce.api.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urbancollection.ecommerce.api.web.dto.PedidoCreateRequest;
import com.urbancollection.ecommerce.api.web.dto.PedidoMapper;
import com.urbancollection.ecommerce.api.web.dto.PedidoResponse;
import com.urbancollection.ecommerce.application.service.IPedidoService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import com.urbancollection.ecommerce.domain.entity.ventas.ItemPedido;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.persistence.jpa.spring.PedidoJpaRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final IPedidoService pedidoService;
    private final PedidoJpaRepository pedidoRepository;

    public PedidoController(IPedidoService pedidoService, PedidoJpaRepository pedidoRepository) {
        this.pedidoService = pedidoService;
        this.pedidoRepository = pedidoRepository;
    }

    // =========================
    // POST /api/pedidos
    // =========================
    @PostMapping
    @Transactional
    public ResponseEntity<?> crearPedido(@RequestBody @Valid PedidoCreateRequest request) {

        // Mapear items del request a ItemPedido
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

        //  Llamar al servicio
        OperationResult result = pedidoService.crearPedido(
                request.getUsuarioId(),
                request.getDireccionId(),
                items,
                request.getCuponId()
        );

        // Si falló la regla de negocio → 404 simple
        if (!result.isSuccess()) {
            String msg = result.getMessage() != null ? result.getMessage() : "Error al crear el pedido";
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(msg);
        }

        //  Usarrepository directamente
        List<Pedido> todos = pedidoRepository.findAll();
        if (todos == null || todos.isEmpty()) {
            return ResponseEntity
                    .internalServerError()
                    .body("El pedido se creó, pero no se pudo recuperar");
        }

        Pedido ultimo = todos.get(todos.size() - 1);
        PedidoResponse response = PedidoMapper.toResponse(ultimo);

        return ResponseEntity.ok(response);
    }


    // GET /api/pedidos

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<PedidoResponse>> listar() {
       
        List<Pedido> pedidos = pedidoRepository.findAll();

        List<PedidoResponse> responses = pedidos.stream()
                .map(PedidoMapper::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

 
    // GET /api/pedidos/{id}

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {

        //  USA REPOSITORY DIRECTAMENTE: Evita ciclo HTTP que causa deadlock
        var pedidoOpt = pedidoRepository.findById(id);

        if (pedidoOpt.isEmpty()) {
            Map<String, Object> body = Map.of(
                    "error", "Recurso no encontrado",
                    "details", List.of("No existe un pedido con id " + id)
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }

        Pedido pedido = pedidoOpt.get();
        PedidoResponse response = PedidoMapper.toResponse(pedido);
        return ResponseEntity.ok(response);
    }

    // =========================
    // GET /api/pedidos/test/error500

    @GetMapping("/test/error500")
    public void forzarError500() {
        throw new RuntimeException("Error interno de prueba");
    }
}