package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.application.service.ProductoService;
import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/web/productos")
public class ProductoWebController {

    private final ProductoService productoService;

    public ProductoWebController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // =========================
    // LISTADO /web/productos
    // =========================
    @GetMapping
    public String listar(Model model) {
        try {
            List<Producto> productos = productoService.listarProductos();
            model.addAttribute("productos", productos);
            return "producto/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "⚠ Error al cargar los productos: " + e.getMessage());
            return "producto/list";
        }
    }

    // =========================
    // FORMULARIO CREAR /web/productos/create
    // =========================
    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("nombre", "");
        model.addAttribute("descripcion", "");
        model.addAttribute("precio", "");
        model.addAttribute("stock", "");
        return "producto/create";
    }

    // =========================
    // GUARDAR POST /web/productos/create
    // =========================
    @PostMapping("/create")
    public String crear(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "precio", required = false) BigDecimal precio,
            @RequestParam(value = "stock", required = false) Integer stock,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validaciones
        if (nombre == null || nombre.trim().isEmpty()) {
            return mostrarError(model, "El nombre del producto es obligatorio", 
                              nombre, descripcion, precio, stock);
        }

        if (nombre.trim().length() < 3) {
            return mostrarError(model, "El nombre debe tener al menos 3 caracteres", 
                              nombre, descripcion, precio, stock);
        }

        if (precio == null) {
            return mostrarError(model, "El precio es obligatorio", 
                              nombre, descripcion, precio, stock);
        }

        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            return mostrarError(model, "El precio debe ser mayor a 0", 
                              nombre, descripcion, precio, stock);
        }

        if (stock == null) {
            return mostrarError(model, "El stock es obligatorio", 
                              nombre, descripcion, precio, stock);
        }

        if (stock < 0) {
            return mostrarError(model, "El stock no puede ser negativo", 
                              nombre, descripcion, precio, stock);
        }

        try {
            // Crear producto
            productoService.crearProducto(
                nombre.trim(),
                descripcion != null ? descripcion.trim() : "",
                precio,
                stock
            );

            redirectAttributes.addFlashAttribute("successMessage", 
                "✓ Producto '" + nombre + "' creado exitosamente");
            return "redirect:/web/productos";

        } catch (Exception e) {
            return mostrarError(model, "Error al crear el producto: " + e.getMessage(), 
                              nombre, descripcion, precio, stock);
        }
    }

    // =========================
    // FORMULARIO EDITAR /web/productos/{id}/edit
    // =========================
    @GetMapping("/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model,
                                         RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.obtenerProductoPorId(id);
            
            if (producto == null) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ Producto no encontrado");
                return "redirect:/web/productos";
            }

            model.addAttribute("producto", producto);
            return "producto/edit";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al cargar el producto: " + e.getMessage());
            return "redirect:/web/productos";
        }
    }

    // =========================
    // ACTUALIZAR POST /web/productos/{id}/edit
    // =========================
    @PostMapping("/{id}/edit")
    public String actualizar(
            @PathVariable Long id,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "precio", required = false) BigDecimal precio,
            @RequestParam(value = "stock", required = false) Integer stock,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // Buscar producto existente
            Producto producto = productoService.obtenerProductoPorId(id);
            
            if (producto == null) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ Producto no encontrado");
                return "redirect:/web/productos";
            }

            // Validaciones
            if (nombre == null || nombre.trim().isEmpty()) {
                model.addAttribute("errorMessage", "El nombre del producto es obligatorio");
                model.addAttribute("producto", producto);
                return "producto/edit";
            }

            if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("errorMessage", "El precio debe ser mayor a 0");
                model.addAttribute("producto", producto);
                return "producto/edit";
            }

            if (stock == null || stock < 0) {
                model.addAttribute("errorMessage", "El stock no puede ser negativo");
                model.addAttribute("producto", producto);
                return "producto/edit";
            }

            // Actualizar campos
            producto.setNombre(nombre.trim());
            producto.setDescripcion(descripcion != null ? descripcion.trim() : "");
            producto.setPrecio(precio);
            producto.setStock(stock);

            // Guardar cambios (reutilizando el método de actualizar stock)
            productoService.actualizarStock(id, stock);

            redirectAttributes.addFlashAttribute("successMessage", 
                "✓ Producto actualizado exitosamente");
            return "redirect:/web/productos";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al actualizar el producto: " + e.getMessage());
            return "redirect:/web/productos";
        }
    }

    // =========================
    // ELIMINAR POST /web/productos/{id}/delete
    // =========================
    @PostMapping("/{id}/delete")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean eliminado = productoService.eliminarProducto(id);
            
            if (eliminado) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✓ Producto eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ Producto no encontrado");
            }

            return "redirect:/web/productos";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al eliminar el producto: " + e.getMessage());
            return "redirect:/web/productos";
        }
    }

    // =========================
    // MÉTODO AUXILIAR PARA ERRORES
    // =========================
    private String mostrarError(Model model, String mensaje,
                               String nombre, String descripcion, 
                               BigDecimal precio, Integer stock) {
        model.addAttribute("errorMessage", mensaje);
        model.addAttribute("nombre", nombre);
        model.addAttribute("descripcion", descripcion);
        model.addAttribute("precio", precio);
        model.addAttribute("stock", stock);
        return "producto/create";
    }
}