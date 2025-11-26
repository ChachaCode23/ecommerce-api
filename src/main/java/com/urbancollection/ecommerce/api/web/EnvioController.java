package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.api.web.dto.EnvioRequest;
import com.urbancollection.ecommerce.application.service.IEnvioService;
import com.urbancollection.ecommerce.application.service.IPedidoService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.logistica.Envio;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    private final IEnvioService envioService; 
    private final IPedidoService pedidoService; 

    public EnvioController(IEnvioService envioService, IPedidoService pedidoService) {
        this.envioService = envioService;
        this.pedidoService = pedidoService;
    }

    // ================== GET /api/envios ==================
    @GetMapping
    public ResponseEntity<List<Envio>> listar() {
        List<Envio> envios = envioService.listar();
        return ResponseEntity.ok(envios);
    }

    // ================== GET /api/envios/{id} ==================
    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtenerPorId(@PathVariable Long id) {
        Optional<Envio> envio = envioService.buscarPorId(id);
        
        if (envio.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(envio.get());
    }

    // ================== POST /api/envios ==================
    @PostMapping
    public ResponseEntity<Envio> crear(@RequestBody EnvioRequest request) {
        // Validar que el pedido existe
        Pedido pedido = pedidoService.obtenerPorId(request.getPedidoId());
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        Envio envio = new Envio();
        envio.setPedido(pedido);
        envio.setTracking(request.getTracking());
        envio.setEstado(request.getEstado());

        OperationResult result = envioService.crear(envio);
        
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(envio);
    }

    // ================== PUT /api/envios/{id} ==================
    @PutMapping("/{id}")
    public ResponseEntity<Envio> actualizar(@PathVariable Long id, @RequestBody EnvioRequest request) {
        Optional<Envio> existente = envioService.buscarPorId(id);
        
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Validar que el pedido existe
        Pedido pedido = pedidoService.obtenerPorId(request.getPedidoId());
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        Envio envio = existente.get();
        envio.setPedido(pedido);
        envio.setTracking(request.getTracking());
        envio.setEstado(request.getEstado());

        OperationResult result = envioService.actualizar(id, envio);
        
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(envio);
    }

    // ================== DELETE /api/envios/{id} ==================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Optional<Envio> existente = envioService.buscarPorId(id);
        
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        OperationResult result = envioService.eliminar(id);
        
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().build();
        }
        
        return ResponseEntity.noContent().build();
    }
}