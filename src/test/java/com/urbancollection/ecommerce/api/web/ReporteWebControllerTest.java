package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.application.service.IPedidoService;
import com.urbancollection.ecommerce.application.service.IProductoService;
import com.urbancollection.ecommerce.application.service.IUsuarioService;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ReporteWebControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IPedidoService pedidoService;

    @Mock
    private IProductoService productoService;

    @Mock
    private IUsuarioService usuarioService;

    @InjectMocks
    private ReporteWebController reporteWebController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reporteWebController).build();
    }

    // =========================
    // GET /web/reportes - OK
    // =========================
    @Test
    void mostrarReportes_cuandoTodoOk_deberiaRetornarVistaConEstadisticas() throws Exception {
        // pedidos mockeados (sin necesidad de setear total/estado)
        Pedido p1 = Mockito.mock(Pedido.class);
        Pedido p2 = Mockito.mock(Pedido.class);
        List<Pedido> pedidos = List.of(p1, p2);

        when(pedidoService.listarTodos()).thenReturn(pedidos);
        when(productoService.listar()).thenReturn(Collections.emptyList());
        when(usuarioService.listar()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/web/reportes"))
                .andExpect(status().isOk())
                .andExpect(view().name("reporte/index"))
                .andExpect(model().attributeExists("totalPedidos"))
                .andExpect(model().attributeExists("totalProductos"))
                .andExpect(model().attributeExists("totalUsuarios"))
                .andExpect(model().attributeExists("totalVentas"))
                .andExpect(model().attributeExists("promedioVenta"))
                .andExpect(model().attributeExists("pedidosPendientes"))
                .andExpect(model().attributeExists("pedidosPagados"))
                .andExpect(model().attributeExists("pedidosCompletados"));
    }

    // =========================
    // GET /web/reportes - ERROR
    // =========================
    @Test
    void mostrarReportes_cuandoServiceLanzaError_deberiaMostrarMensajeError() throws Exception {
        when(pedidoService.listarTodos()).thenThrow(new RuntimeException("fallo inesperado"));

        mockMvc.perform(get("/web/reportes"))
                .andExpect(status().isOk())
                .andExpect(view().name("reporte/index"))
                .andExpect(model().attributeExists("errorMessage"));
    }
}

