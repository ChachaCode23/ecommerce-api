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

/**

 *
 * Desde aquí expongo las operaciones principales sobre el catálogo:
 *  - listar productos
 *  - obtener detalle de un producto por ID
 *  - crear un producto nuevo
 *  - actualizar el stock de un producto existente
 *  - eliminar un producto
 *
 * Este controlador es la "capa web". no hago la lógica de negocio
 * Eso lo delego al ProductoService.
 *
 * Aquí básicamente:
 *  - Recibo los datos del request.
 *  - Hago validaciones básicas (que no falte nada obvio).
 *  - Llamo al service.
 *  - Devuelvo la respuesta con el código HTTP correcto.
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    // Inyecto el servicio de productos (capa de aplicación / negocio)
    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    // ================== GET /api/productos ==================
    /**
     * Este endpoint devuelve la lista de todos los productos.
     *
     * Flujo:
     * 1. Llama al service para obtener los productos.
     * 2. Devuelve la lista directamente con código 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<Producto>> listar() {
        List<Producto> lista = service.listarProductos();
        return ResponseEntity.ok(lista);
    }

    // ================== GET /api/productos/{id} ==================
    /**
     * Este endpoint devuelve un producto específico por su ID.
     *
     * Si el producto existe -> 200 OK con el producto.
     * Si no existe -> 404 con un JSON que dice "Producto no encontrado".
     *
     * Nota:
     * Devuelve ResponseEntity<?> porque a veces devuelve el objeto Producto
     * y a veces devuelve un Map con el error.
     */
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
    /**
     * Crea un producto nuevo.
     *
     * Ejemplo del body que espero:
     * {
     *   "nombre": "Gorra negra edición limitada",
     *   "descripcion": "Algodón, talla ajustable",
     *   "precio": 1299.99,
     *   "stock": 20
     * }
     *
     * Flujo:
     * 1. Recibo el request como CrearProductoRequest.
     * 2. Llamo al service.crearProducto(...) pasándole esos datos.
     * 3. Si el service lo crea bien, devuelvo 201 Created con el producto creado.
     *
     * Manejo de errores:
     * - Si el service tira IllegalArgumentException, eso normalmente es validación de negocio,
     *   por ejemplo "stock no puede ser negativo" o "precio inválido".
     *   En ese caso respondo 400 Bad Request con el mensaje.
     *
     * - Si pasa un error inesperado (null raro, excepción interna),
     *   devuelvo 500 Internal Server Error.
     */
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
                    .status(HttpStatus.CREATED) // 201
                    .body(creado);

        } catch (IllegalArgumentException ex) {
            // Validación de negocio falló (por ejemplo datos inválidos del request).
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // 400
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            // Error inesperado del servidor.
            log.error("Error creando producto", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                    .body(Map.of("error", "No se pudo crear el producto"));
        }
    }

    // ================== PATCH /api/productos/{id}/stock ==================
    /**
     * Actualiza solamente el stock de un producto ya existente.
     *
     * Ejemplo:
     *   PATCH /api/productos/10/stock
     *   Body:
     *   {
     *     "nuevoStock": 45
     *   }
     *
     * Flujo:
     * 1. Recibo el id del producto y el nuevo stock.
     * 2. Llamo al service.actualizarStock(id, nuevoStock).
     * 3. Si el producto existe y se pudo actualizar, devuelvo 200 con el producto actualizado.
     *
     * Manejo de errores:
     * - Si el producto no existe -> devuelvo 404.
     * - Si el stock es inválido (por ejemplo negativo) -> devuelvo 400.
     * - Si algo truena inesperado -> devuelvo 500.
     *
     * Nota:
     * Aquí uso PATCH porque no estoy reemplazando todo el recurso,
     * solo estoy cambiando una parte (el stock).
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(@PathVariable Long id,
                                             @RequestBody ActualizarStockRequest req) {
        try {
            Producto actualizado = service.actualizarStock(id, req.getNuevoStock());
            if (actualizado == null) {
                // No se encontró ese producto
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND) // 404
                        .body(Map.of("error", "Producto no encontrado"));
            }
            // OK, devuelvo el producto con su stock nuevo
            return ResponseEntity.ok(actualizado);

        } catch (IllegalArgumentException ex) {
            // Ejemplo: req.getNuevoStock() < 0
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // 400
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            log.error("Error actualizando stock", ex);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // 500
                    .body(Map.of("error", "No se pudo actualizar el stock"));
        }
    }

    // ================== DELETE /api/productos/{id} ==================
    /**
     * Elimina un producto por ID.
     *
     * Flujo:
     * 1. Llamo service.eliminarProducto(id)
     *    - true  -> sí lo borró
     *    - false -> no existe ese producto
     *
     * 2. Si no existe -> 404 con mensaje "Producto no encontrado".
     * 3. Si se borró bien -> 204 No Content (o sea, borrado exitoso sin body).
     *
     * Nota:
     * 204 es la respuesta clásica cuando la operación fue exitosa
     * pero no necesitas devolver contenido.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        boolean eliminado = service.eliminarProducto(id);
        if (!eliminado) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND) // 404
                    .body(Map.of("error", "Producto no encontrado"));
        }
        // Si se eliminó correctamente -> devuelvo 204.
        return ResponseEntity.noContent().build();
    }

    // ================== DTOs request ==================
    // Estas clases internas representan el body que me manda el cliente
    // en los endpoints POST y PATCH.
    //
    // Yo hago esto para no exponer la entidad Producto completa como body de entrada.
    // O sea, controlo exactamente qué campos el cliente puede mandar/modificar.

    /**
     * CrearProductoRequest:
     * Esta clase representa el body que espero en POST /api/productos.
     *
     * Campos:
     * - nombre: nombre comercial del producto.
     * - descripcion: texto descriptivo.
     * - precio: BigDecimal (precio de venta).
     * - stock: cantidad inicial en inventario.
     *
     * El controlador usa los getters de aquí para pasárselos al service.
     * También están los setters porque Spring hace el binding automático
     * desde el JSON del request a este objeto.
     */
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

    /**
     * ActualizarStockRequest:
     * Esta clase representa el body que espero en:
     * PATCH /api/productos/{id}/stock
     *
     * Solo trae el nuevoStock porque yo estoy actualizando exclusivamente eso.
     * No quiero que desde este endpoint el cliente cambie nombre, precio, etc.
     *
     * Esto es importante para seguridad/lógica de negocio: limito qué se puede editar.
     */
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
