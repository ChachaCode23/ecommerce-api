package com.urbancollection.ecommerce.api.web;

import java.math.BigDecimal;
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

import com.urbancollection.ecommerce.domain.entity.catalogo.Producto;
import com.urbancollection.ecommerce.persistence.jpa.spring.ProductoJpaRepository;

@Controller
@RequestMapping("/web/productos")
// Controlador web para manejar todo lo relacionado con productos en las vistas 
public class ProductoWebController {

    // Repositorio JPA para acceder a la tabla de productos en la base de datos.
    private final ProductoJpaRepository productoRepository;

    // Constructor donde Spring inyecta el repositorio de productos.
    public ProductoWebController(ProductoJpaRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @GetMapping
    // Acción GET que lista todos los productos y los manda a la vista.
    public String listar(Model model) {
        try {
            // Obtengo todos los productos de la base de datos.
            List<Producto> productos = productoRepository.findAll();
            // Los agrego al modelo para que la vista pueda mostrarlos.
            model.addAttribute("productos", productos);
            return "producto/list";
        } catch (Exception e) {
            // Si ocurre un error, envío un mensaje de error a la vista.
            model.addAttribute("errorMessage", "⚠ Error al cargar los productos: " + e.getMessage());
            return "producto/list";
        }
    }

    @GetMapping("/create")
    // Acción GET que muestra el formulario para crear un nuevo producto.
    public String mostrarFormularioCrear(Model model) {
        // Inicializo los campos del formulario vacíos para evitar nulls.
        model.addAttribute("nombre", "");
        model.addAttribute("descripcion", "");
        model.addAttribute("precio", "");
        model.addAttribute("stock", "");
        return "producto/create";
    }

    @PostMapping("/create")
    // Acción POST que recibe los datos del formulario y crea el producto.
    public String crear(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "precio", required = false) BigDecimal precio,
            @RequestParam(value = "stock", required = false) Integer stock,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validación: el nombre es obligatorio.
        if (nombre == null || nombre.trim().isEmpty()) {
            return mostrarError(model, "El nombre del producto es obligatorio", nombre, descripcion, precio, stock);
        }

        // Validación: el precio debe ser mayor que cero.
        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            return mostrarError(model, "El precio debe ser mayor a 0", nombre, descripcion, precio, stock);
        }

        // Validación: el stock no puede ser negativo.
        if (stock == null || stock < 0) {
            return mostrarError(model, "El stock no puede ser negativo", nombre, descripcion, precio, stock);
        }

        try {
            // Creo una nueva instancia de Producto y la lleno con los datos del formulario.
            Producto producto = new Producto();
            producto.setNombre(nombre.trim());
            // Si la descripción viene null, guardo un string vacío para evitar problemas.
            producto.setDescripcion(descripcion != null ? descripcion.trim() : "");
            producto.setPrecio(precio);
            producto.setStock(stock);
            // Al crear el producto lo dejo activo por defecto.
            producto.setActivo(true);
            // Genero un SKU simple usando la hora actual (solo para tener algo único).
            producto.setSku("PROD-" + System.currentTimeMillis());

            // Guardo el producto en la base de datos.
            productoRepository.save(producto);

            // Agrego un mensaje de éxito que se mostrará después del redirect.
            redirectAttributes.addFlashAttribute("successMessage", "✓ Producto creado exitosamente");
            return "redirect:/web/productos";
        } catch (Exception e) {
            // Si algo falla al guardar, vuelvo al formulario con mensaje de error.
            return mostrarError(model, "Error al crear el producto: " + e.getMessage(), nombre, descripcion, precio, stock);
        }
    }

    @GetMapping("/{id}/edit")
    // Acción GET que muestra el formulario para editar un producto existente.
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Busco el producto por su id en la base de datos.
            Optional<Producto> productoOpt = productoRepository.findById(id);
            
            // Si no existe, redirijo al listado con un mensaje de error.
            if (!productoOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ Producto no encontrado");
                return "redirect:/web/productos";
            }

            // Si existe, lo envío a la vista para que se muestren sus datos en el formulario.
            model.addAttribute("producto", productoOpt.get());
            return "producto/edit";
        } catch (Exception e) {
            // Manejo de errores al cargar el producto.
            redirectAttributes.addFlashAttribute("errorMessage", "⚠ Error al cargar el producto: " + e.getMessage());
            return "redirect:/web/productos";
        }
    }

    @PostMapping("/{id}/edit")
    // Acción POST que actualiza los datos de un producto existente.
    public String actualizar(
            @PathVariable Long id,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "precio", required = false) BigDecimal precio,
            @RequestParam(value = "stock", required = false) Integer stock,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // Busco el producto por id.
            Optional<Producto> productoOpt = productoRepository.findById(id);
            
            // Si no lo encuentro, redirijo con error.
            if (!productoOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ Producto no encontrado");
                return "redirect:/web/productos";
            }

            Producto producto = productoOpt.get();

            // Validación básica de los campos antes de actualizar.
            if (nombre == null || nombre.trim().isEmpty() || 
                precio == null || precio.compareTo(BigDecimal.ZERO) <= 0 || 
                stock == null || stock < 0) {

                // Si algo está mal, aviso y recargo el formulario con el producto actual.
                model.addAttribute("errorMessage", "Datos inválidos");
                model.addAttribute("producto", producto);
                return "producto/edit";
            }

            // Si los datos son válidos, actualizo los campos del producto.
            producto.setNombre(nombre.trim());
            producto.setDescripcion(descripcion != null ? descripcion.trim() : "");
            producto.setPrecio(precio);
            producto.setStock(stock);

            // Guardo los cambios en la base de datos.
            productoRepository.save(producto);

            // Mensaje de éxito después de actualizar.
            redirectAttributes.addFlashAttribute("successMessage", "✓ Producto actualizado exitosamente");
            return "redirect:/web/productos";
        } catch (Exception e) {
            // Si algo falla en el proceso de actualización, lo informo.
            redirectAttributes.addFlashAttribute("errorMessage", "⚠ Error al actualizar el producto: " + e.getMessage());
            return "redirect:/web/productos";
        }
    }

    @PostMapping("/{id}/delete")
    // Acción POST que elimina un producto por su id.
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Verifico si el producto existe antes de intentar borrarlo.
            if (!productoRepository.existsById(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ Producto no encontrado");
                return "redirect:/web/productos";
            }

            // Elimino el producto de la base de datos.
            productoRepository.deleteById(id);
            // Mensaje de éxito después de eliminar.
            redirectAttributes.addFlashAttribute("successMessage", "✓ Producto eliminado exitosamente");
            return "redirect:/web/productos";
        } catch (Exception e) {
            // Manejo de errores durante la eliminación.
            redirectAttributes.addFlashAttribute("errorMessage", "⚠ Error al eliminar: " + e.getMessage());
            return "redirect:/web/productos";
        }
    }

    // Método de ayuda para centralizar el manejo de errores en la creación de productos.
    private String mostrarError(Model model, String mensaje, String nombre, String descripcion, BigDecimal precio, Integer stock) {
        // Envío el mensaje de error a la vista.
        model.addAttribute("errorMessage", mensaje);
        // Mantengo los valores que el usuario había escrito para que no se pierdan.
        model.addAttribute("nombre", nombre);
        model.addAttribute("descripcion", descripcion);
        model.addAttribute("precio", precio);
        model.addAttribute("stock", stock);
        // Vuelvo a la vista de creación.
        return "producto/create";
    }
}
