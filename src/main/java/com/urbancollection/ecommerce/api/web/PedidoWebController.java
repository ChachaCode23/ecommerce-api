package com.urbancollection.ecommerce.api.web;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.urbancollection.ecommerce.domain.entity.catalogo.Cupon;
import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import com.urbancollection.ecommerce.domain.entity.ventas.ItemPedido;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.domain.enums.EstadoDePedido;
import com.urbancollection.ecommerce.domain.enums.MetodoDePago; // 👈 CORRECCIÓN: Nueva Importación
import com.urbancollection.ecommerce.persistence.jpa.spring.CuponJpaRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.PedidoJpaRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.ProductoJpaRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.UsuarioJpaRepository;

// Controlador web para manejar los pedidos desde la interfaz 
@Controller
@RequestMapping("/web/pedidos")
public class PedidoWebController {

    // Repositorio para acceder y manejar los pedidos en la base de datos.
    private final PedidoJpaRepository pedidoRepository;
    // Repositorio para consultar los usuarios que pueden hacer pedidos.
    private final UsuarioJpaRepository usuarioRepository;
    // Repositorio para obtener los productos que se agregan al pedido.
    private final ProductoJpaRepository productoRepository;
    // Repositorio para consultar y aplicar cupones de descuento.
    private final CuponJpaRepository cuponRepository;

    // Constructor donde Spring inyecta todos los repositorios necesarios.
    public PedidoWebController(
            PedidoJpaRepository pedidoRepository,
            UsuarioJpaRepository usuarioRepository,
            ProductoJpaRepository productoRepository,
            CuponJpaRepository cuponRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.cuponRepository = cuponRepository;
    }

    // Acción GET para mostrar el listado de todos los pedidos.
    @GetMapping
    public String listar(Model model) {
        try {
            // Busco todos los pedidos en la base de datos.
            List<Pedido> pedidos = pedidoRepository.findAll();
            // Los agrego al modelo para que la vista los muestre.
            model.addAttribute("pedidos", pedidos);
            return "pedido/list";
        } catch (Exception e) {
            // Si ocurre un error, lo muestro en la vista.
            model.addAttribute("errorMessage", "⚠ Error al cargar los pedidos: " + e.getMessage());
            return "pedido/list";
        }
    }

