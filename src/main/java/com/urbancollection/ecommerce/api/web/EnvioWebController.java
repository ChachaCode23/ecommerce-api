package com.urbancollection.ecommerce.api.web;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.urbancollection.ecommerce.domain.entity.logistica.Envio;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.domain.enums.EstadoDeEnvio;
import com.urbancollection.ecommerce.domain.enums.EstadoDePedido;
import com.urbancollection.ecommerce.persistence.jpa.spring.EnvioJpaRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.PedidoJpaRepository;

// Controlador web para manejar todo lo relacionado con los envíos en la parte de vistas 
@Controller
@RequestMapping("/web/envios")
public class EnvioWebController {

    // Repositorio para acceder a la tabla de envíos en la base de datos.
    private final EnvioJpaRepository envioRepository;
    // Repositorio para acceder a los pedidos, ya que un envío siempre está asociado a un pedido.
    private final PedidoJpaRepository pedidoRepository;

    // Constructor donde Spring inyecta los dos repositorios que vamos a usar.
    public EnvioWebController(EnvioJpaRepository envioRepository, PedidoJpaRepository pedidoRepository) {
        this.envioRepository = envioRepository;
        this.pedidoRepository = pedidoRepository;
    }

    // Método GET para mostrar el listado de todos los envíos.
    @GetMapping
    public String listar(Model model) {
        try {
            // Consulto todos los envíos y los mando al modelo para que la vista los muestre.
            model.addAttribute("envios", envioRepository.findAll());
            return "envio/list";
        } catch (Exception e) {
            // Si algo sale mal, mando un mensaje de error a la vista.
            model.addAttribute("errorMessage", "⚠ Error: " + e.getMessage());
            return "envio/list";
        }
    }

