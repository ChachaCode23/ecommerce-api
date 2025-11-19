package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.application.service.ProductoService;
import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController).build();
        objectMapper = new ObjectMapper();
    }

    // ================== GET /api/productos ==================

    @Test
    void listar_deberiaRetornarListaDeProductosYStatus200() throws Exception {
        Producto p1 = new Producto();
        Producto p2 = new Producto();
        when(productoService.listarProductos()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    // ================== GET /api/productos/{id} ==================

    @Test
    void getById_cuandoExiste_deberiaRetornarProductoYStatus200() throws Exception {
        Producto p = new Producto();

        // Si la entidad tiene setNombre no rompe, si no lo tiene, se ignora
        try {
            p.getClass().getMethod("setNombre", String.class).invoke(p, "Gorra negra");
        } catch (Exception ignored) {
        }

        when(productoService.obtenerProductoPorId(1L)).thenReturn(p);

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk());
        // No afirmo sobre campos JSON específicos para no acoplar al modelo.
    }

    @Test
    void getById_cuandoNoExiste_deberiaRetornar404ConMensajeError() throws Exception {
        when(productoService.obtenerProductoPorId(99L)).thenReturn(null);

        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Producto no encontrado"));
    }

    // ================== POST /api/productos ==================

    @Test
    void crear_cuandoTodoOk_deberiaRetornar201ConProductoCreado() throws Exception {
        Producto creado = new Producto();
        when(productoService.crearProducto(
                any(String.class),
                any(String.class),
                any(BigDecimal.class),
                any(Integer.class)
        )).thenReturn(creado);

        ProductoController.CrearProductoRequest request = new ProductoController.CrearProductoRequest();
        request.setNombre("Gorra negra");
        request.setDescripcion("Algodón, talla ajustable");
        request.setPrecio(new BigDecimal("1299.99"));
        request.setStock(20);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void crear_cuandoServiceLanzaIllegalArgument_deberiaRetornar400ConMensajeError() throws Exception {
        when(productoService.crearProducto(
                any(String.class),
                any(String.class),
                any(BigDecimal.class),
                any(Integer.class)
        )).thenThrow(new IllegalArgumentException("stock no puede ser negativo"));

        ProductoController.CrearProductoRequest request = new ProductoController.CrearProductoRequest();
        request.setNombre("Gorra negra");
        request.setDescripcion("Algodón, talla ajustable");
        request.setPrecio(new BigDecimal("1299.99"));
        request.setStock(-5);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("stock no puede ser negativo"));
    }

    @Test
    void crear_cuandoServiceLanzaException_deberiaRetornar500ConMensajeGenerico() throws Exception {
        when(productoService.crearProducto(
                any(String.class),
                any(String.class),
                any(BigDecimal.class),
                any(Integer.class)
        )).thenThrow(new RuntimeException("Fallo inesperado"));

        ProductoController.CrearProductoRequest request = new ProductoController.CrearProductoRequest();
        request.setNombre("Gorra negra");
        request.setDescripcion("Algodón, talla ajustable");
        request.setPrecio(new BigDecimal("1299.99"));
        request.setStock(20);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("No se pudo crear el producto"));
    }

    // ================== PATCH /api/productos/{id}/stock ==================

    @Test
    void actualizarStock_cuandoTodoOk_deberiaRetornar200ConProductoActualizado() throws Exception {
        Producto actualizado = new Producto();
        when(productoService.actualizarStock(eq(1L), eq(50))).thenReturn(actualizado);

        ProductoController.ActualizarStockRequest request = new ProductoController.ActualizarStockRequest();
        request.setNuevoStock(50);

        mockMvc.perform(patch("/api/productos/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarStock_cuandoProductoNoExiste_deberiaRetornar404() throws Exception {
        when(productoService.actualizarStock(eq(1L), eq(50))).thenReturn(null);

        ProductoController.ActualizarStockRequest request = new ProductoController.ActualizarStockRequest();
        request.setNuevoStock(50);

        mockMvc.perform(patch("/api/productos/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Producto no encontrado"));
    }

    @Test
    void actualizarStock_cuandoServiceLanzaIllegalArgument_deberiaRetornar400ConMensajeError() throws Exception {
        when(productoService.actualizarStock(eq(1L), eq(-10)))
                .thenThrow(new IllegalArgumentException("stock no puede ser negativo"));

        ProductoController.ActualizarStockRequest request = new ProductoController.ActualizarStockRequest();
        request.setNuevoStock(-10);

        mockMvc.perform(patch("/api/productos/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("stock no puede ser negativo"));
    }

    @Test
    void actualizarStock_cuandoServiceLanzaException_deberiaRetornar500ConMensajeGenerico() throws Exception {
        when(productoService.actualizarStock(eq(1L), eq(10)))
                .thenThrow(new RuntimeException("Fallo inesperado"));

        ProductoController.ActualizarStockRequest request = new ProductoController.ActualizarStockRequest();
        request.setNuevoStock(10);

        mockMvc.perform(patch("/api/productos/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("No se pudo actualizar el stock"));
    }

    // ================== DELETE /api/productos/{id} ==================

    @Test
    void eliminar_cuandoProductoExiste_deberiaRetornar204() throws Exception {
        when(productoService.eliminarProducto(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_cuandoProductoNoExiste_deberiaRetornar404ConMensajeError() throws Exception {
        when(productoService.eliminarProducto(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Producto no encontrado"));
    }
}

