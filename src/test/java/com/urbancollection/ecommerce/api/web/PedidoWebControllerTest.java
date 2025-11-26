package com.urbancollection.ecommerce.api.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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

import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.domain.enums.EstadoDePedido;
import com.urbancollection.ecommerce.persistence.jpa.spring.CuponJpaRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.PedidoJpaRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.ProductoJpaRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.UsuarioJpaRepository;

class PedidoWebControllerTest {

    @Mock private PedidoJpaRepository pedidoRepository;
    @Mock private UsuarioJpaRepository usuarioRepository;
    @Mock private ProductoJpaRepository productoRepository;
    @Mock private CuponJpaRepository cuponRepository;
    @Mock private Model model;
    @Mock private RedirectAttributes redirectAttributes;

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
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        // Act
        String vista = controller.listar(model);

        // Assert
        assertEquals("pedido/list", vista);
        verify(pedidoRepository).findAll();
        verify(model).addAttribute(eq("pedidos"), anyList());
    }

    @Test
    void listar_ConError_DebeMostrarMensajeError() {
        // Arrange
        when(pedidoRepository.findAll()).thenThrow(new RuntimeException("Error de prueba"));

        // Act
        String vista = controller.listar(model);

        // Assert
        assertEquals("pedido/list", vista);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void ver_ConPedidoExistente_DebeRetornarVista() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act
        String vista = controller.ver(1L, model, redirectAttributes);

        // Assert
        assertEquals("pedido/detail", vista);
        verify(pedidoRepository).findById(1L);
        verify(model).addAttribute(eq("pedido"), any(Pedido.class));
    }

    @Test
    void ver_ConPedidoNoExistente_DebeRedirigir() {
        // Arrange
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        String vista = controller.ver(999L, model, redirectAttributes);

        // Assert
        assertEquals("redirect:/web/pedidos", vista);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void mostrarFormularioCrear_DebeRetornarVistaCreate() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(List.of());
        when(productoRepository.findAll()).thenReturn(List.of());
        when(cuponRepository.findAll()).thenReturn(List.of());

        // Act
        String vista = controller.mostrarFormularioCrear(model);

        // Assert
        assertEquals("pedido/create", vista);
        verify(model).addAttribute(eq("usuarios"), anyList());
        verify(model).addAttribute(eq("productos"), anyList());
        verify(model).addAttribute(eq("cupones"), anyList());
    }

    @Test
    void crear_ConDatosValidos_DebeCrearYRedirigir() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setPrecio(new BigDecimal("100.00"));
        producto.setStock(10);
        
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(i -> i.getArguments()[0]);

        List<Long> productosIds = List.of(1L);
        List<Integer> cantidades = List.of(2);

        // Act
        String vista = controller.crear(1L, null, productosIds, cantidades, "TARJETA", model, redirectAttributes);

        // Assert
        assertEquals("redirect:/web/pedidos", vista);
        verify(pedidoRepository).save(any(Pedido.class));
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void crear_SinUsuarioId_DebeMostrarError() {
        // Act
        String vista = controller.crear(null, null, List.of(1L), List.of(2), "TARJETA", model, redirectAttributes);

        // Assert
        assertEquals("pedido/create", vista);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void crear_SinProductos_DebeMostrarError() {
        // Act
        String vista = controller.crear(1L, null, null, List.of(2), "TARJETA", model, redirectAttributes);

        // Assert
        assertEquals("pedido/create", vista);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void crear_ConUsuarioNoExistente_DebeMostrarError() {
        // Arrange
        when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());
        when(usuarioRepository.findAll()).thenReturn(List.of());
        when(productoRepository.findAll()).thenReturn(List.of());
        when(cuponRepository.findAll()).thenReturn(List.of());

        // Act
        String vista = controller.crear(999L, null, List.of(1L), List.of(2), "TARJETA", model, redirectAttributes);

        // Assert
        assertEquals("pedido/create", vista);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void cambiarEstado_ConEstadoValido_DebeActualizar() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(EstadoDePedido.PENDIENTE_PAGO);
        
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Act
        String vista = controller.cambiarEstado(1L, "PAGADO", redirectAttributes);

        // Assert
        assertEquals("redirect:/web/pedidos/1", vista);
        verify(pedidoRepository).save(any(Pedido.class));
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void cambiarEstado_SinEstado_DebeMostrarError() {
        // Arrange
        Pedido pedido = new Pedido();
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act
        String vista = controller.cambiarEstado(1L, null, redirectAttributes);

        // Assert
        assertEquals("redirect:/web/pedidos/1", vista);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void eliminar_ConPedidoExistente_DebeEliminar() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

        // Act
        String vista = controller.eliminar(1L, redirectAttributes);

        // Assert
        assertEquals("redirect:/web/pedidos", vista);
        verify(pedidoRepository).deleteById(1L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void eliminar_ConPedidoNoExistente_DebeMostrarError() {
        // Arrange
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        String vista = controller.eliminar(999L, redirectAttributes);

        // Assert
        assertEquals("redirect:/web/pedidos", vista);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}