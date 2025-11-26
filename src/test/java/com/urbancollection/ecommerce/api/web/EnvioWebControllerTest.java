package com.urbancollection.ecommerce.api.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

import com.urbancollection.ecommerce.domain.entity.logistica.Envio;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.domain.enums.EstadoDePedido;
import com.urbancollection.ecommerce.persistence.jpa.spring.EnvioJpaRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.PedidoJpaRepository;

class EnvioWebControllerTest {

    @Mock private EnvioJpaRepository envioRepository;
    @Mock private PedidoJpaRepository pedidoRepository;
    @Mock private Model model;
    @Mock private RedirectAttributes redirectAttributes;

    @InjectMocks
    private EnvioWebController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listar_DebeRetornarVistaEnvioList() {
        List<Envio> envios = new ArrayList<>();
        when(envioRepository.findAll()).thenReturn(envios);

        String vista = controller.listar(model);

        assertEquals("envio/list", vista);
        verify(envioRepository).findAll();
        verify(model).addAttribute("envios", envios);
    }

    @Test
    void listar_ConError_DebeMostrarMensajeError() {
        when(envioRepository.findAll()).thenThrow(new RuntimeException("Error"));

        String vista = controller.listar(model);

        assertEquals("envio/list", vista);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void mostrarFormularioCrear_DebeRetornarVistaCreate() {
        List<Pedido> pedidos = new ArrayList<>();
        Pedido pedido = new Pedido();
        pedido.setEstado(EstadoDePedido.PAGADO);
        pedido.setId(1L);
        pedidos.add(pedido);
        
        when(pedidoRepository.findAll()).thenReturn(pedidos);
        when(envioRepository.findAll()).thenReturn(new ArrayList<>());

        String vista = controller.mostrarFormularioCrear(model);

        assertEquals("envio/create", vista);
        verify(model).addAttribute(eq("pedidos"), any(List.class));
    }

    @Test
    void crear_ConDatosValidos_DebeCrearYRedirigir() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setEstado(EstadoDePedido.PAGADO);
        
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
        when(envioRepository.findByPedidoId(1L)).thenReturn(Optional.empty());
        when(envioRepository.findByTracking("TRACK123")).thenReturn(Optional.empty());
        when(envioRepository.save(any(Envio.class))).thenReturn(new Envio());

        String vista = controller.crear(1L, "TRACK123", "PENDIENTE", model, redirectAttributes);

        assertEquals("redirect:/web/envios", vista);
        verify(envioRepository).save(any(Envio.class));
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void crear_SinPedidoId_DebeMostrarError() {
        String vista = controller.crear(null, "TRACK123", "PENDIENTE", model, redirectAttributes);

        assertEquals("envio/create", vista);
        verify(model).addAttribute(eq("errorMessage"), eq("El pedido es obligatorio"));
    }

    @Test
    void crear_SinTracking_DebeMostrarError() {
        String vista = controller.crear(1L, null, "PENDIENTE", model, redirectAttributes);

        assertEquals("envio/create", vista);
        verify(model).addAttribute(eq("errorMessage"), eq("El tracking es obligatorio"));
    }

    @Test
    void crear_ConPedidoNoExistente_DebeMostrarError() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.empty());
        
        String vista = controller.crear(1L, "TRACK123", "PENDIENTE", model, redirectAttributes);

        assertEquals("envio/create", vista);
        verify(model).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void mostrarFormularioEditar_ConEnvioExistente_DebeRetornarVista() {
        Envio envio = new Envio();
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));

        String vista = controller.mostrarFormularioEditar(1L, model, redirectAttributes);

        assertEquals("envio/edit", vista);
        verify(model).addAttribute("envio", envio);
    }

    @Test
    void mostrarFormularioEditar_ConEnvioNoExistente_DebeRedirigir() {
        when(envioRepository.findById(999L)).thenReturn(Optional.empty());

        String vista = controller.mostrarFormularioEditar(999L, model, redirectAttributes);

        assertEquals("redirect:/web/envios", vista);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void actualizar_ConDatosValidos_DebeActualizarYRedirigir() {
        Envio envio = new Envio();
        Pedido pedido = new Pedido();
        envio.setPedido(pedido);
        
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envio));
        when(envioRepository.findByTracking("TRACK456")).thenReturn(Optional.empty());
        when(envioRepository.save(any(Envio.class))).thenReturn(envio);

        String vista = controller.actualizar(1L, "TRACK456", "EN_TRANSITO", model, redirectAttributes);

        assertEquals("redirect:/web/envios", vista);
        verify(envioRepository).save(any(Envio.class));
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void actualizar_ConEnvioNoExistente_DebeRedirigir() {
        when(envioRepository.findById(1L)).thenReturn(Optional.empty());

        String vista = controller.actualizar(1L, "TRACK456", "PENDIENTE", model, redirectAttributes);

        assertEquals("redirect:/web/envios", vista);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    void eliminar_ConEnvioExistente_DebeEliminarYRedirigir() {
        when(envioRepository.existsById(1L)).thenReturn(true);

        String vista = controller.eliminar(1L, redirectAttributes);

        assertEquals("redirect:/web/envios", vista);
        verify(envioRepository).deleteById(1L);
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    void eliminar_ConError_DebeMostrarMensajeError() {
        when(envioRepository.existsById(1L)).thenReturn(false);

        String vista = controller.eliminar(1L, redirectAttributes);

        assertEquals("redirect:/web/envios", vista);
        verify(redirectAttributes).addFlashAttribute(eq("errorMessage"), anyString());
    }
}