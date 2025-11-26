package com.urbancollection.ecommerce.api.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import com.urbancollection.ecommerce.persistence.jpa.spring.ProductoJpaRepository;

@ExtendWith(MockitoExtension.class)
class ProductoWebControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoJpaRepository productoRepository;

    @InjectMocks
    private ProductoWebController productoWebController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoWebController).build();
    }

    @Test
    void listar_cuandoTodoOk_deberiaMostrarListaProductos() throws Exception {
        when(productoRepository.findAll()).thenReturn(Collections.singletonList(new Producto()));

        mockMvc.perform(get("/web/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("producto/list"))
                .andExpect(model().attributeExists("productos"));
    }

    @Test
    void listar_cuandoRepositoryLanzaError_deberiaMostrarMensajeError() throws Exception {
        when(productoRepository.findAll()).thenThrow(new RuntimeException("fallo BD"));

        mockMvc.perform(get("/web/productos"))
                .andExpect(status().isOk())
                .andExpect(view().name("producto/list"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void mostrarFormularioCrear_deberiaCargarValoresPorDefecto() throws Exception {
        mockMvc.perform(get("/web/productos/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("producto/create"))
                .andExpect(model().attribute("nombre", ""))
                .andExpect(model().attribute("descripcion", ""))
                .andExpect(model().attribute("precio", ""))
                .andExpect(model().attribute("stock", ""));
    }

    @Test
    void crear_cuandoNombreVacio_deberiaVolverAFormularioConError() throws Exception {
        mockMvc.perform(post("/web/productos/create")
                        .param("nombre", "")
                        .param("descripcion", "Desc")
                        .param("precio", "100.00")
                        .param("stock", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("producto/create"))
                .andExpect(model().attribute("errorMessage", "El nombre del producto es obligatorio"));
    }

    @Test
    void crear_cuandoPrecioNegativo_deberiaVolverAFormularioConError() throws Exception {
        mockMvc.perform(post("/web/productos/create")
                        .param("nombre", "Gorra negra")
                        .param("descripcion", "Desc")
                        .param("precio", "-10.00")
                        .param("stock", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("producto/create"))
                .andExpect(model().attribute("errorMessage", "El precio debe ser mayor a 0"));
    }

    @Test
    void crear_cuandoTodoValido_deberiaRedirigirConMensajeExito() throws Exception {
        when(productoRepository.save(any(Producto.class))).thenReturn(new Producto());

        mockMvc.perform(post("/web/productos/create")
                        .param("nombre", "Gorra negra")
                        .param("descripcion", "Algodón")
                        .param("precio", "1500.00")
                        .param("stock", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/productos"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void mostrarFormularioEditar_cuandoProductoExiste_deberiaMostrarVistaEdit() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);
        
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        mockMvc.perform(get("/web/productos/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("producto/edit"))
                .andExpect(model().attributeExists("producto"));
    }

    @Test
    void mostrarFormularioEditar_cuandoProductoNoExiste_deberiaRedirigirConError() throws Exception {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/web/productos/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/productos"))
                .andExpect(flash().attribute("errorMessage", "⚠ Producto no encontrado"));
    }

    @Test
    void actualizar_cuandoProductoNoExiste_deberiaRedirigirConError() throws Exception {
        when(productoRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/web/productos/1/edit")
                        .param("nombre", "Gorra negra")
                        .param("descripcion", "Algodón")
                        .param("precio", "1500.00")
                        .param("stock", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/productos"))
                .andExpect(flash().attribute("errorMessage", "⚠ Producto no encontrado"));
    }

    @Test
    void actualizar_cuandoNombreInvalido_deberiaVolverAEditConError() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);
        
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        mockMvc.perform(post("/web/productos/1/edit")
                        .param("nombre", "")
                        .param("descripcion", "Algodón")
                        .param("precio", "1500.00")
                        .param("stock", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("producto/edit"))
                .andExpect(model().attribute("errorMessage", "Datos inválidos"))
                .andExpect(model().attributeExists("producto"));
    }

    @Test
    void actualizar_cuandoTodoValido_deberiaRedirigirConExito() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);
        
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        mockMvc.perform(post("/web/productos/1/edit")
                        .param("nombre", "Gorra negra")
                        .param("descripcion", "Algodón")
                        .param("precio", "1500.00")
                        .param("stock", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/productos"))
                .andExpect(flash().attribute("successMessage", "✓ Producto actualizado exitosamente"));
    }

    @Test
    void eliminar_cuandoProductoExiste_deberiaRedirigirConMensajeExito() throws Exception {
        when(productoRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(post("/web/productos/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/productos"))
                .andExpect(flash().attribute("successMessage", "✓ Producto eliminado exitosamente"));
    }

    @Test
    void eliminar_cuandoProductoNoExiste_deberiaRedirigirConMensajeError() throws Exception {
        when(productoRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(post("/web/productos/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/productos"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}