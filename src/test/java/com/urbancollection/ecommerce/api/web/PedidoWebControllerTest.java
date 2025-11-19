package com.urbancollection.ecommerce.api.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.urbancollection.ecommerce.api.web.dto.PedidoResponse;
import com.urbancollection.ecommerce.application.service.IPedidoService;
import com.urbancollection.ecommerce.application.service.IProductoService;
import com.urbancollection.ecommerce.application.service.IUsuarioService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.domain.repository.DireccionRepository;

class PedidoWebControllerTest {

    @Mock
    private IPedidoService pedidoService;

    @Mock
    private IProductoService productoService;

    @Mock
    private IUsuarioService usuarioService;

    @Mock
    private DireccionRepository direccionRepository;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private PedidoWebController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listar_DebeRetornarVistaPedidoList() {
        // Arrange
        List<Pedido> pedidos = new ArrayList<>();
        when(pedidoService.listarTodos()).thenReturn(pedidos);

        // Act
        String vista = controller.listar(model, null);

        // Assert
        assertEquals("pedido/list", vista);
        verify(pedidoService).listarTodos();
        verify(model).addAttribute(eq("pedidos"), anyList());
    }

    @Test
    void listar_ConError_DebeMostrarMensajeError() {
        // Arrange
        when(pedidoService.listarTodos()).thenThrow(new RuntimeException("Error de prueba"));

        // Act
        String vista = controller.listar(model, null);

        // Assert
        assertEquals("pedido/list", vista);
        verify(model).addAttribute(eq("errorMessage"), anyString());
        verify(model).addAttribute(eq("pedidos"), any(ArrayList.class));
    }

    @Test
    void detalle_ConPedidoExistente_DebeRetornarVista() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        when(pedidoService.obtenerPorId(1L)).thenReturn(pedido);

        // Act
        String vista = controller.detalle(1L, model, redirectAttributes);

