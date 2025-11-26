package com.urbancollection.ecommerce.api.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.Optional;

import com.urbancollection.ecommerce.domain.entity.catalogo.Cupon;
import com.urbancollection.ecommerce.persistence.jpa.spring.CuponJpaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CuponWebControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CuponJpaRepository cuponRepository;

    @InjectMocks
    private CuponWebController cuponWebController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cuponWebController).build();
    }

    @Test
    void listar_deberiaCargarCuponesYMostrarVista() throws Exception {
        Cupon cupon = new Cupon();
        when(cuponRepository.findAll()).thenReturn(Collections.singletonList(cupon));

        mockMvc.perform(get("/web/cupones"))
                .andExpect(status().isOk())
                .andExpect(view().name("cupon/list"))
                .andExpect(model().attributeExists("cupones"));
    }

    @Test
    void listar_cuandoRepositoryFalla_deberiaMostrarError() throws Exception {
        when(cuponRepository.findAll()).thenThrow(new RuntimeException("Fallo en BD"));

        mockMvc.perform(get("/web/cupones"))
                .andExpect(status().isOk())
                .andExpect(view().name("cupon/list"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void mostrarFormularioCrear_deberiaRetornarVistaConValoresPorDefecto() throws Exception {
        mockMvc.perform(get("/web/cupones/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("cupon/create"))
                .andExpect(model().attribute("codigo", ""))
                .andExpect(model().attribute("activo", true))
                .andExpect(model().attribute("tipo", "PORCENTAJE"))
                .andExpect(model().attribute("valorDescuento", ""));
    }

    @Test
    void crear_conDatosValidosYRepositoryOK_deberiaRedirigirConMensajeExito() throws Exception {
        when(cuponRepository.findByCodigo("ABC123")).thenReturn(Optional.empty());
        when(cuponRepository.save(any(Cupon.class))).thenReturn(new Cupon());

        mockMvc.perform(post("/web/cupones/create")
                        .param("codigo", "ABC123")
                        .param("activo", "true")
                        .param("tipo", "PORCENTAJE")
                        .param("valorDescuento", "10")
                        .param("minimoCompra", "100")
                        .param("topeDescuento", "50")
                        .param("fechaInicio", "2025-01-01")
                        .param("fechaFin", "2025-12-31"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cupones"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void crear_conCodigoVacio_deberiaVolverAlFormularioConError() throws Exception {
        mockMvc.perform(post("/web/cupones/create")
                        .param("codigo", "")
                        .param("activo", "true")
                        .param("tipo", "PORCENTAJE")
                        .param("valorDescuento", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("cupon/create"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void mostrarFormularioEditar_cuandoCuponExiste_deberiaMostrarVistaEdit() throws Exception {
        Cupon cupon = new Cupon();
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(cupon));

        mockMvc.perform(get("/web/cupones/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("cupon/edit"))
                .andExpect(model().attributeExists("cupon"));
    }

    @Test
    void mostrarFormularioEditar_cuandoCuponNoExiste_deberiaRedirigirConError() throws Exception {
        when(cuponRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/web/cupones/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cupones"))
                .andExpect(flash().attribute("errorMessage", "⚠ Cupón no encontrado"));
    }

    @Test
    void actualizar_conDatosValidosYRepositoryOK_deberiaRedirigirConMensajeExito() throws Exception {
        Cupon cuponExistente = new Cupon();
        cuponExistente.setId(1L);
        cuponExistente.setCodigo("ABC123");
        when(cuponRepository.findById(1L)).thenReturn(Optional.of(cuponExistente));
        when(cuponRepository.findByCodigo("ABC123")).thenReturn(Optional.of(cuponExistente));
        when(cuponRepository.save(any(Cupon.class))).thenReturn(cuponExistente);

        mockMvc.perform(post("/web/cupones/1/edit")
                        .param("codigo", "ABC123")
                        .param("activo", "true")
                        .param("tipo", "PORCENTAJE")
                        .param("valorDescuento", "10")
                        .param("minimoCompra", "100")
                        .param("topeDescuento", "50")
                        .param("fechaInicio", "2025-01-01")
                        .param("fechaFin", "2025-12-31"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cupones"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void actualizar_conCuponNoExistente_deberiaRedirigirConError() throws Exception {
        when(cuponRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/web/cupones/1/edit")
                        .param("codigo", "ABC123")
                        .param("activo", "true")
                        .param("tipo", "PORCENTAJE")
                        .param("valorDescuento", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cupones"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void eliminar_cuandoRepositoryOK_deberiaRedirigirConMensajeExito() throws Exception {
        when(cuponRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(post("/web/cupones/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cupones"))
                .andExpect(flash().attribute("successMessage", "✓ Cupón eliminado exitosamente"));
    }

    @Test
    void eliminar_cuandoCuponNoExiste_deberiaRedirigirConMensajeError() throws Exception {
        when(cuponRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(post("/web/cupones/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cupones"))
                .andExpect(flash().attribute("errorMessage", "⚠ Cupón no encontrado"));
    }
}