package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.application.service.IEnvioService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.logistica.Envio;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.domain.enums.EstadoDePedido;
import com.urbancollection.ecommerce.domain.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class EnvioWebControllerTest {

    @Mock
    private IEnvioService envioService;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private EnvioWebController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listar_DebeRetornarVistaEnvioList() {
        // Arrange
        List<Envio> envios = new ArrayList<>();
        when(envioService.listar()).thenReturn(envios);

        // Act
        String vista = controller.listar(model);

        // Assert
        assertEquals("envio/list", vista);
        verify(envioService).listar();
        verify(model).addAttribute("envios", envios);
    }

    @Test
    void listar_ConError_DebeMostrarMensajeError() {
        // Arrange
        when(envioService.listar()).thenThrow(new RuntimeException("Error"));

        // Act
        String vista = controller.listar(model);

        // Assert
        assertEquals("envio/list", vista);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void mostrarFormularioCrear_DebeRetornarVistaCreate() {
        // Arrange
        List<Pedido> pedidos = new ArrayList<>();
        Pedido pedido = new Pedido();
        pedido.setEstado(EstadoDePedido.PAGADO);
        pedido.setId(1L);
        pedidos.add(pedido);
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        // Act
        String vista = controller.mostrarFormularioCrear(model);

        // Assert
        assertEquals("envio/create", vista);
        verify(model).addAttribute(eq("pedidos"), anyList());
    }

    @Test
    void crear_ConDatosValidos_DebeCrearYRedirigir() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setEstado(EstadoDePedido.PAGADO);
        when(pedidoRepository.findById(1L)).thenReturn(pedido);
        when(envioService.crear(any(Envio.class))).thenReturn(OperationResult.success("Creado"));

        // Act
        String vista = controller.crear(1L, "TRACK123", "PENDIENTE", model, redirectAttributes);

        // Assert
        assertEquals("redirect:/web/envios", vista);
        verify(envioService).crear(any(Envio.class));
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void crear_SinPedidoId_DebeMostrarError() {
        // Act
        String vista = controller.crear(null, "TRACK123", "PENDIENTE", model, redirectAttributes);

        // Assert
        assertEquals("envio/create", vista);
        verify(model).addAttribute(eq("errorMessage"), eq("El pedido es obligatorio"));
    }

    @Test
    void crear_SinTracking_DebeMostrarError() {
        // Act
        String vista = controller.crear(1L, null, "PENDIENTE", model, redirectAttributes);

        // Assert
        assertEquals("envio/create", vista);
        verify(model).addAttribute(eq("errorMessage"), eq("El código de tracking es obligatorio"));
    }

    @Test
    void crear_ConTrackingCorto_DebeMostrarError() {
        // Act
        String vista = controller.crear(1L, "TR", "PENDIENTE", model, redirectAttributes);

        // Assert
        assertEquals("envio/create", vista);
        verify(model).addAttribute(eq("errorMessage"), eq("El tracking debe tener al menos 5 caracteres"));
    }

    @Test
    void crear_ConPedidoNoPagado_DebeMostrarError() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setEstado(EstadoDePedido.PENDIENTE_PAGO);
        when(pedidoRepository.findById(1L)).thenReturn(pedido);

        // Act
        String vista = controller.crear(1L, "TRACK123", "PENDIENTE", model, redirectAttributes);

        // Assert
        assertEquals("envio/create", vista);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void mostrarFormularioEditar_ConEnvioExistente_DebeRetornarVista() {
        // Arrange
        Envio envio = new Envio();
        when(envioService.buscarPorId(1L)).thenReturn(Optional.of(envio));

        // Act
        String vista = controller.mostrarFormularioEditar(1L, model, redirectAttributes);

        // Assert
        assertEquals("envio/edit", vista);
        verify(model).addAttribute("envio", envio);
    }

    @Test
    void mostrarFormularioEditar_ConEnvioNoExistente_DebeRedirigir() {
        // Arrange
        when(envioService.buscarPorId(999L)).thenReturn(Optional.empty());

        // Act
        String vista = controller.mostrarFormularioEditar(999L, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/web/envios", vista);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void actualizar_ConDatosValidos_DebeActualizarYRedirigir() {
        // Arrange
        Envio envio = new Envio();
        when(envioService.buscarPorId(1L)).thenReturn(Optional.of(envio));
        when(envioService.actualizar(anyLong(), any(Envio.class)))
            .thenReturn(OperationResult.success("Actualizado"));

        // Act
        String vista = controller.actualizar(1L, "TRACK456", "EN_TRANSITO", model, redirectAttributes);

        // Assert
        assertEquals("redirect:/web/envios", vista);
        verify(envioService).actualizar(anyLong(), any(Envio.class));
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void actualizar_SinTracking_DebeMostrarError() {
        // Arrange
        Envio envio = new Envio();
        when(envioService.buscarPorId(1L)).thenReturn(Optional.of(envio));

        // Act
        String vista = controller.actualizar(1L, null, "PENDIENTE", model, redirectAttributes);

        // Assert
        assertEquals("envio/edit", vista);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void eliminar_ConEnvioExistente_DebeEliminarYRedirigir() {
        // Arrange
        when(envioService.eliminar(1L)).thenReturn(OperationResult.success("Eliminado"));

        // Act
        String vista = controller.eliminar(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/web/envios", vista);
        verify(envioService).eliminar(1L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void eliminar_ConError_DebeMostrarMensajeError() {
        // Arrange
        when(envioService.eliminar(1L)).thenReturn(OperationResult.failure("Error"));

        // Act
        String vista = controller.eliminar(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/web/envios", vista);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}