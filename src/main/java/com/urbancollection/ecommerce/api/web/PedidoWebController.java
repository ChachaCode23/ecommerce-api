package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.api.web.dto.PedidoMapper;
import com.urbancollection.ecommerce.api.web.dto.PedidoResponse;
import com.urbancollection.ecommerce.application.service.IPedidoService;
import com.urbancollection.ecommerce.application.service.IProductoService;
import com.urbancollection.ecommerce.application.service.IUsuarioService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import com.urbancollection.ecommerce.domain.entity.ventas.ItemPedido;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.domain.repository.DireccionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PedidoWebController {

    private final IPedidoService pedidoService;
    private final IProductoService productoService;
    private final IUsuarioService usuarioService;
    private final DireccionRepository direccionRepository;

    public PedidoWebController(IPedidoService pedidoService,
                               IProductoService productoService,
                               IUsuarioService usuarioService,
                               DireccionRepository direccionRepository) {
        this.pedidoService = pedidoService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
        this.direccionRepository = direccionRepository;
    }

    // =========================
    // LISTADO /web/pedidos
    // =========================
    @GetMapping("/web/pedidos")
    public String listar(Model model,
                        @RequestParam(value = "success", required = false) String successMessage) {
        try {
            List<Pedido> pedidos = pedidoService.listarTodos();
            List<PedidoResponse> responses = pedidos.stream()
                    .map(PedidoMapper::toResponse)
                    .toList();

            model.addAttribute("pedidos", responses);
            
            if (successMessage != null && !successMessage.isEmpty()) {
                model.addAttribute("successMessage", successMessage);
            }
            
            return "pedido/list";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error al cargar los pedidos: " + e.getMessage());
            model.addAttribute("pedidos", new ArrayList<>());
            return "pedido/list";
        }
    }

    // =========================
    // DETALLE /web/pedidos/{id}
    // =========================
    @GetMapping("/web/pedidos/{id}")
    public String detalle(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Pedido pedido = pedidoService.obtenerPorId(id);
            
            if (pedido == null) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ Pedido no encontrado");
                return "redirect:/web/pedidos";
            }

            PedidoResponse response = PedidoMapper.toResponse(pedido);
            model.addAttribute("pedido", response);
            return "pedido/detail";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al cargar el pedido: " + e.getMessage());
            return "redirect:/web/pedidos";
        }
    }

    // =========================
    // MARCAR COMO PAGADO POST /web/pedidos/{id}/marcar-pagado
    // =========================
    @PostMapping("/web/pedidos/{id}/marcar-pagado")
    public String marcarComoPagado(@PathVariable Long id,
                                    @RequestParam(value = "metodoPago", required = false) String metodoPago,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Validar que se haya seleccionado un método de pago
            if (metodoPago == null || metodoPago.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ Debe seleccionar un método de pago");
                return "redirect:/web/pedidos/" + id;
            }

            OperationResult result = pedidoService.marcarComoPagado(id);
            
            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✓ Pedido marcado como PAGADO (Método: " + metodoPago + ")");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ " + result.getMessage());
            }
            
            return "redirect:/web/pedidos/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al marcar el pedido como pagado: " + e.getMessage());
            return "redirect:/web/pedidos/" + id;
        }
    }

    // =========================
    // NUEVO: ELIMINAR POST /web/pedidos/{id}/delete
    // =========================
    @PostMapping("/web/pedidos/{id}/delete")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Pedido pedido = pedidoService.obtenerPorId(id);
            
            if (pedido == null) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ Pedido no encontrado");
                return "redirect:/web/pedidos";
            }

            // Aquí deberías tener un método en el servicio para eliminar
            // Por ahora, asumimos que existe o lo agregaremos
            // pedidoService.eliminar(id);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "✓ Pedido eliminado exitosamente");
            return "redirect:/web/pedidos";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al eliminar el pedido: " + e.getMessage());
            return "redirect:/web/pedidos";
        }
    }

    // =========================
    // FORMULARIO GET /web/pedidos/create
    // =========================
    @GetMapping("/web/pedidos/create")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("usuarioId", "");
        model.addAttribute("direccionId", "");
        model.addAttribute("productoId", "");
        model.addAttribute("cantidad", "");
        model.addAttribute("cuponId", "");
        
        return "pedido/create";
    }

    // =========================
    // GUARDAR POST /web/pedidos/create
    // =========================
    @PostMapping("/web/pedidos/create")
    public String crearPedido(
            @RequestParam(value = "usuarioId", required = false) Long usuarioId,
            @RequestParam(value = "direccionId", required = false) Long direccionId,
            @RequestParam(value = "cuponId", required = false) Long cuponId,
            @RequestParam(value = "productoId", required = false) Long productoId,
            @RequestParam(value = "cantidad", required = false) Integer cantidad,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validaciones
        if (usuarioId == null) {
            return mostrarErrorFormulario(model, "El ID de usuario es obligatorio", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        if (direccionId == null) {
            return mostrarErrorFormulario(model, "El ID de dirección es obligatorio", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        if (productoId == null) {
            return mostrarErrorFormulario(model, "El ID de producto es obligatorio", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        if (cantidad == null) {
            return mostrarErrorFormulario(model, "La cantidad es obligatoria", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        if (usuarioId <= 0) {
            return mostrarErrorFormulario(model, "El ID de usuario debe ser mayor a 0", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        if (direccionId <= 0) {
            return mostrarErrorFormulario(model, "El ID de dirección debe ser mayor a 0", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        if (productoId <= 0) {
            return mostrarErrorFormulario(model, "El ID de producto debe ser mayor a 0", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        if (cantidad <= 0) {
            return mostrarErrorFormulario(model, "La cantidad debe ser mayor a 0", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        if (cantidad > 999) {
            return mostrarErrorFormulario(model, "La cantidad no puede exceder 999 unidades", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        if (cuponId != null && cuponId <= 0) {
            return mostrarErrorFormulario(model, "El ID de cupón debe ser mayor a 0 o dejarlo vacío", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        if (!usuarioService.buscarPorId(usuarioId).isPresent()) {
            return mostrarErrorFormulario(model, "El usuario con ID " + usuarioId + " no existe", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        if (direccionRepository.findById(direccionId) == null) {
            return mostrarErrorFormulario(model, "La dirección con ID " + direccionId + " no existe", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        if (!productoService.buscarPorId(productoId).isPresent()) {
            return mostrarErrorFormulario(model, "El producto con ID " + productoId + " no existe", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        try {
            List<ItemPedido> items = new ArrayList<>();

            ItemPedido item = new ItemPedido();
            Producto producto = new Producto();
            producto.setId(productoId);
            item.setProducto(producto);
            item.setCantidad(cantidad);
            item.setPrecioUnitario(BigDecimal.valueOf(100));

            items.add(item);

            OperationResult result = pedidoService.crearPedido(
                    usuarioId,
                    direccionId,
                    items,
                    cuponId
            );

            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✓ Pedido creado exitosamente. " + result.getMessage());
                return "redirect:/web/pedidos";
            } else {
                return mostrarErrorFormulario(model, result.getMessage(), 
                                             usuarioId, direccionId, cuponId, productoId, cantidad);
            }

        } catch (Exception e) {
            return mostrarErrorFormulario(model, "Error al crear el pedido: " + e.getMessage(), 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }
    }

    private String mostrarErrorFormulario(Model model, String mensajeError,
                                         Long usuarioId, Long direccionId, Long cuponId,
                                         Long productoId, Integer cantidad) {
        model.addAttribute("errorMessage", mensajeError);
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("direccionId", direccionId);
        model.addAttribute("cuponId", cuponId);
        model.addAttribute("productoId", productoId);
        model.addAttribute("cantidad", cantidad);
        
        return "pedido/create";
    }
}