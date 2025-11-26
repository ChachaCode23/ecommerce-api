package com.urbancollection.ecommerce.api.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urbancollection.ecommerce.application.dto.ProductoDTO;
import com.urbancollection.ecommerce.application.service.IProductoService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    private final IProductoService productoService; // ✅ CAMBIADO: Service en lugar de Repository

    public ProductoController(IProductoService productoService) {
        this.productoService = productoService;
    }

    // ================== GET /api/productos ==================
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar() {
        List<ProductoDTO> lista = productoService.listar();
        return ResponseEntity.ok(lista);
    }

    // ================== GET /api/productos/{id} ==================
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<ProductoDTO> producto = productoService.buscarPorId(id);
        
        if (producto.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Producto no encontrado"));
        }
        
        return ResponseEntity.ok(producto.get());
    }

    // ================== POST /api/productos ==================
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearProductoRequest req) {
        try {
            // Validaciones básicas
            if (req.getNombre() == null || req.getNombre().trim().isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El nombre es obligatorio"));
            }
            
            if (req.getPrecio() == null || req.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El precio debe ser mayor a 0"));
            }
            
            if (req.getStock() < 0) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El stock no puede ser negativo"));
            }

            // Crear producto
            Producto producto = new Producto();
            producto.setNombre(req.getNombre());
            producto.setDescripcion(req.getDescripcion());
            producto.setPrecio(req.getPrecio());
            producto.setStock(req.getStock());

            OperationResult result = productoService.crear(producto);
            
            if (!result.isSuccess()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", result.getMessage()));
            }

            // Convertir a DTO para respuesta
            ProductoDTO dto = new ProductoDTO();
            dto.setId(producto.getId());
            dto.setNombre(producto.getNombre());
            dto.setDescripcion(producto.getDescripcion());
            dto.setPrecio(producto.getPrecio());
            dto.setStock(producto.getStock());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(dto);

        } catch (IllegalArgumentException ex) {
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

    // ================== PUT /api/productos/{id} ==================
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @RequestBody CrearProductoRequest req) {
        try {
            Optional<ProductoDTO> productoOpt = productoService.buscarPorId(id);
            
            if (productoOpt.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Producto no encontrado"));
            }

            // Validaciones
            if (req.getNombre() == null || req.getNombre().trim().isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El nombre es obligatorio"));
            }
            
            if (req.getPrecio() == null || req.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El precio debe ser mayor a 0"));
            }
            
            if (req.getStock() < 0) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El stock no puede ser negativo"));
            }

            // Crear entidad con datos actualizados
            Producto producto = new Producto();
            producto.setId(id);
            producto.setNombre(req.getNombre());
            producto.setDescripcion(req.getDescripcion());
            producto.setPrecio(req.getPrecio());
            producto.setStock(req.getStock());

            OperationResult result = productoService.actualizar(id, producto);
            
            if (!result.isSuccess()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", result.getMessage()));
            }

            // Convertir a DTO para respuesta
            ProductoDTO dto = new ProductoDTO();
            dto.setId(producto.getId());
            dto.setNombre(producto.getNombre());
            dto.setDescripcion(producto.getDescripcion());
            dto.setPrecio(producto.getPrecio());
            dto.setStock(producto.getStock());
            
            return ResponseEntity.ok(dto);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Error actualizando producto", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo actualizar el producto"));
        }
    }

    // ================== PATCH /api/productos/{id}/stock ==================
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable Long id,
                                             @RequestBody ActualizarStockRequest req) {
        try {
            Optional<ProductoDTO> productoOpt = productoService.buscarPorId(id);
            
            if (productoOpt.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Producto no encontrado"));
            }
            
            if (req.getNuevoStock() == null || req.getNuevoStock() < 0) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "El stock no puede ser negativo"));
            }

            ProductoDTO dto = productoOpt.get();
            Producto producto = new Producto();
            producto.setId(dto.getId());
            producto.setNombre(dto.getNombre());
            producto.setDescripcion(dto.getDescripcion());
            producto.setPrecio(dto.getPrecio());
            producto.setStock(req.getNuevoStock());

            OperationResult result = productoService.actualizar(id, producto);
            
            if (!result.isSuccess()) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", result.getMessage()));
            }

            // Actualizar DTO con nuevo stock
            dto.setStock(req.getNuevoStock());
            
            return ResponseEntity.ok(dto);

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
        Optional<ProductoDTO> producto = productoService.buscarPorId(id);
        
        if (producto.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Producto no encontrado"));
        }
        
        OperationResult result = productoService.eliminar(id);
        
        if (!result.isSuccess()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", result.getMessage()));
        }
        
        return ResponseEntity.noContent().build();
    }

    // ================== DTOs request ==================

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