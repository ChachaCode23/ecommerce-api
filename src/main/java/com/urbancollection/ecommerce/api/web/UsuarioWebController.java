package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.application.service.IUsuarioService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.logistica.Direccion;
import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import com.urbancollection.ecommerce.domain.repository.DireccionRepository;
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
    private final DireccionRepository direccionRepository;

    public UsuarioWebController(IUsuarioService usuarioService, DireccionRepository direccionRepository) {
        this.usuarioService = usuarioService;
        this.direccionRepository = direccionRepository;
    }

 // Muestra la página con el listado de usuarios.
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

 // Muestra el formulario para crear un nuevo usuario.
    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("nombre", "");
        model.addAttribute("correo", "");
        model.addAttribute("contrasena", "");
        model.addAttribute("rol", "CUSTOMER");
        return "usuario/create";
    }

    // Procesa el formulario de creación de usuario y su dirección principal.
    @PostMapping("/create")
    public String crear(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "correo", required = false) String correo,
            @RequestParam(value = "contrasena", required = false) String contrasena,
            @RequestParam(value = "rol", required = false) String rol,
            @RequestParam(value = "direccion_linea1", required = false) String linea1,
            @RequestParam(value = "direccion_linea2", required = false) String linea2,
            @RequestParam(value = "direccion_ciudad", required = false) String ciudad,
            @RequestParam(value = "direccion_provincia", required = false) String provincia,
            @RequestParam(value = "direccion_codigo_postal", required = false) String codigoPostal,
            @RequestParam(value = "direccion_pais", required = false) String pais,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (nombre == null || nombre.trim().isEmpty()) {
            return mostrarError(model, "El nombre es obligatorio", nombre, correo, contrasena, rol);
        }

        if (nombre.trim().length() < 3) {
            return mostrarError(model, "El nombre debe tener al menos 3 caracteres", nombre, correo, contrasena, rol);
        }

        if (correo == null || correo.trim().isEmpty()) {
            return mostrarError(model, "El correo es obligatorio", nombre, correo, contrasena, rol);
        }

        if (!correo.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return mostrarError(model, "El correo no tiene un formato válido", nombre, correo, contrasena, rol);
        }

        if (contrasena == null || contrasena.trim().isEmpty()) {
            return mostrarError(model, "La contraseña es obligatoria", nombre, correo, contrasena, rol);
        }

        if (contrasena.trim().length() < 6) {
            return mostrarError(model, "La contraseña debe tener al menos 6 caracteres", nombre, correo, contrasena, rol);
        }

        if (rol == null || rol.trim().isEmpty()) {
            return mostrarError(model, "El rol es obligatorio", nombre, correo, contrasena, rol);
        }

        if (linea1 == null || linea1.trim().isEmpty()) {
            return mostrarError(model, "La dirección (línea 1) es obligatoria", nombre, correo, contrasena, rol);
        }

        if (ciudad == null || ciudad.trim().isEmpty()) {
            return mostrarError(model, "La ciudad es obligatoria", nombre, correo, contrasena, rol);
        }

        if (pais == null || pais.trim().isEmpty()) {
            return mostrarError(model, "El país es obligatorio", nombre, correo, contrasena, rol);
        }

        try {
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre.trim());
            usuario.setCorreo(correo.trim().toLowerCase());
            usuario.setContrasena(contrasena);
            usuario.setRol(rol.trim().toUpperCase());

            Direccion direccion = new Direccion();
            direccion.setLinea1(linea1.trim());
            direccion.setLinea2(linea2 != null ? linea2.trim() : null);
            direccion.setCiudad(ciudad.trim());
            direccion.setProvincia(provincia != null ? provincia.trim() : null);
            direccion.setCodigoPostal(codigoPostal != null ? codigoPostal.trim() : null);
            direccion.setPais(pais.trim());
            direccion.setNombreContacto(nombre.trim());

            OperationResult result = usuarioService.crearConDireccion(usuario, direccion);

            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✔ Usuario '" + nombre + "' creado exitosamente con su dirección");
                return "redirect:/web/usuarios";
            } else {
                return mostrarError(model, result.getMessage(), nombre, correo, contrasena, rol);
            }

        } catch (Exception e) {
            return mostrarError(model, "Error al crear el usuario: " + e.getMessage(), 
                              nombre, correo, contrasena, rol);
        }
    }

 // Muestra el formulario de edición para el usuario seleccionado.
    @GetMapping("/{id}/edit")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model,
                                         RedirectAttributes redirectAttributes) {
        System.out.println("========== EDITAR USUARIO ==========");
        System.out.println("ID recibido: " + id);
        
        try {
            Optional<Usuario> usuarioOpt = usuarioService.buscarPorId(id);
            System.out.println("Usuario encontrado: " + usuarioOpt.isPresent());
            
            if (!usuarioOpt.isPresent()) {
                System.out.println("Usuario NO encontrado, redirigiendo...");
                redirectAttributes.addFlashAttribute("errorMessage", 
                    "⚠ Usuario no encontrado");
                return "redirect:/web/usuarios";
            }

            Usuario usuario = usuarioOpt.get();
            System.out.println("Usuario: " + usuario.getNombre());
            model.addAttribute("usuario", usuario);

            //  Cargar la dirección principal del usuario
            Direccion direccion = direccionRepository.findPrincipalByUsuarioId(usuario.getId().intValue());
            System.out.println("Dirección encontrada: " + (direccion != null));
            if (direccion != null) {
                System.out.println("  - Ciudad: " + direccion.getCiudad());
                System.out.println("  - Línea1: " + direccion.getLinea1());
            }
            model.addAttribute("direccion", direccion);

            System.out.println("Retornando vista: usuario/edit");
            return "usuario/edit";
            
        } catch (Exception e) {
            System.out.println("EXCEPCIÓN: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al cargar el usuario: " + e.getMessage());
            return "redirect:/web/usuarios";
        }
    }

    // Procesa el formulario de edición y actualiza el usuario y su dirección
    @PostMapping("/{id}/edit")
    public String actualizar(
            @PathVariable Long id,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "correo", required = false) String correo,
            @RequestParam(value = "contrasena", required = false) String contrasena,
            @RequestParam(value = "rol", required = false) String rol,
            @RequestParam(value = "direccion_linea1", required = false) String linea1,
            @RequestParam(value = "direccion_linea2", required = false) String linea2,
            @RequestParam(value = "direccion_ciudad", required = false) String ciudad,
            @RequestParam(value = "direccion_provincia", required = false) String provincia,
            @RequestParam(value = "direccion_codigo_postal", required = false) String codigoPostal,
            @RequestParam(value = "direccion_pais", required = false) String pais,
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

            // Actualizar usuario
            Usuario cambios = new Usuario();
            cambios.setNombre(nombre.trim());
            cambios.setCorreo(correo.trim().toLowerCase());
            cambios.setRol(rol != null ? rol.trim().toUpperCase() : usuario.getRol());
            
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

            if (!result.isSuccess()) {
                model.addAttribute("errorMessage", result.getMessage());
                model.addAttribute("usuario", usuario);
                return "usuario/edit";
            }

            //  Actualizar dirección
            Direccion direccion = direccionRepository.findPrincipalByUsuarioId(usuario.getId().intValue());
            
            if (direccion != null) {
                // Actualiza dirección existente
                if (linea1 != null && !linea1.trim().isEmpty()) {
                    direccion.setLinea1(linea1.trim());
                }
                direccion.setLinea2(linea2 != null ? linea2.trim() : null);
                if (ciudad != null && !ciudad.trim().isEmpty()) {
                    direccion.setCiudad(ciudad.trim());
                }
                direccion.setProvincia(provincia != null ? provincia.trim() : null);
                direccion.setCodigoPostal(codigoPostal != null ? codigoPostal.trim() : null);
                if (pais != null && !pais.trim().isEmpty()) {
                    direccion.setPais(pais.trim());
                }
                direccionRepository.save(direccion);
            } else if (linea1 != null && !linea1.trim().isEmpty() && 
                      ciudad != null && !ciudad.trim().isEmpty() && 
                      pais != null && !pais.trim().isEmpty()) {
                // Crear nueva dirección si no existe
                Direccion nuevaDireccion = new Direccion();
                nuevaDireccion.setUsuarioId(usuario.getId().intValue());
                nuevaDireccion.setLinea1(linea1.trim());
                nuevaDireccion.setLinea2(linea2 != null ? linea2.trim() : null);
                nuevaDireccion.setCiudad(ciudad.trim());
                nuevaDireccion.setProvincia(provincia != null ? provincia.trim() : null);
                nuevaDireccion.setCodigoPostal(codigoPostal != null ? codigoPostal.trim() : null);
                nuevaDireccion.setPais(pais.trim());
                nuevaDireccion.setNombreContacto(nombre.trim());
                nuevaDireccion.setEsPrincipal(true);
                direccionRepository.save(nuevaDireccion);
            }

            redirectAttributes.addFlashAttribute("successMessage", 
                "✔ Usuario y dirección actualizados exitosamente");
            return "redirect:/web/usuarios";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                "⚠ Error al actualizar el usuario: " + e.getMessage());
            return "redirect:/web/usuarios";
        }
    }

    // Elimina el usuario por id
    @PostMapping("/{id}/delete")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            OperationResult result = usuarioService.eliminar(id);
            
            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "✔ Usuario eliminado exitosamente");
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