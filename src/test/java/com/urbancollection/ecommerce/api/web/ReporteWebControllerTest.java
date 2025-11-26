package com.urbancollection.ecommerce.api.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.persistence.jpa.spring.PedidoJpaRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.ProductoJpaRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.UsuarioJpaRepository;


@ExtendWith(MockitoExtension.class)
class ReporteWebControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PedidoJpaRepository pedidoRepository;

    @Mock
    private ProductoJpaRepository productoRepository;

    @Mock
    private UsuarioJpaRepository usuarioRepository;

    @InjectMocks
    private ReporteWebController reporteWebController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reporteWebController).build();
    }

    @Test
    void mostrarReportes_cuandoTodoOk_deberiaRetornarVistaConEstadisticas() throws Exception {
        Pedido p1 = new Pedido();
        p1.setTotal(new BigDecimal("100.00"));
        
        Pedido p2 = new Pedido();
        p2.setTotal(new BigDecimal("200.00"));
        
        List<Pedido> pedidos = List.of(p1, p2);

        when(pedidoRepository.findAll()).thenReturn(pedidos);
        when(productoRepository.count()).thenReturn(5L);
        when(usuarioRepository.count()).thenReturn(10L);

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

    @Test
    void mostrarReportes_cuandoRepositoryLanzaError_deberiaMostrarMensajeError() throws Exception {
        when(pedidoRepository.findAll()).thenThrow(new RuntimeException("fallo inesperado"));

        mockMvc.perform(get("/web/reportes"))
                .andExpect(status().isOk())
                .andExpect(view().name("reporte/index"))
                .andExpect(model().attributeExists("errorMessage"));
    }
}