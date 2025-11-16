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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    @Transactional(readOnly = true)
    @GetMapping("/web/pedidos")
    public String listar(Model model,
                        @RequestParam(value = "success", required = false) String successMessage) {
        try {
            List<Pedido> pedidos = pedidoService.listarTodos();
            List<PedidoResponse> responses = pedidos.stream()
                    .map(PedidoMapper::toResponse)
                    .toList();

            model.addAttribute("pedidos", responses);
            
            // Agregar mensaje de éxito si existe
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
    // FORMULARIO GET /web/pedidos/create
    // =========================
    @GetMapping("/web/pedidos/create")
    public String mostrarFormularioCrear(Model model) {
        // Inicializar valores por defecto para evitar errores en el formulario
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

        // =========================
        // VALIDACIONES DE BACKEND
        // =========================

        // 1. Validar que todos los campos obligatorios estén presentes
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

        // 2. Validar rangos de valores
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

        // 3. Validar cupón si fue proporcionado
        if (cuponId != null && cuponId <= 0) {
            return mostrarErrorFormulario(model, "El ID de cupón debe ser mayor a 0 o dejarlo vacío", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        // 4. Validar que el usuario exista
        if (!usuarioService.buscarPorId(usuarioId).isPresent()) {
            return mostrarErrorFormulario(model, "El usuario con ID " + usuarioId + " no existe", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        // 5. Validar que la dirección exista
        if (direccionRepository.findById(direccionId) == null) {
            return mostrarErrorFormulario(model, "La dirección con ID " + direccionId + " no existe", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        // 6. Validar que el producto exista
        if (!productoService.buscarPorId(productoId).isPresent()) {
            return mostrarErrorFormulario(model, "El producto con ID " + productoId + " no existe", 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }

        // =========================
        // CREAR EL PEDIDO
        // =========================

        try {
            // Armamos items para el pedido
            List<ItemPedido> items = new ArrayList<>();

            ItemPedido item = new ItemPedido();
            Producto producto = new Producto();
            producto.setId(productoId);
            item.setProducto(producto);
            item.setCantidad(cantidad);
            // Precio simbólico (idealmente debería obtenerse del producto real)
            item.setPrecioUnitario(BigDecimal.valueOf(100));

            items.add(item);

            // Crear el pedido usando el servicio
            OperationResult result = pedidoService.crearPedido(
                    usuarioId,
                    direccionId,
                    items,
                    cuponId
            );

            if (result.isSuccess()) {
                // Redirigir al listado con mensaje de éxito usando flash attribute
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✓ Pedido creado exitosamente. " + result.getMessage());
                return "redirect:/web/pedidos";
            } else {
                // Mostrar error del servicio
                return mostrarErrorFormulario(model, result.getMessage(), 
                                             usuarioId, direccionId, cuponId, productoId, cantidad);
            }

        } catch (Exception e) {
            return mostrarErrorFormulario(model, "Error al crear el pedido: " + e.getMessage(), 
                                         usuarioId, direccionId, cuponId, productoId, cantidad);
        }
    }

    /**
     * Método auxiliar para mostrar errores en el formulario
     */
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