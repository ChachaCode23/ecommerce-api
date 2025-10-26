package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.application.service.ProductoService;
import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // ================== GET /api/productos ==================
    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        List<Producto> lista = service.listarProductos();
        return ResponseEntity.ok(lista);
    }

    // ================== GET /api/productos/{id} ==================
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Producto p = service.obtenerProductoPorId(id);
        if (p == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Producto no encontrado"));
        }
        return ResponseEntity.ok(p);
    }

    // ================== POST /api/productos ==================
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearProductoRequest req) {
        try {
            Producto creado = service.crearProducto(
                    req.getNombre(),
                    req.getDescripcion(),
                    req.getPrecio(),
                    req.getStock()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(creado);

        } catch (IllegalArgumentException ex) {
            // validación mala del request
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Error creando producto", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo crear el producto"));
        }
    }

    // ================== PATCH /api/productos/{id}/stock ==================
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable Long id,
                                             @RequestBody ActualizarStockRequest req) {
        try {
            Producto actualizado = service.actualizarStock(id, req.getNuevoStock());
            if (actualizado == null) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Producto no encontrado"));
            }
            return ResponseEntity.ok(actualizado);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Error actualizando stock", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo actualizar el stock"));
        }
    }

    // ================== DELETE /api/productos/{id} ==================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        boolean eliminado = service.eliminarProducto(id);
        if (!eliminado) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Producto no encontrado"));
        }
        // 204 No Content
        return ResponseEntity.noContent().build();
    }

    // ================== DTOs request ==================

    // body para POST /api/productos
    public static class CrearProductoRequest {
        private String nombre;
        private String descripcion;
        private BigDecimal precio;
        private int stock;

        public String getNombre() {
            return nombre;
        }
        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }
        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public BigDecimal getPrecio() {
            return precio;
        }
        public void setPrecio(BigDecimal precio) {
            this.precio = precio;
        }

        public int getStock() {
            return stock;
        }
        public void setStock(int stock) {
            this.stock = stock;
        }
    }

    // body para PATCH /api/productos/{id}/stock
    public static class ActualizarStockRequest {
        private Integer nuevoStock;

        public Integer getNuevoStock() {
            return nuevoStock;
        }

        public void setNuevoStock(Integer nuevoStock) {
            this.nuevoStock = nuevoStock;
        }
    }
}
