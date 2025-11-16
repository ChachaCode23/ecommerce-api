package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.application.service.IUsuarioService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/web/usuarios")
public class UsuarioWebController {

    private final IUsuarioService usuarioService;

    public UsuarioWebController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // =========================
    // LISTADO /web/usuarios
    // =========================
    @GetMapping
    public String listar(Model model) {
        try {
            List<Usuario> usuarios = usuarioService.listar();
            model.addAttribute("usuarios", usuarios);
            return "usuario/list";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "⚠ Error al cargar los usuarios: " + e.getMessage());
            return "usuario/list";
        }
    }

    // =========================
    // FORMULARIO CREAR /web/usuarios/create
    // =========================
    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("nombre", "");
        model.addAttribute("correo", "");
        model.addAttribute("contrasena", "");
        model.addAttribute("rol", "CUSTOMER");
        return "usuario/create";
    }

    // =========================
    // GUARDAR POST /web/usuarios/create
    // =========================
    @PostMapping("/create")
    public String crear(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "correo", required = false) String correo,
            @RequestParam(value = "contrasena", required = false) String contrasena,
            @RequestParam(value = "rol", required = false) String rol,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validaciones
        if (nombre == null || nombre.trim().isEmpty()) {
            return mostrarError(model, "El nombre es obligatorio", 
                              nombre, correo, contrasena, rol);
        }

        if (nombre.trim().length() < 3) {
            return mostrarError(model, "El nombre debe tener al menos 3 caracteres", 
                              nombre, correo, contrasena, rol);
        }

        if (correo == null || correo.trim().isEmpty()) {
            return mostrarError(model, "El correo es obligatorio", 
                              nombre, correo, contrasena, rol);
        }

        if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return mostrarError(model, "El correo no tiene un formato válido", 
                              nombre, correo, contrasena, rol);
        }

        if (contrasena == null || contrasena.trim().isEmpty()) {
            return mostrarError(model, "La contraseña es obligatoria", 
                              nombre, correo, contrasena, rol);
        }

        if (contrasena.trim().length() < 6) {
            return mostrarError(model, "La contraseña debe tener al menos 6 caracteres", 
                              nombre, correo, contrasena, rol);
        }

        if (rol == null || rol.trim().isEmpty()) {
            return mostrarError(model, "El rol es obligatorio", 
                              nombre, correo, contrasena, rol);
        }

        try {
            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre.trim());
            usuario.setCorreo(correo.trim().toLowerCase());
            usuario.setContrasena(contrasena); // En producción debería hashearse
            usuario.setRol(rol.trim().toUpperCase());

            OperationResult result = usuarioService.crear(usuario);

            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✓ Usuario '" + nombre + "' creado exitosamente");
                return "redirect:/web/usuarios";
            } else {
                return mostrarError(model, result.getMessage(), 
                                  nombre, correo, contrasena, rol);
            }

        } catch (Exception e) {
            return mostrarError(model, "Error al crear el usuario: " + e.getMessage(), 
                              nombre, correo, contrasena, rol);
        }
    }

    // =========================
    // FORMULARIO EDITAR /web/usuarios/{id}/edit
    // =========================
    @GetMapping("/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model,
                                         RedirectAttributes redirectAttributes) {
        try {
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
            
            if (!usuarioOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ Usuario no encontrado");
                return "redirect:/web/usuarios";
            }

            model.addAttribute("usuario", usuarioOpt.get());
            return "usuario/edit";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al cargar el usuario: " + e.getMessage());
            return "redirect:/web/usuarios";
        }
    }

    // =========================
    // ACTUALIZAR POST /web/usuarios/{id}/edit
    // =========================
    @PostMapping("/{id}/edit")
    public String actualizar(
            @PathVariable Long id,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "correo", required = false) String correo,
            @RequestParam(value = "contrasena", required = false) String contrasena,
            @RequestParam(value = "rol", required = false) String rol,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
            
            if (!usuarioOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ Usuario no encontrado");
                return "redirect:/web/usuarios";
            }

            Usuario usuario = usuarioOpt.get();

            // Validaciones
            if (nombre == null || nombre.trim().isEmpty()) {
                model.addAttribute("errorMessage", "El nombre es obligatorio");
                model.addAttribute("usuario", usuario);
                return "usuario/edit";
            }

            if (correo == null || correo.trim().isEmpty()) {
                model.addAttribute("errorMessage", "El correo es obligatorio");
                model.addAttribute("usuario", usuario);
                return "usuario/edit";
            }

            if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                model.addAttribute("errorMessage", "El correo no tiene un formato válido");
                model.addAttribute("usuario", usuario);
                return "usuario/edit";
            }

            // Crear objeto con cambios
            Usuario cambios = new Usuario();
            cambios.setNombre(nombre.trim());
            cambios.setCorreo(correo.trim().toLowerCase());
            cambios.setRol(rol != null ? rol.trim().toUpperCase() : usuario.getRol());
            
            // Solo actualizar contraseña si se proporcionó una nueva
            if (contrasena != null && !contrasena.trim().isEmpty()) {
                if (contrasena.trim().length() < 6) {
                    model.addAttribute("errorMessage", "La contraseña debe tener al menos 6 caracteres");
                    model.addAttribute("usuario", usuario);
                    return "usuario/edit";
                }
                cambios.setContrasena(contrasena);
            } else {
                cambios.setContrasena(usuario.getContrasena());
            }

            OperationResult result = usuarioService.actualizar(id, cambios);

            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✓ Usuario actualizado exitosamente");
                return "redirect:/web/usuarios";
            } else {
                model.addAttribute("errorMessage", result.getMessage());
                model.addAttribute("usuario", usuario);
                return "usuario/edit";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al actualizar el usuario: " + e.getMessage());
            return "redirect:/web/usuarios";
        }
    }

    // =========================
    // ELIMINAR POST /web/usuarios/{id}/delete
    // =========================
    @PostMapping("/{id}/delete")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            OperationResult result = usuarioService.eliminar(id);
            
            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✓ Usuario eliminado exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ " + result.getMessage());
            }

            return "redirect:/web/usuarios";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al eliminar el usuario: " + e.getMessage());
            return "redirect:/web/usuarios";
        }
    }

    // =========================
    // MÉTODO AUXILIAR PARA ERRORES
    // =========================
    private String mostrarError(Model model, String mensaje,
                               String nombre, String correo, 
                               String contrasena, String rol) {
        model.addAttribute("errorMessage", mensaje);
        model.addAttribute("nombre", nombre);
        model.addAttribute("correo", correo);
        model.addAttribute("contrasena", contrasena);
        model.addAttribute("rol", rol);
        return "usuario/create";
    }
}