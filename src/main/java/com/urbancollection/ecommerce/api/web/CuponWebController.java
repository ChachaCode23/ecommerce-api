package com.urbancollection.ecommerce.api.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

import com.urbancollection.ecommerce.application.service.ICuponService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.catalogo.Cupon;
import com.urbancollection.ecommerce.domain.enums.TipoDescuento;

@Controller
@RequestMapping("/web/cupones")
public class CuponWebController {

    private final ICuponService cuponService;

    public CuponWebController(ICuponService cuponService) {
        this.cuponService = cuponService;
    }

    // =========================
    // LISTADO /web/cupones
    // =========================
    @GetMapping
    public String listar(Model model) {
        try {
            List<Cupon> cupones = cuponService.listar();
            model.addAttribute("cupones", cupones);
            return "cupon/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "⚠ Error al cargar los cupones: " + e.getMessage());
            return "cupon/list";
        }
    }

    // =========================
    // FORMULARIO CREAR /web/cupones/create
    // =========================
    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("codigo", "");
        model.addAttribute("activo", true);
        model.addAttribute("tipo", "PORCENTAJE");
        model.addAttribute("valorDescuento", "");
        model.addAttribute("minimoCompra", "");
        model.addAttribute("topeDescuento", "");
        model.addAttribute("fechaInicio", "");
        model.addAttribute("fechaFin", "");
        return "cupon/create";
    }

    // =========================
    // GUARDAR POST /web/cupones/create
    // =========================
    @PostMapping("/create")
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

        // Validaciones
        if (codigo == null || codigo.trim().isEmpty()) {
            return mostrarError(model, "El código del cupón es obligatorio", 
                              codigo, activo, tipo, valorDescuento, minimoCompra, 
                              topeDescuento, fechaInicio, fechaFin);
        }

        if (codigo.trim().length() < 3) {
            return mostrarError(model, "El código debe tener al menos 3 caracteres", 
                              codigo, activo, tipo, valorDescuento, minimoCompra, 
                              topeDescuento, fechaInicio, fechaFin);
        }

        if (tipo == null || tipo.trim().isEmpty()) {
            return mostrarError(model, "El tipo de descuento es obligatorio", 
                              codigo, activo, tipo, valorDescuento, minimoCompra, 
                              topeDescuento, fechaInicio, fechaFin);
        }

        if (valorDescuento == null) {
            return mostrarError(model, "El valor de descuento es obligatorio", 
                              codigo, activo, tipo, valorDescuento, minimoCompra, 
                              topeDescuento, fechaInicio, fechaFin);
        }

        if (valorDescuento.compareTo(BigDecimal.ZERO) <= 0) {
            return mostrarError(model, "El valor de descuento debe ser mayor a 0", 
                              codigo, activo, tipo, valorDescuento, minimoCompra, 
                              topeDescuento, fechaInicio, fechaFin);
        }

        // Validar porcentaje
        if ("PORCENTAJE".equals(tipo) && valorDescuento.compareTo(new BigDecimal("100")) > 0) {
            return mostrarError(model, "El porcentaje no puede ser mayor a 100", 
                              codigo, activo, tipo, valorDescuento, minimoCompra, 
                              topeDescuento, fechaInicio, fechaFin);
        }

        try {
            Cupon cupon = new Cupon();
            cupon.setCodigo(codigo.trim().toUpperCase());
            cupon.setActivo(activo);
            cupon.setTipo(TipoDescuento.valueOf(tipo));
            cupon.setValorDescuento(valorDescuento);
            cupon.setMinimoCompra(minimoCompra);
            cupon.setTopeDescuento(topeDescuento);

            // Parsear fechas si fueron proporcionadas
            if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
                cupon.setFechaInicio(LocalDateTime.parse(fechaInicio + "T00:00:00"));
            }

            if (fechaFin != null && !fechaFin.trim().isEmpty()) {
                cupon.setFechaFin(LocalDateTime.parse(fechaFin + "T23:59:59"));
            }

            OperationResult result = cuponService.crear(cupon);

            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✓ Cupón '" + codigo + "' creado exitosamente");
                return "redirect:/web/cupones";
            } else {
                return mostrarError(model, result.getMessage(), 
                                  codigo, activo, tipo, valorDescuento, minimoCompra, 
                                  topeDescuento, fechaInicio, fechaFin);
            }

        } catch (Exception e) {
            return mostrarError(model, "Error al crear el cupón: " + e.getMessage(), 
                              codigo, activo, tipo, valorDescuento, minimoCompra, 
                              topeDescuento, fechaInicio, fechaFin);
        }
    }

    // =========================
    // FORMULARIO EDITAR /web/cupones/{id}/edit
    // =========================
    @GetMapping("/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model,
                                         RedirectAttributes redirectAttributes) {
        try {
            Optional<Cupon> cuponOpt = cuponService.buscarPorId(id);
            
            if (!cuponOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ Cupón no encontrado");
                return "redirect:/web/cupones";
            }

            model.addAttribute("cupon", cuponOpt.get());
            return "cupon/edit";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al cargar el cupón: " + e.getMessage());
            return "redirect:/web/cupones";
        }
    }

    // =========================
    // ACTUALIZAR POST /web/cupones/{id}/edit
    // =========================
    @PostMapping("/{id}/edit")
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

        try {
            Optional<Cupon> cuponOpt = cuponService.buscarPorId(id);
            
            if (!cuponOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ Cupón no encontrado");
                return "redirect:/web/cupones";
            }

            Cupon cupon = cuponOpt.get();

            // Validaciones
            if (codigo == null || codigo.trim().isEmpty()) {
                model.addAttribute("errorMessage", "El código del cupón es obligatorio");
                model.addAttribute("cupon", cupon);
                return "cupon/edit";
            }

            if (valorDescuento == null || valorDescuento.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("errorMessage", "El valor de descuento debe ser mayor a 0");
                model.addAttribute("cupon", cupon);
                return "cupon/edit";
            }

            // Actualizar campos
            Cupon cambios = new Cupon();
            cambios.setCodigo(codigo.trim().toUpperCase());
            cambios.setActivo(activo);
            cambios.setTipo(TipoDescuento.valueOf(tipo));
            cambios.setValorDescuento(valorDescuento);
            cambios.setMinimoCompra(minimoCompra);
            cambios.setTopeDescuento(topeDescuento);

            if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
                cambios.setFechaInicio(LocalDateTime.parse(fechaInicio + "T00:00:00"));
            }

            if (fechaFin != null && !fechaFin.trim().isEmpty()) {
                cambios.setFechaFin(LocalDateTime.parse(fechaFin + "T23:59:59"));
            }

            OperationResult result = cuponService.actualizar(id, cambios);

            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✓ Cupón actualizado exitosamente");
                return "redirect:/web/cupones";
            } else {
                model.addAttribute("errorMessage", result.getMessage());
                model.addAttribute("cupon", cupon);
                return "cupon/edit";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al actualizar el cupón: " + e.getMessage());
            return "redirect:/web/cupones";
        }
    }

    // =========================
    // ELIMINAR POST /web/cupones/{id}/delete
    // =========================
    @PostMapping("/{id}/delete")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            OperationResult result = cuponService.eliminar(id);
            
            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✓ Cupón eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ " + result.getMessage());
            }

            return "redirect:/web/cupones";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al eliminar el cupón: " + e.getMessage());
            return "redirect:/web/cupones";
        }
    }

    // =========================
    // MÉTODO AUXILIAR PARA ERRORES
    // =========================
    private String mostrarError(Model model, String mensaje,
                               String codigo, boolean activo, String tipo,
                               BigDecimal valorDescuento, BigDecimal minimoCompra,
                               BigDecimal topeDescuento, String fechaInicio, String fechaFin) {
        model.addAttribute("errorMessage", mensaje);
        model.addAttribute("codigo", codigo);
        model.addAttribute("activo", activo);
        model.addAttribute("tipo", tipo);
        model.addAttribute("valorDescuento", valorDescuento);
        model.addAttribute("minimoCompra", minimoCompra);
        model.addAttribute("topeDescuento", topeDescuento);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
        return "cupon/create";
    }
}