    // Acción GET para mostrar el formulario de creación de un nuevo pedido.
    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model) {
        try {
            // Cargo todos los usuarios, productos y cupones para llenar los combos del formulario.
            List<Usuario> usuarios = usuarioRepository.findAll();
            List<Producto> productos = productoRepository.findAll();
            List<Cupon> cupones = cuponRepository.findAll();

            // Agrego las listas al modelo para que el formulario las use.
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("productos", productos);
            model.addAttribute("cupones", cupones);
            // Valores por defecto en el formulario.
            model.addAttribute("usuarioId", "");
            model.addAttribute("cuponId", "");
            return "pedido/create";
        } catch (Exception e) {
            // Si falla la carga de datos, redirijo al listado de pedidos con error.
            model.addAttribute("errorMessage", "⚠ Error al cargar el formulario: " + e.getMessage());
            return "redirect:/web/pedidos";
        }
    }

    // Acción POST que recibe los datos del formulario y crea el pedido.
    @PostMapping("/create")
    public String crear(
            @RequestParam(value = "usuarioId", required = false) Long usuarioId,
            @RequestParam(value = "cuponId", required = false) Long cuponId,
            @RequestParam(value = "productosIds", required = false) List<Long> productosIds,
            @RequestParam(value = "cantidades", required = false) List<Integer> cantidades,
            @RequestParam(value = "metodoPago", required = false) String metodoPago,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validación: el usuario es obligatorio.
        if (usuarioId == null) {
            return mostrarError(model, "El usuario es obligatorio", usuarioId, cuponId);
        }

        // Validación: el método de pago también es obligatorio.
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            return mostrarError(model, "El método de pago es obligatorio", usuarioId, cuponId);
        }

        // Validación: debe haber al menos un producto seleccionado.
        if (productosIds == null || productosIds.isEmpty()) {
            return mostrarError(model, "Debe seleccionar al menos un producto", usuarioId, cuponId);
        }

        // Validación: la lista de cantidades debe coincidir con la de productos.
        if (cantidades == null || cantidades.size() != productosIds.size()) {
            return mostrarError(model, "Las cantidades no coinciden con los productos", usuarioId, cuponId);
        }

        try {
            // Busco el usuario que hace el pedido.
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
            if (!usuarioOpt.isPresent()) {
                return mostrarError(model, "Usuario no encontrado", usuarioId, cuponId);
            }

            Usuario usuario = usuarioOpt.get();
            // Creo una instancia de Pedido para ir llenándola.
            Pedido pedido = new Pedido();
            pedido.setUsuario(usuario);
            
            // Si se selecciona método de pago, el pedido se considera pagado.
            pedido.setEstado(EstadoDePedido.PAGADO);

            // Intento convertir el String del método de pago al enum correspondiente.
            try {
                MetodoDePago metodo = MetodoDePago.valueOf(metodoPago.toUpperCase());
                pedido.setMetodoPago(metodo);
            } catch (IllegalArgumentException e) {
                // Si el valor no coincide con el enum, es un método de pago inválido.
                return mostrarError(model, "Método de pago inválido", usuarioId, cuponId);
            }

            // Si el usuario seleccionó un cupón, lo busco y lo asigno al pedido.
            if (cuponId != null) {
                Optional<Cupon> cuponOpt = cuponRepository.findById(cuponId);
                cuponOpt.ifPresent(pedido::setCupon);
            }

            // Empiezo con subtotal en cero y lo iré sumando según los productos.
            BigDecimal subtotal = BigDecimal.ZERO;
            // Recorro todos los productos seleccionados.
            for (int i = 0; i < productosIds.size(); i++) {
                Long productoId = productosIds.get(i);
                Integer cantidad = cantidades.get(i);

                // Si la cantidad no es válida, simplemente la ignoro.
                if (cantidad == null || cantidad <= 0) continue;

                // Busco el producto en base de datos.
                Optional<Producto> productoOpt = productoRepository.findById(productoId);
                if (!productoOpt.isPresent()) continue;

                Producto producto = productoOpt.get();

                // Valido que haya suficiente stock para la cantidad pedida.
                if (producto.getStock() < cantidad) {
                    return mostrarError(model, 
                        "Stock insuficiente para " + producto.getNombre() + 
                        " (disponible: " + producto.getStock() + ")", 
                        usuarioId, cuponId);
                }

                // Creo el item del pedido 
                ItemPedido item = new ItemPedido();
                item.setPedido(pedido);
                item.setProducto(producto);
                item.setCantidad(cantidad);
                item.setPrecioUnitario(producto.getPrecio());

                // Agrego el item a la lista de items del pedido.
                pedido.agregarItem(item);
                // Actualizo el subtotal sumando cantidad * precio.
                subtotal = subtotal.add(producto.getPrecio().multiply(new BigDecimal(cantidad)));
            }

            // Si no se agregó ningún item válido, no tiene sentido crear el pedido.
            if (pedido.getItems().isEmpty()) {
                return mostrarError(model, "No se agregaron productos válidos al pedido", usuarioId, cuponId);
            }

            // Asigno el subtotal calculado.
            pedido.setSubtotal(subtotal);
            // Variable para ir calculando el descuento final.
            BigDecimal descuento = BigDecimal.ZERO;

            // Si el pedido tiene cupón, calculo el descuento correspondiente.
            if (pedido.getCupon() != null) {
                Cupon cupon = pedido.getCupon();
                if (cupon.isActivo()) {
                    // Dependiendo del tipo de cupón, calculo el descuento.
                    switch (cupon.getTipo()) {
                        case PORCENTAJE:
                            // Descuento = subtotal * porcentaje / 100, redondeando a 2 decimales.
                            descuento = subtotal.multiply(cupon.getValorDescuento())
                                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                            // Si hay un tope de descuento, lo respeto.
                            if (cupon.getTopeDescuento() != null && descuento.compareTo(cupon.getTopeDescuento()) > 0) {
                                descuento = cupon.getTopeDescuento();
                            }
                            break;
                        case MONTO_FIJO:
                            // En monto fijo, el descuento es un valor fijo.
                            descuento = cupon.getValorDescuento();
                            // Pero nunca puede ser mayor que el subtotal.
                            if (descuento.compareTo(subtotal) > 0) {
                                descuento = subtotal;
                            }
                            break;
                    }
                }
            }

            // Asigno el descuento calculado.
            pedido.setDescuento(descuento);
            // Total = subtotal - descuento.
            pedido.setTotal(subtotal.subtract(descuento));

            // Guardo el pedido en la base de datos.
            pedidoRepository.save(pedido);

            // Actualizo el stock de cada producto según las cantidades vendidas.
            for (ItemPedido item : pedido.getItems()) {
                Producto producto = item.getProducto();
                producto.setStock(producto.getStock() - item.getCantidad());
                productoRepository.save(producto);
            }

            // Mensaje de éxito al crear el pedido.
            redirectAttributes.addFlashAttribute("successMessage", 
                "✓ Pedido creado exitosamente");
            return "redirect:/web/pedidos";

        } catch (Exception e) {
            // Si algo falla en cualquier parte del proceso, muestro el error.
            return mostrarError(model, "Error al crear el pedido: " + e.getMessage(), 
                              usuarioId, cuponId);
        }
    }

    // Acción GET para ver el detalle de un pedido específico.
    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Busco el pedido por su id.
            Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
            
            // Si no existe, redirijo al listado con mensaje de error.
            if (!pedidoOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ Pedido no encontrado");
                return "redirect:/web/pedidos";
            }

            // Si existe, lo agrego al modelo para que la vista muestre el detalle.
            model.addAttribute("pedido", pedidoOpt.get());
            return "pedido/detail";

        } catch (Exception e) {
            // Manejo de errores al cargar el detalle.
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al cargar el pedido: " + e.getMessage());
            return "redirect:/web/pedidos";
        }
    }

    // Acción POST para cambiar el estado de un pedido.
    @PostMapping("/{id}/estado")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam(value = "estado", required = false) String estado,
            RedirectAttributes redirectAttributes) {

        try {
            // Busco el pedido en la base de datos.
            Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
            
            // Si no existe, aviso y redirijo.
            if (!pedidoOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ Pedido no encontrado");
                return "redirect:/web/pedidos";
            }

            Pedido pedido = pedidoOpt.get();

            // Valido que se haya enviado un estado.
            if (estado == null || estado.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ El estado es obligatorio");
                return "redirect:/web/pedidos/" + id;
            }

            try {
                // Intento convertir el String a enum de EstadoDePedido.
                EstadoDePedido nuevoEstado = EstadoDePedido.valueOf(estado);
                pedido.setEstado(nuevoEstado);
                // Guardo el nuevo estado del pedido.
                pedidoRepository.save(pedido);

                redirectAttributes.addFlashAttribute("successMessage", 
                    "✓ Estado del pedido actualizado exitosamente");
            } catch (IllegalArgumentException e) {
                // Si el estado no coincide con el enum, es inválido.
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ Estado de pedido inválido: " + estado);
            }

            return "redirect:/web/pedidos/" + id;

        } catch (Exception e) {
            // Manejo de errores en el cambio de estado.
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al cambiar el estado: " + e.getMessage());
            return "redirect:/web/pedidos";
        }
    }

    // Acción POST para eliminar un pedido por su id.
    @PostMapping("/{id}/delete")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Busco el pedido antes de eliminarlo.
            Optional<Pedido> pedidoOpt = pedidoRepository.findById(id);
            
            // Si no existe, envío mensaje de error.
            if (!pedidoOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ Pedido no encontrado");
                return "redirect:/web/pedidos";
            }

            Pedido pedido = pedidoOpt.get();

            // Antes de borrar el pedido, devuelvo el stock de los productos.
            for (ItemPedido item : pedido.getItems()) {
                Producto producto = item.getProducto();
                producto.setStock(producto.getStock() + item.getCantidad());
                productoRepository.save(producto);
            }

            // Ahora sí elimino el pedido.
            pedidoRepository.deleteById(id);

            redirectAttributes.addFlashAttribute("successMessage", 
                "✓ Pedido eliminado exitosamente");
            return "redirect:/web/pedidos";

        } catch (Exception e) {
            // Manejo de errores al eliminar.
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al eliminar el pedido: " + e.getMessage());
            return "redirect:/web/pedidos";
        }
    }

    // Método privado de ayuda para centralizar el manejo de errores en la creación.
    private String mostrarError(Model model, String mensaje, Long usuarioId, Long cuponId) {
        // Agrego el mensaje de error al modelo.
        model.addAttribute("errorMessage", mensaje);
        // Mantengo los valores seleccionados para que el usuario no tenga que reingresarlos.
        model.addAttribute("usuarioId", usuarioId);
        model.addAttribute("cuponId", cuponId);

        try {
            // Intento recargar las listas para que el formulario se pueda volver a mostrar.
            model.addAttribute("usuarios", usuarioRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("cupones", cuponRepository.findAll());
        } catch (Exception e) {
            // Si algo falla al cargar las listas, las dejo vacías para evitar errores.
            model.addAttribute("usuarios", List.of());
            model.addAttribute("productos", List.of());
            model.addAttribute("cupones", List.of());
        }

        // Siempre regreso a la vista de creación de pedido.
        return "pedido/create";
    }
}
