package com.urbancollection.ecommerce.api.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.urbancollection.ecommerce.application.service.IEnvioService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.logistica.Envio;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.domain.enums.EstadoDeEnvio;
import com.urbancollection.ecommerce.domain.enums.EstadoDePedido;
import com.urbancollection.ecommerce.domain.repository.PedidoRepository;

@Controller
@RequestMapping("/web/envios")
public class EnvioWebController {

    private final IEnvioService envioService;
    private final PedidoRepository pedidoRepository;

    public EnvioWebController(IEnvioService envioService, PedidoRepository pedidoRepository) {
        this.envioService = envioService;
        this.pedidoRepository = pedidoRepository;
    }

    // =========================
    // LISTADO /web/envios
    // =========================
    @GetMapping
    public String listar(Model model) {
        try {
            List<Envio> envios = envioService.listar();
            model.addAttribute("envios", envios);
            return "envio/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "⚠ Error al cargar los envíos: " + e.getMessage());
            return "envio/list";
        }
    }

    // =========================
    // FORMULARIO CREAR /web/envios/create
    // =========================
    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model) {
        try {
            //  Obtener TODOS los pedidos PAGADOS
            List<Pedido> todosPedidos = pedidoRepository.findAll();
            
            //  Filtrar en memoria los que están PAGADOS y NO tienen envíos
            List<Pedido> pedidosDisponibles = new ArrayList<>();
            for (Pedido p : todosPedidos) {
                if (p.getEstado() == EstadoDePedido.PAGADO) {
                    // Forzar carga de envíos
                    List<Envio> enviosPedido = p.getEnvios();
                    if (enviosPedido.isEmpty()) {
                        pedidosDisponibles.add(p);
                    }
                }
            }
            
            model.addAttribute("pedidos", pedidosDisponibles);
            model.addAttribute("pedidoId", "");
            model.addAttribute("tracking", "");
            model.addAttribute("estado", "PENDIENTE");
            return "envio/create";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "⚠ Error al cargar el formulario: " + e.getMessage());
            return "redirect:/web/envios";
        }
    }

    // =========================
    // GUARDAR POST /web/envios/create
    // =========================
    @PostMapping("/create")
    public String crear(
            @RequestParam(value = "pedidoId", required = false) Long pedidoId,
            @RequestParam(value = "tracking", required = false) String tracking,
            @RequestParam(value = "estado", required = false) String estado,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validaciones
        if (pedidoId == null) {
            return mostrarError(model, "El pedido es obligatorio", 
                              pedidoId, tracking, estado);
        }

        if (tracking == null || tracking.trim().isEmpty()) {
            return mostrarError(model, "El código de tracking es obligatorio", 
                              pedidoId, tracking, estado);
        }

        if (tracking.trim().length() < 5) {
            return mostrarError(model, "El tracking debe tener al menos 5 caracteres", 
                              pedidoId, tracking, estado);
        }

        if (estado == null || estado.trim().isEmpty()) {
            return mostrarError(model, "El estado del envío es obligatorio", 
                              pedidoId, tracking, estado);
        }

        try {
            // Buscar pedido
            Pedido pedido = pedidoRepository.findById(pedidoId);
            if (pedido == null) {
                return mostrarError(model, "El pedido no existe", 
                                  pedidoId, tracking, estado);
            }

            //  Validar que el pedido esté PAGADO
            if (pedido.getEstado() != EstadoDePedido.PAGADO) {
                return mostrarError(model, "Solo se pueden crear envíos para pedidos PAGADOS", 
                                  pedidoId, tracking, estado);
            }

            // Validar que el pedido no tenga envío
            if (pedido.getEnvios() != null && !pedido.getEnvios().isEmpty()) {
                return mostrarError(model, "Este pedido ya tiene un envío asignado", 
                                  pedidoId, tracking, estado);
            }

            // Crear envío
            Envio envio = new Envio();
            envio.setPedido(pedido);
            envio.setTracking(tracking.trim().toUpperCase());
            envio.setEstado(EstadoDeEnvio.valueOf(estado));

            OperationResult result = envioService.crear(envio);

            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✓ Envío creado exitosamente con tracking '" + tracking + "'");
                return "redirect:/web/envios";
            } else {
                return mostrarError(model, result.getMessage(), 
                                  pedidoId, tracking, estado);
            }

        } catch (IllegalArgumentException e) {
            return mostrarError(model, "Estado de envío inválido: " + e.getMessage(), 
                              pedidoId, tracking, estado);
        } catch (Exception e) {
            return mostrarError(model, "Error al crear el envío: " + e.getMessage(), 
                              pedidoId, tracking, estado);
        }
    }

    // =========================
    // FORMULARIO EDITAR /web/envios/{id}/edit
    // =========================
    @GetMapping("/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model,
                                         RedirectAttributes redirectAttributes) {
        try {
            Optional<Envio> envioOpt = envioService.buscarPorId(id);
            
            if (!envioOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ Envío no encontrado");
                return "redirect:/web/envios";
            }

            model.addAttribute("envio", envioOpt.get());
            return "envio/edit";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al cargar el envío: " + e.getMessage());
            return "redirect:/web/envios";
        }
    }

    // =========================
    // ACTUALIZAR POST /web/envios/{id}/edit
    // =========================
    @PostMapping("/{id}/edit")
    public String actualizar(
            @PathVariable Long id,
            @RequestParam(value = "tracking", required = false) String tracking,
            @RequestParam(value = "estado", required = false) String estado,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Optional<Envio> envioOpt = envioService.buscarPorId(id);
            
            if (!envioOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ Envío no encontrado");
                return "redirect:/web/envios";
            }

            Envio envio = envioOpt.get();

            // Validaciones
            if (tracking == null || tracking.trim().isEmpty()) {
                model.addAttribute("errorMessage", "El código de tracking es obligatorio");
                model.addAttribute("envio", envio);
                return "envio/edit";
            }

            if (tracking.trim().length() < 5) {
                model.addAttribute("errorMessage", "El tracking debe tener al menos 5 caracteres");
                model.addAttribute("envio", envio);
                return "envio/edit";
            }

            if (estado == null || estado.trim().isEmpty()) {
                model.addAttribute("errorMessage", "El estado es obligatorio");
                model.addAttribute("envio", envio);
                return "envio/edit";
            }

            // Actualizar campos
            Envio cambios = new Envio();
            cambios.setPedido(envio.getPedido());
            cambios.setTracking(tracking.trim().toUpperCase());
            cambios.setEstado(EstadoDeEnvio.valueOf(estado));

            OperationResult result = envioService.actualizar(id, cambios);

            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✓ Envío actualizado exitosamente");
                return "redirect:/web/envios";
            } else {
                model.addAttribute("errorMessage", result.getMessage());
                model.addAttribute("envio", envio);
                return "envio/edit";
            }

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Estado de envío inválido: " + e.getMessage());
            return "redirect:/web/envios";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al actualizar el envío: " + e.getMessage());
            return "redirect:/web/envios";
        }
    }

    // =========================
    // ELIMINAR POST /web/envios/{id}/delete
    // =========================
    @PostMapping("/{id}/delete")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            OperationResult result = envioService.eliminar(id);
            
            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✓ Envío eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ " + result.getMessage());
            }

            return "redirect:/web/envios";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al eliminar el envío: " + e.getMessage());
            return "redirect:/web/envios";
        }
    }

    // =========================
    // MÉTODO AUXILIAR PARA ERRORES
    // =========================
    private String mostrarError(Model model, String mensaje,
                               Long pedidoId, String tracking, String estado) {
        model.addAttribute("errorMessage", mensaje);
        model.addAttribute("pedidoId", pedidoId);
        model.addAttribute("tracking", tracking);
        model.addAttribute("estado", estado);
        
        try {
            // ✅ Filtrar pedidos disponibles al mostrar error
            List<Pedido> todosPedidos = pedidoRepository.findAll();
            List<Pedido> pedidosDisponibles = todosPedidos.stream()
                .filter(p -> p.getEstado() == EstadoDePedido.PAGADO)
                .filter(p -> p.getEnvios() == null || p.getEnvios().isEmpty())
                .collect(Collectors.toList());
            
            model.addAttribute("pedidos", pedidosDisponibles);
        } catch (Exception e) {
            model.addAttribute("pedidos", List.of());
        }
        
        return "envio/create";
    }
}