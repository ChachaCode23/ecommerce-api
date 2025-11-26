package com.urbancollection.ecommerce.api.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import com.urbancollection.ecommerce.domain.enums.TipoDescuento;
import com.urbancollection.ecommerce.persistence.jpa.spring.CuponJpaRepository;

// Controlador Spring MVC que maneja las peticiones relacionadas con cupones en la parte web.
@Controller
@RequestMapping("/web/cupones")
// Clase del controlador de cupones (capa de presentación que habla con las vistas).
public class CuponWebController {

    // Repositorio JPA que me permite acceder a la tabla de cupones en la base de datos.
    private final CuponJpaRepository cuponRepository;

    // Constructor donde Spring inyecta el repositorio de cupones.
    public CuponWebController(CuponJpaRepository cuponRepository) {
        this.cuponRepository = cuponRepository;
    }

    // Método GET que lista todos los cupones y los manda a la vista.
    @GetMapping
    // Acción que devuelve la vista con la lista de cupones.
    public String listar(Model model) {
        // Uso try-catch para manejar errores en esta operación y no romper la aplicación.
        try {
            model.addAttribute("cupones", cuponRepository.findAll());
            return "cupon/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "⚠ Error: " + e.getMessage());
            return "cupon/list";
        }
    }

    // Muestra el formulario vacío para crear un nuevo cupón.
    @GetMapping("/create")
    // Acción que prepara el modelo con valores por defecto para el formulario de creación.
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("codigo", "");
        model.addAttribute("activo", true);
        model.addAttribute("tipo", "PORCENTAJE");
        model.addAttribute("valorDescuento", "");
        return "cupon/create";
    }

    // Procesa el formulario de creación de un nuevo cupón.
    @PostMapping("/create")
    // Acción que recibe los datos del formulario y crea el cupón si todo está bien.
    public String crear(
            @RequestParam(value = "codigo", required = false) String codigo,
            @RequestParam(value = "activo", required = false, defaultValue = "false") boolean activo,
            @RequestParam(value = "tipo", required = false) String tipo,
            @RequestParam(value = "valorDescuento", required = false) BigDecimal valorDescuento,
            @RequestParam(value = "minimoCompra", required = false) BigDecimal minimoCompra,
            @RequestParam(value = "topeDescuento", required = false) BigDecimal topeDescuento,
            @RequestParam(value = "fechaInicio", required = false) String fechaInicio,
            @RequestParam(value = "fechaFin", required = false) String fechaFin,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validación básica el código del cupón es obligatorio.
        if (codigo == null || codigo.trim().isEmpty()) {
            model.addAttribute("errorMessage", "El código es obligatorio");
            return "cupon/create";
        }

        // Uso try-catch para manejar errores en esta operación y no romper la aplicación.
        try {
            // Verifico si ya existe un cupón con ese mismo código en la base de datos.
            Optional<Cupon> existente = cuponRepository.findByCodigo(codigo.trim().toUpperCase());
            if (existente.isPresent()) {
                model.addAttribute("errorMessage", "Ya existe un cupón con ese código");
                return "cupon/create";
            }

            // Creo una nueva instancia de la entidad Cupon para llenarla con los datos del formulario.
            Cupon cupon = new Cupon();
            // Guardo el código en mayúsculas y sin espacios al inicio o al final.
            cupon.setCodigo(codigo.trim().toUpperCase());
            cupon.setActivo(activo);
            cupon.setTipo(TipoDescuento.valueOf(tipo));
            cupon.setValorDescuento(valorDescuento);
            cupon.setMinimoCompra(minimoCompra);
            cupon.setTopeDescuento(topeDescuento);

            // Si el usuario envía una fecha de inicio, la convierto a LocalDateTime al inicio del día.
            if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
                cupon.setFechaInicio(LocalDateTime.parse(fechaInicio + "T00:00:00"));
            }

            // Si el usuario envía una fecha de fin, la convierto a LocalDateTime al final del día.
            if (fechaFin != null && !fechaFin.trim().isEmpty()) {
                cupon.setFechaFin(LocalDateTime.parse(fechaFin + "T23:59:59"));
            }

            // Finalmente guardo los cambios del cupón en la base de datos.
            cuponRepository.save(cupon);

            redirectAttributes.addFlashAttribute("successMessage", "✓ Cupón creado exitosamente");
            return "redirect:/web/cupones";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
            return "cupon/create";
        }
    }

    // Carga los datos de un cupón existente para mostrarlos en el formulario de edición.
    @GetMapping("/{id}/edit")
    // Acción que busca el cupón por id y lo manda a la vista de edición.
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        // Uso try-catch para manejar errores en esta operación y no romper la aplicación.
        try {
            // Busco el cupón por su id en la base de datos.
            Optional<Cupon> cuponOpt = cuponRepository.findById(id);
            
            // Si no encuentro el cupón, mando un mensaje de error y redirijo al listado.
            if (!cuponOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ Cupón no encontrado");
                return "redirect:/web/cupones";
            }

            model.addAttribute("cupon", cuponOpt.get());
            return "cupon/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "⚠ Error: " + e.getMessage());
            return "redirect:/web/cupones";
        }
    }

    // Procesa el formulario de edición y actualiza el cupón en la base de datos.
    @PostMapping("/{id}/edit")
    // Acción que actualiza un cupón existente con los datos que manda el formulario.
    public String actualizar(
            @PathVariable Long id,
            @RequestParam(value = "codigo", required = false) String codigo,
            @RequestParam(value = "activo", required = false, defaultValue = "false") boolean activo,
            @RequestParam(value = "tipo", required = false) String tipo,
            @RequestParam(value = "valorDescuento", required = false) BigDecimal valorDescuento,
            @RequestParam(value = "minimoCompra", required = false) BigDecimal minimoCompra,
            @RequestParam(value = "topeDescuento", required = false) BigDecimal topeDescuento,
            @RequestParam(value = "fechaInicio", required = false) String fechaInicio,
            @RequestParam(value = "fechaFin", required = false) String fechaFin,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Uso try-catch para manejar errores en esta operación y no romper la aplicación.
        try {
            // Busco el cupón por su id en la base de datos.
            Optional<Cupon> cuponOpt = cuponRepository.findById(id);
            
            // Si no encuentro el cupón, mando un mensaje de error y redirijo al listado.
            if (!cuponOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ Cupón no encontrado");
                return "redirect:/web/cupones";
            }

            Cupon cupon = cuponOpt.get();

            // Verifico si ya existe un cupón con ese mismo código en la base de datos.
            Optional<Cupon> existente = cuponRepository.findByCodigo(codigo.trim().toUpperCase());
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                model.addAttribute("errorMessage", "Ya existe otro cupón con ese código");
                model.addAttribute("cupon", cupon);
                return "cupon/edit";
            }

            // Guardo el código en mayúsculas y sin espacios al inicio o al final.
            cupon.setCodigo(codigo.trim().toUpperCase());
            cupon.setActivo(activo);
            cupon.setTipo(TipoDescuento.valueOf(tipo));
            cupon.setValorDescuento(valorDescuento);
            cupon.setMinimoCompra(minimoCompra);
            cupon.setTopeDescuento(topeDescuento);

            // Si el usuario envía una fecha de inicio, la convierto a LocalDateTime al inicio del día.
            if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
                cupon.setFechaInicio(LocalDateTime.parse(fechaInicio + "T00:00:00"));
            } else {
                cupon.setFechaInicio(null);
            }

            // Si el usuario envía una fecha de fin, la convierto a LocalDateTime al final del día.
            if (fechaFin != null && !fechaFin.trim().isEmpty()) {
                cupon.setFechaFin(LocalDateTime.parse(fechaFin + "T23:59:59"));
            } else {
                cupon.setFechaFin(null);
            }

            // Finalmente guardo los cambios del cupón en la base de datos.
            cuponRepository.save(cupon);

            redirectAttributes.addFlashAttribute("successMessage", "✓ Cupón actualizado exitosamente");
            return "redirect:/web/cupones";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "⚠ Error: " + e.getMessage());
            return "redirect:/web/cupones";
        }
    }

    // Maneja la petición para eliminar un cupón según su id.
    @PostMapping("/{id}/delete")
    // Acción que elimina el cupón de la base de datos.
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Uso try-catch para manejar errores en esta operación y no romper la aplicación.
        try {
            // Antes de borrar, verifico que el cupón exista realmente.
            if (!cuponRepository.existsById(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ Cupón no encontrado");
                return "redirect:/web/cupones";
            }

            cuponRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "✓ Cupón eliminado exitosamente");
            return "redirect:/web/cupones";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "⚠ Error: " + e.getMessage());
            return "redirect:/web/cupones";
        }
    }
}
