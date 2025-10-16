package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.api.web.dto.ProductoCreateRequest;          // DTO de entrada para crear/actualizar productos.
import com.urbancollection.ecommerce.application.dto.ProductoDTO;                // DTO de salida (lo que exponemos al cliente).
import com.urbancollection.ecommerce.application.service.ProductoService;        // Servicio de aplicación con la lógica de negocio.
import com.urbancollection.ecommerce.domain.base.OperationResult;               // Resultado estándar (éxito/error + mensaje).
import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;            // Entidad de dominio Producto.

import io.swagger.v3.oas.annotations.Operation;                                  // Anotaciones de Swagger/OpenAPI.
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;                                                 // Para validar el body usando Bean Validation.
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;



@RequestMapping(value = "/api/productos", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Productos", description = "Gestión básica de productos")
public class ProductoController {

    private final ProductoService productoService; // Dependencia al servicio, inyeccion por constructor.

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /**
     * GET /api/productos
     * Lista todos los productos en formato DTO.
     */
    @Operation(summary = "Listar productos")
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar() {
        return ResponseEntity.ok(productoService.listar());
    }

    /**
     * GET /api/productos/{id}
     * Busca un producto por id y lo retorna como DTO.
     * - Si no existe, responde 404 con un body {"error": "..."}.
     */
    @Operation(summary = "Obtener producto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        Optional<ProductoDTO> dto = productoService.buscarPorId(id);
        return dto.<ResponseEntity<?>>map(ResponseEntity::ok)
                  .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                          .body(Map.of("error", "Producto no encontrado")));
    }


    /**
     * POST /api/productos
     * Crea un producto nuevo.
     * 1) Recibe ProductoCreateRequest (validado con @Valid).
     * 2) Mapea a entidad Producto (capa dominio).
     * 3) Llama a productoService.crear(...) que hace validaciones/negocio y guarda.
     * 4) Responde 201 Created si salió bien; 400 si falló alguna validación/regla.
     */
    @Operation(summary = "Crear producto")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> crear(@Valid @RequestBody ProductoCreateRequest req) {
        // Mapeo Request -> Entidad
        Producto p = new Producto();
        p.setNombre(req.getNombre());
        p.setDescripcion(req.getDescripcion());
        p.setPrecio(req.getPrecio());
        p.setStock(req.getStock());

        // Caso de uso en el servicio
        OperationResult r = productoService.crear(p);

        // Armar respuesta estandar segun resultado
        Map<String, Object> body = new LinkedHashMap<>();
        if (r.isSuccess()) {
            body.put("message", r.getMessage());
            return ResponseEntity.status(HttpStatus.CREATED).body(body);
        } else {
            body.put("error", r.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }
    }

    /**
     * PUT /api/productos/{id}
     * Actualiza un producto existente.
     * - Usa el mismo DTO de creacion para simplicidad (nombre, descripcion, precio, stock).
     * - Si el servicio reporta error (ej. duplicado, validacion), retorna 400 con detalle.
     */
    @Operation(summary = "Actualizar producto")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> actualizar(@PathVariable Long id,
                                        @Valid @RequestBody ProductoCreateRequest req) {
        // Mapeo Request -> Entidad con los cambios permitidos
        Producto cambios = new Producto();
        cambios.setNombre(req.getNombre());
        cambios.setDescripcion(req.getDescripcion());
        cambios.setPrecio(req.getPrecio());
        cambios.setStock(req.getStock());

        OperationResult r = productoService.actualizar(id, cambios);

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
     * DELETE /api/productos/{id}
     * Elimina un producto por su id.
     * - Responde 200 con mensaje si se eliminó.
     * - Si no existe, el servicio devuelve error y respondemos 400 con detalle.
     */
    @Operation(summary = "Eliminar producto")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        OperationResult r = productoService.eliminar(id);

        Map<String, Object> body = new LinkedHashMap<>();
        if (r.isSuccess()) {
            body.put("message", r.getMessage());
            return ResponseEntity.ok(body);
        } else {
            body.put("error", r.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
        }
    }
}