    // Método GET que muestra el formulario para crear un nuevo envío.
    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model) {
        try {
            // Traigo todos los pedidos de la base de datos.
            List<Pedido> todosPedidos = pedidoRepository.findAll();
            // Traigo todos los envíos de la base de datos.
            List<Envio> todosEnvios = envioRepository.findAll();
            
            // De la lista de envíos, saco los ids de los pedidos que ya tienen envío.
            List<Long> pedidosConEnvio = todosEnvios.stream()
                    .map(e -> e.getPedido().getId())
                    .collect(Collectors.toList());
            
            // Filtro los pedidos que están PAGADOS y además todavía no tienen envío.
            List<Pedido> pedidosDisponibles = todosPedidos.stream()
                    .filter(p -> p.getEstado() == EstadoDePedido.PAGADO)
                    .filter(p -> !pedidosConEnvio.contains(p.getId()))
                    .collect(Collectors.toList());
            
            // Mando la lista de pedidos disponibles al modelo para que el usuario los seleccione.
            model.addAttribute("pedidos", pedidosDisponibles);
            // Valores por defecto para el formulario.
            model.addAttribute("pedidoId", "");
            model.addAttribute("tracking", "");
            model.addAttribute("estado", "PENDIENTE");
            return "envio/create";
        } catch (Exception e) {
            // Si hay error, redirijo al listado con un mensaje de error.
            model.addAttribute("errorMessage", "⚠ Error: " + e.getMessage());
            return "redirect:/web/envios";
        }
    }

    // Método POST que procesa el formulario de creación de un nuevo envío.
    @PostMapping("/create")
    public String crear(
            @RequestParam(value = "pedidoId", required = false) Long pedidoId,
            @RequestParam(value = "tracking", required = false) String tracking,
            @RequestParam(value = "estado", required = false) String estado,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validación básica: el pedido es obligatorio.
        if (pedidoId == null) {
            model.addAttribute("errorMessage", "El pedido es obligatorio");
            return "envio/create";
        }

        // Validacion, el tracking es obligatorio.
        if (tracking == null || tracking.trim().isEmpty()) {
            model.addAttribute("errorMessage", "El tracking es obligatorio");
            return "envio/create";
        }

        try {
            // Busco el pedido por id para asegurarme de que exista.
            Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
            if (!pedidoOpt.isPresent()) {
                model.addAttribute("errorMessage", "El pedido no existe");
                return "envio/create";
            }

            // Verifico si ya existe un envío para ese pedido (no debería haber dos envíos para el mismo pedido).
            Optional<Envio> envioExistente = envioRepository.findByPedidoId(pedidoId);
            if (envioExistente.isPresent()) {
                model.addAttribute("errorMessage", "Ya existe un envío para este pedido");
                return "envio/create";
            }

            // Verifico si ya existe un envío con el mismo tracking (tracking debe ser único).
            Optional<Envio> trackingExistente = envioRepository.findByTracking(tracking.trim().toUpperCase());
            if (trackingExistente.isPresent()) {
                model.addAttribute("errorMessage", "Ya existe un envío con ese tracking");
                return "envio/create";
            }

            // Si las validaciones pasan, creo un nuevo objeto Envio.
            Envio envio = new Envio();
            // Asigno el pedido encontrado al envío.
            envio.setPedido(pedidoOpt.get());
            // Guardo el tracking en mayúsculas para mantener un formato uniforme.
            envio.setTracking(tracking.trim().toUpperCase());
            // Convierto el string del estado al enum correspondiente.
            envio.setEstado(EstadoDeEnvio.valueOf(estado));

            // Guardo el envío en la base de datos.
            envioRepository.save(envio);

            // Mensaje de éxito para mostrar en la vista luego del redirect.
            redirectAttributes.addFlashAttribute("successMessage", "✓ Envío creado exitosamente");
            return "redirect:/web/envios";
        } catch (Exception e) {
            // Si algo falla en el proceso, muestro el mensaje de error y vuelvo al formulario.
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return "envio/create";
        }
    }

    // Método GET que carga el formulario de edición de un envío existente.
    @GetMapping("/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Busco el envío por su id.
            Optional<Envio> envioOpt = envioRepository.findById(id);
            
            // Si no existe, redirijo al listado con un mensaje de error.
            if (!envioOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ Envío no encontrado");
                return "redirect:/web/envios";
            }

            // Si el envío existe, lo mando al modelo para que el formulario lo muestre.
            model.addAttribute("envio", envioOpt.get());
            return "envio/edit";
        } catch (Exception e) {
            // En caso de error, redirijo al listado con mensaje.
            redirectAttributes.addFlashAttribute("errorMessage", "⚠ Error: " + e.getMessage());
            return "redirect:/web/envios";
        }
    }

    // Método POST que actualiza un envío existente con los datos del formulario.
    @PostMapping("/{id}/edit")
    public String actualizar(
            @PathVariable Long id,
            @RequestParam(value = "tracking", required = false) String tracking,
            @RequestParam(value = "estado", required = false) String estado,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // Busco el envío en la base de datos.
            Optional<Envio> envioOpt = envioRepository.findById(id);
            
            // Si no lo encuentro, redirijo al listado.
            if (!envioOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ Envío no encontrado");
                return "redirect:/web/envios";
            }

            Envio envio = envioOpt.get();

            // Verifico que no exista otro envío con el mismo tracking (distinto id).
            Optional<Envio> trackingExistente = envioRepository.findByTracking(tracking.trim().toUpperCase());
            if (trackingExistente.isPresent() && !trackingExistente.get().getId().equals(id)) {
                model.addAttribute("errorMessage", "Ya existe otro envío con ese tracking");
                // Vuelvo a mandar el envío actual al modelo para que el formulario se mantenga.
                model.addAttribute("envio", envio);
                return "envio/edit";
            }

            // Actualizo el tracking del envío actual.
            envio.setTracking(tracking.trim().toUpperCase());
            
            // Convierto el estado que viene como String al enum y lo asigno.
            EstadoDeEnvio nuevoEstado = EstadoDeEnvio.valueOf(estado);
            envio.setEstado(nuevoEstado);
            
            // Si el nuevo estado es ENTREGADO, actualizo también el estado del pedido a COMPLETADO.
            if (nuevoEstado == EstadoDeEnvio.ENTREGADO) {
                Pedido pedido = envio.getPedido();
                pedido.setEstado(EstadoDePedido.COMPLETADO);
                // Guardo primero el pedido con el nuevo estado.
                pedidoRepository.save(pedido);
            }

            // Guardo los cambios del envío.
            envioRepository.save(envio);

            // Mensaje de éxito al actualizar.
            redirectAttributes.addFlashAttribute("successMessage", "✓ Envío actualizado exitosamente");
            return "redirect:/web/envios";
        } catch (Exception e) {
            // Si ocurre un error, lo informo y redirijo al listado.
            redirectAttributes.addFlashAttribute("errorMessage", "⚠ Error: " + e.getMessage());
            return "redirect:/web/envios";
        }
    }

    // Método POST para eliminar un envío por su id.
    @PostMapping("/{id}/delete")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Primero verifico si el envío existe.
            if (!envioRepository.existsById(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ Envío no encontrado");
                return "redirect:/web/envios";
            }

            // Si existe, lo elimino de la base de datos.
            envioRepository.deleteById(id);
            // Mensaje de éxito después de eliminar.
            redirectAttributes.addFlashAttribute("successMessage", "✓ Envío eliminado exitosamente");
            return "redirect:/web/envios";
        } catch (Exception e) {
            // Si ocurre algún error, lo muestro en la vista.
            redirectAttributes.addFlashAttribute("errorMessage", "⚠ Error: " + e.getMessage());
            return "redirect:/web/envios";
        }
    }
}