        // Assert
        assertEquals("pedido/detail", vista);
        verify(pedidoService).obtenerPorId(1L);
        verify(model).addAttribute(eq("pedido"), any(PedidoResponse.class));
    }

    @Test
    void detalle_ConPedidoNoExistente_DebeRedirigir() {
        // Arrange
        when(pedidoService.obtenerPorId(999L)).thenReturn(null);

        // Act
        String vista = controller.detalle(999L, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/web/pedidos", vista);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void marcarComoPagado_SinMetodoPago_DebeMostrarError() {
        // Act
        String vista = controller.marcarComoPagado(1L, null, redirectAttributes);

        // Assert
        assertEquals("redirect:/web/pedidos/1", vista);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void marcarComoPagado_ConExito_DebeMostrarMensajeExito() {
        // Arrange
        when(pedidoService.marcarComoPagado(1L)).thenReturn(OperationResult.success("Éxito"));

        // Act
        String vista = controller.marcarComoPagado(1L, "TARJETA", redirectAttributes);

        // Assert
        assertEquals("redirect:/web/pedidos/1", vista);
        verify(pedidoService).marcarComoPagado(1L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void marcarComoPagado_ConFallo_DebeMostrarError() {
        // Arrange
        when(pedidoService.marcarComoPagado(1L)).thenReturn(OperationResult.failure("Error"));

        // Act
        String vista = controller.marcarComoPagado(1L, "TARJETA", redirectAttributes);

        // Assert
        assertEquals("redirect:/web/pedidos/1", vista);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void mostrarFormularioCrear_DebeRetornarVistaCreate() {
        // Act
        String vista = controller.mostrarFormularioCrear(model);

        // Assert
        assertEquals("pedido/create", vista);
        verify(model, times(5)).addAttribute(anyString(), any());
    }

    @Test
    void crearPedido_ConDatosValidos_DebeCrearYRedirigir() {
        // Arrange
        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(new com.urbancollection.ecommerce.domain.entity.usuarios.Usuario()));
        when(direccionRepository.findById(1L)).thenReturn(new com.urbancollection.ecommerce.domain.entity.logistica.Direccion());
        
        // ProductoDTO en lugar de Producto
        com.urbancollection.ecommerce.application.dto.ProductoDTO productoDTO = 
            new com.urbancollection.ecommerce.application.dto.ProductoDTO();
        productoDTO.setId(1L);
        when(productoService.buscarPorId(1L)).thenReturn(Optional.of(productoDTO));
        
        when(pedidoService.crearPedido(anyLong(), anyLong(), anyList(), anyLong()))
            .thenReturn(OperationResult.success("Pedido creado"));

        // Act
        String vista = controller.crearPedido(1L, 1L, 1L, 1L, 2, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/web/pedidos", vista);
        verify(pedidoService).crearPedido(anyLong(), anyLong(), anyList(), anyLong());
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void crearPedido_SinUsuarioId_DebeMostrarError() {
        // Act
        String vista = controller.crearPedido(null, 1L, 1L, 1L, 2, model, redirectAttributes);

        // Assert
        assertEquals("pedido/create", vista);
        verify(model).addAttribute(eq("errorMessage"), eq("El ID de usuario es obligatorio"));
    }

    @Test
    void crearPedido_SinDireccionId_DebeMostrarError() {
        // Act
        String vista = controller.crearPedido(1L, null, 1L, 1L, 2, model, redirectAttributes);

        // Assert
        assertEquals("pedido/create", vista);
        verify(model).addAttribute(eq("errorMessage"), eq("El ID de dirección es obligatorio"));
    }

    @Test
    void crearPedido_SinProductoId_DebeMostrarError() {
        // Act
        String vista = controller.crearPedido(1L, 1L, 1L, null, 2, model, redirectAttributes);

        // Assert
        assertEquals("pedido/create", vista);
        verify(model).addAttribute(eq("errorMessage"), eq("El ID de producto es obligatorio"));
    }

    @Test
    void crearPedido_SinCantidad_DebeMostrarError() {
        // Act
        String vista = controller.crearPedido(1L, 1L, 1L, 1L, null, model, redirectAttributes);

        // Assert
        assertEquals("pedido/create", vista);
        verify(model).addAttribute(eq("errorMessage"), eq("La cantidad es obligatoria"));
    }

    @Test
    void crearPedido_ConCantidadCero_DebeMostrarError() {
        // Act
        String vista = controller.crearPedido(1L, 1L, 1L, 1L, 0, model, redirectAttributes);

        // Assert
        assertEquals("pedido/create", vista);
        verify(model).addAttribute(eq("errorMessage"), eq("La cantidad debe ser mayor a 0"));
    }

    @Test
    void crearPedido_ConCantidadMayor999_DebeMostrarError() {
        // Act
        String vista = controller.crearPedido(1L, 1L, 1L, 1L, 1000, model, redirectAttributes);

        // Assert
        assertEquals("pedido/create", vista);
        verify(model).addAttribute(eq("errorMessage"), eq("La cantidad no puede exceder 999 unidades"));
    }

    @Test
    void crearPedido_ConUsuarioNoExistente_DebeMostrarError() {
        // Arrange
        when(usuarioService.buscarPorId(999L)).thenReturn(Optional.empty());

        // Act
        String vista = controller.crearPedido(999L, 1L, 1L, 1L, 2, model, redirectAttributes);

        // Assert
        assertEquals("pedido/create", vista);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void eliminar_ConPedidoExistente_DebeRedirigir() {
        // Arrange
        Pedido pedido = new Pedido();
        when(pedidoService.obtenerPorId(1L)).thenReturn(pedido);

        // Act
        String vista = controller.eliminar(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/web/pedidos", vista);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void eliminar_ConPedidoNoExistente_DebeMostrarError() {
        // Arrange
        when(pedidoService.obtenerPorId(999L)).thenReturn(null);

        // Act
        String vista = controller.eliminar(999L, redirectAttributes);

        // Assert
        assertEquals("redirect:/web/pedidos", vista);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}