package com.urbancollection.ecommerce.api.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.Optional;

import com.urbancollection.ecommerce.application.service.ICuponService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.catalogo.Cupon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CuponWebControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ICuponService cuponService;

    @InjectMocks
    private CuponWebController cuponWebController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cuponWebController).build();
    }

    // =========================
    // LISTAR
    // =========================

    @Test
    void listar_deberiaCargarCuponesYMostrarVista() throws Exception {
        Cupon cupon = new Cupon();
        when(cuponService.listar()).thenReturn(Collections.singletonList(cupon));

        mockMvc.perform(get("/web/cupones"))
                .andExpect(status().isOk())
                .andExpect(view().name("cupon/list"))
                .andExpect(model().attributeExists("cupones"));
    }

    @Test
    void listar_cuandoServicioFalla_deberiaMostrarError() throws Exception {
        when(cuponService.listar()).thenThrow(new RuntimeException("Fallo en BD"));

        mockMvc.perform(get("/web/cupones"))
                .andExpect(status().isOk())
                .andExpect(view().name("cupon/list"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    // =========================
    // FORMULARIO CREAR
    // =========================

    @Test
    void mostrarFormularioCrear_deberiaRetornarVistaConValoresPorDefecto() throws Exception {
        mockMvc.perform(get("/web/cupones/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("cupon/create"))
                .andExpect(model().attribute("codigo", ""))
                .andExpect(model().attribute("activo", true))
                .andExpect(model().attribute("tipo", "PORCENTAJE"))
                .andExpect(model().attribute("valorDescuento", ""))
                .andExpect(model().attribute("minimoCompra", ""))
                .andExpect(model().attribute("topeDescuento", ""))
                .andExpect(model().attribute("fechaInicio", ""))
                .andExpect(model().attribute("fechaFin", ""));
    }

    // =========================
    // CREAR (POST)
    // =========================

    @Test
    void crear_conDatosValidosYServicioOK_deberiaRedirigirConMensajeExito() throws Exception {
        OperationResult result = Mockito.mock(OperationResult.class);
        when(result.isSuccess()).thenReturn(true);
        when(cuponService.crear(any(Cupon.class))).thenReturn(result);

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
                .andExpect(flash().attribute("successMessage",
                        "✓ Cupón 'ABC123' creado exitosamente"));
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

    // =========================
    // EDITAR (GET)
    // =========================

    @Test
    void mostrarFormularioEditar_cuandoCuponExiste_deberiaMostrarVistaEdit() throws Exception {
        Cupon cupon = new Cupon();
        when(cuponService.buscarPorId(1L)).thenReturn(Optional.of(cupon));

        mockMvc.perform(get("/web/cupones/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("cupon/edit"))
                .andExpect(model().attributeExists("cupon"));
    }

    @Test
    void mostrarFormularioEditar_cuandoCuponNoExiste_deberiaRedirigirConError() throws Exception {
        when(cuponService.buscarPorId(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/web/cupones/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cupones"))
                .andExpect(flash().attribute("errorMessage", "⚠ Cupón no encontrado"));
    }

    // =========================
    // ACTUALIZAR (POST)
    // =========================

    @Test
    void actualizar_conDatosValidosYServicioOK_deberiaRedirigirConMensajeExito() throws Exception {
        Cupon cuponExistente = new Cupon();
        when(cuponService.buscarPorId(1L)).thenReturn(Optional.of(cuponExistente));

        OperationResult result = Mockito.mock(OperationResult.class);
        when(result.isSuccess()).thenReturn(true);
        when(cuponService.actualizar(Mockito.eq(1L), any(Cupon.class))).thenReturn(result);

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
                .andExpect(flash().attribute("successMessage", "✓ Cupón actualizado exitosamente"));
    }

    @Test
    void actualizar_conCodigoVacio_deberiaVolverAVistaEditConError() throws Exception {
        Cupon cuponExistente = new Cupon();
        when(cuponService.buscarPorId(1L)).thenReturn(Optional.of(cuponExistente));

        mockMvc.perform(post("/web/cupones/1/edit")
                        .param("codigo", "")
                        .param("activo", "true")
                        .param("tipo", "PORCENTAJE")
                        .param("valorDescuento", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("cupon/edit"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attributeExists("cupon"));
    }

    // =========================
    // ELIMINAR
    // =========================

    @Test
    void eliminar_cuandoServicioOK_deberiaRedirigirConMensajeExito() throws Exception {
        OperationResult result = Mockito.mock(OperationResult.class);
        when(result.isSuccess()).thenReturn(true);
        when(cuponService.eliminar(1L)).thenReturn(result);

        mockMvc.perform(post("/web/cupones/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cupones"))
                .andExpect(flash().attribute("successMessage", "✓ Cupón eliminado exitosamente"));
    }

    @Test
    void eliminar_cuandoServicioDevuelveErrorNegocio_deberiaRedirigirConMensajeError() throws Exception {
        OperationResult result = Mockito.mock(OperationResult.class);
        when(result.isSuccess()).thenReturn(false);
        when(result.getMessage()).thenReturn("No se puede eliminar");
        when(cuponService.eliminar(1L)).thenReturn(result);

        mockMvc.perform(post("/web/cupones/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/web/cupones"))
                .andExpect(flash().attribute("errorMessage", "⚠ No se puede eliminar"));
    }
}
