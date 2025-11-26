package com.urbancollection.ecommerce.api.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbancollection.ecommerce.application.dto.ProductoDTO;
import com.urbancollection.ecommerce.application.service.IProductoService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController).build();
        new ObjectMapper();
    }

    @Test
    void listar_deberiaRetornarListaDeProductosYStatus200() throws Exception {
        ProductoDTO dto1 = new ProductoDTO();
        dto1.setId(1L);
        dto1.setNombre("Producto 1");
        dto1.setPrecio(new BigDecimal("100.00"));
        dto1.setStock(10);

        ProductoDTO dto2 = new ProductoDTO();
        dto2.setId(2L);
        dto2.setNombre("Producto 2");
        dto2.setPrecio(new BigDecimal("200.00"));
        dto2.setStock(20);

        when(productoService.listar()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Producto 1"))
                .andExpect(jsonPath("$[1].nombre").value("Producto 2"));
    }

    @Test
    void getById_cuandoExiste_deberiaRetornarProductoYStatus200() throws Exception {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(1L);
        dto.setNombre("Producto Test");
        dto.setPrecio(new BigDecimal("100.00"));
        dto.setStock(10);

        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Producto Test"));
    }

    @Test
    void getById_cuandoNoExiste_deberiaRetornar404ConMensajeError() throws Exception {
        when(productoService.buscarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Producto no encontrado"));
    }

    @Test
    void crear_cuandoTodoOk_deberiaRetornar201ConProductoCreado() throws Exception {
        String requestBody = """
                {
                    "nombre": "Nuevo Producto",
                    "descripcion": "Descripción",
                    "precio": 150.00,
                    "stock": 25
                }
                """;

        when(productoService.crear(any(Producto.class)))
                .thenReturn(OperationResult.success("Producto creado"));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Nuevo Producto"));
    }

    @Test
    void crear_cuandoNombreVacio_deberiaRetornar400() throws Exception {
        String requestBody = """
                {
                    "nombre": "",
                    "precio": 150.00,
                    "stock": 25
                }
                """;

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El nombre es obligatorio"));
    }

    @Test
    void crear_cuandoPrecioInvalido_deberiaRetornar400() throws Exception {
        String requestBody = """
                {
                    "nombre": "Producto",
                    "precio": -10,
                    "stock": 25
                }
                """;

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El precio debe ser mayor a 0"));
    }

    @Test
    void actualizarStock_cuandoTodoOk_deberiaRetornar200ConProductoActualizado() throws Exception {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(1L);
        dto.setNombre("Producto");
        dto.setPrecio(new BigDecimal("100.00"));
        dto.setStock(50);

        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(dto));
        when(productoService.actualizar(eq(1L), any(Producto.class)))
                .thenReturn(OperationResult.success("Stock actualizado"));

        String requestBody = """
                {
                    "nuevoStock": 100
                }
                """;

        mockMvc.perform(patch("/api/productos/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(100));
    }

    @Test
    void actualizarStock_cuandoProductoNoExiste_deberiaRetornar404() throws Exception {
        when(productoService.buscarPorId(999L)).thenReturn(Optional.empty());

        String requestBody = """
                {
                    "nuevoStock": 100
                }
                """;

        mockMvc.perform(patch("/api/productos/999/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Producto no encontrado"));
    }

    @Test
    void actualizarStock_cuandoStockNegativo_deberiaRetornar400() throws Exception {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(1L);
        dto.setNombre("Producto");
        dto.setPrecio(new BigDecimal("100.00"));
        dto.setStock(50);

        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(dto));

        String requestBody = """
                {
                    "nuevoStock": -5
                }
                """;

        mockMvc.perform(patch("/api/productos/1/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El stock no puede ser negativo"));
    }

    @Test
    void eliminar_cuandoProductoExiste_deberiaRetornar204() throws Exception {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(1L);

        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(dto));
        when(productoService.eliminar(1L)).thenReturn(OperationResult.success("Eliminado"));

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminar_cuandoProductoNoExiste_deberiaRetornar404ConMensajeError() throws Exception {
        when(productoService.buscarPorId(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Producto no encontrado"));
    }
}