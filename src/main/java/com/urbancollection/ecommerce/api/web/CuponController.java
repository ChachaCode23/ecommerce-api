package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.api.web.dto.CuponRequest;
import com.urbancollection.ecommerce.application.service.ICuponService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.catalogo.Cupon;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cupones")
public class CuponController {

    private final ICuponService cuponService;

    public CuponController(ICuponService cuponService) {
        this.cuponService = cuponService;
    }

    // ================== GET /api/cupones ==================
    @GetMapping
    public ResponseEntity<List<Cupon>> listar() {
        List<Cupon> cupones = cuponService.listar();
        return ResponseEntity.ok(cupones);
    }

    // ================== GET /api/cupones/{id} ==================
    @GetMapping("/{id}")
    public ResponseEntity<Cupon> obtenerPorId(@PathVariable Long id) {
        Optional<Cupon> cupon = cuponService.buscarPorId(id);
        
        if (cupon.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(cupon.get());
    }

    // ================== POST /api/cupones ==================
    @PostMapping
    public ResponseEntity<Cupon> crear(@RequestBody CuponRequest request) {
        Cupon cupon = new Cupon();
        cupon.setCodigo(request.getCodigo());
        cupon.setActivo(request.isActivo());
        cupon.setFechaInicio(request.getFechaInicio());
        cupon.setFechaFin(request.getFechaFin());
        cupon.setMinimoCompra(request.getMinimoCompra());
        cupon.setTipo(request.getTipo());
        cupon.setValorDescuento(request.getValorDescuento());
        cupon.setTopeDescuento(request.getTopeDescuento());

        OperationResult result = cuponService.crear(cupon);
        
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(cupon);
    }

    // ================== PUT /api/cupones/{id} ==================
    @PutMapping("/{id}")
    public ResponseEntity<Cupon> actualizar(@PathVariable Long id, @RequestBody CuponRequest request) {
        Optional<Cupon> existente = cuponService.buscarPorId(id);

        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Cupon cupon = existente.get();
        cupon.setCodigo(request.getCodigo());
        cupon.setActivo(request.isActivo());
        cupon.setFechaInicio(request.getFechaInicio());
        cupon.setFechaFin(request.getFechaFin());
        cupon.setMinimoCompra(request.getMinimoCompra());
        cupon.setTipo(request.getTipo());
        cupon.setValorDescuento(request.getValorDescuento());
        cupon.setTopeDescuento(request.getTopeDescuento());

        OperationResult result = cuponService.actualizar(id, cupon);
        
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(cupon);
    }

    // ================== DELETE /api/cupones/{id} ==================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Optional<Cupon> existente = cuponService.buscarPorId(id);

        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        OperationResult result = cuponService.eliminar(id);
        
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }
}