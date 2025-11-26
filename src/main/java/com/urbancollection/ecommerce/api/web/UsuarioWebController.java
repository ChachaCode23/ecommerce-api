package com.urbancollection.ecommerce.api.web;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.urbancollection.ecommerce.domain.entity.logistica.Direccion;
import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import com.urbancollection.ecommerce.domain.repository.DireccionRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.UsuarioJpaRepository;

@Controller
@RequestMapping("/web/usuarios")
// Controlador web para gestionar usuarios desde las vistas 
public class UsuarioWebController {

    // Repositorio JPA para acceder a los usuarios en la base de datos.
    private final UsuarioJpaRepository usuarioRepository;
    // Repositorio de dominio para manejar direcciones asociadas a un usuario.
    private final DireccionRepository direccionRepository;

    // Constructor donde Spring inyecta los repositorios necesarios.
    public UsuarioWebController(UsuarioJpaRepository usuarioRepository, DireccionRepository direccionRepository) {
        this.usuarioRepository = usuarioRepository;
        this.direccionRepository = direccionRepository;
    }

    @GetMapping
    // Acción GET que lista todos los usuarios y los manda a la vista.
    public String listar(Model model) {
        try {
            // Busco todos los usuarios en la base de datos.
            List<Usuario> usuarios = usuarioRepository.findAll();
            // Agrego la lista al modelo para que la vista los muestre.
            model.addAttribute("usuarios", usuarios);
            return "usuario/list";
        } catch (Exception e) {
            // Si hay un error, envío un mensaje a la vista.
            model.addAttribute("errorMessage", "⚠ Error al cargar usuarios: " + e.getMessage());
            return "usuario/list";
        }
    }

    @GetMapping("/create")
    // Acción GET que muestra el formulario para crear un nuevo usuario.
    public String mostrarFormularioCrear(Model model) {
        // Solo devolvemos la vista del formulario, sin lógica extra.
        return "usuario/create";
    }

    @PostMapping("/create")
    // Acción POST que procesa el formulario y crea un nuevo usuario.
    public String crear(
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "telefono", required = false) String telefono,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // Validación: el nombre es obligatorio.
            if (nombre == null || nombre.trim().isEmpty()) {
                return mostrarError(model, "El nombre es obligatorio", nombre, email, telefono);
            }

            // Validación: el email también es obligatorio.
            if (email == null || email.trim().isEmpty()) {
                return mostrarError(model, "El email es obligatorio", nombre, email, telefono);
            }

            // Verifico si ya existe un usuario con ese email.
            Optional<Usuario> existente = usuarioRepository.findByEmail(email.trim());
            if (existente.isPresent()) {
                return mostrarError(model, "Ya existe un usuario con ese email", nombre, email, telefono);
            }

            // Creo la entidad Usuario y asigno los valores.
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre.trim());
            usuario.setEmail(email.trim().toLowerCase());
            usuario.setTelefono(telefono != null ? telefono.trim() : "");
            // Rol por defecto para usuarios creados desde la web.
            usuario.setRol("CUSTOMER");

            // Guardo el usuario en la base de datos.
            usuarioRepository.save(usuario);

            // Mensaje de éxito para mostrar en el listado.
            redirectAttributes.addFlashAttribute("successMessage", "✓ Usuario creado exitosamente");
            return "redirect:/web/usuarios";
        } catch (Exception e) {
            // Si algo falla, mando el mensaje de error y redirijo.
            redirectAttributes.addFlashAttribute("errorMessage", "⚠ Error: " + e.getMessage());
            return "redirect:/web/usuarios";
        }
    }

    @GetMapping("/{id}/edit")
    // Acción GET que muestra el formulario para editar un usuario existente.
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Busco el usuario por su id.
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
            
            // Si no existe, redirijo al listado con mensaje de error.
            if (!usuarioOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ Usuario no encontrado");
                return "redirect:/web/usuarios";
            }

            Usuario usuario = usuarioOpt.get();
            // Agrego el usuario al modelo para que la vista muestre sus datos.
            model.addAttribute("usuario", usuario);
            
            // Cargar dirección principal del usuario desde el repositorio de direcciones.
            Direccion direccion = direccionRepository.findPrincipalByUsuarioId(usuario.getId().intValue());
            // Agrego la dirección (puede ser null) al modelo.
            model.addAttribute("direccion", direccion);
            
            return "usuario/edit";
        } catch (Exception e) {
            // Manejo de errores al cargar el formulario de edición.
            redirectAttributes.addFlashAttribute("errorMessage", "⚠ Error: " + e.getMessage());
            return "redirect:/web/usuarios";
        }
    }

    @PostMapping("/{id}/edit")
    // Acción POST que actualiza los datos del usuario y su dirección principal.
    public String actualizar(
            @PathVariable Long id,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "correo", required = false) String correo,
            @RequestParam(value = "telefono", required = false) String telefono,
            @RequestParam(value = "direccion_linea1", required = false) String direccionLinea1,
            @RequestParam(value = "direccion_linea2", required = false) String direccionLinea2,
            @RequestParam(value = "direccion_ciudad", required = false) String direccionCiudad,
            @RequestParam(value = "direccion_provincia", required = false) String direccionProvincia,
            @RequestParam(value = "direccion_codigo_postal", required = false) String direccionCodigoPostal,
            @RequestParam(value = "direccion_pais", required = false) String direccionPais,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // Busco el usuario por id.
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
            
            // Si no existe, redirijo.
            if (!usuarioOpt.isPresent()) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ Usuario no encontrado");
                return "redirect:/web/usuarios";
            }

            Usuario usuario = usuarioOpt.get();

            // Validar campos obligatorios ANTES de usar .trim()
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

            // Verificar correo duplicado: que no pertenezca a otro usuario.
            Optional<Usuario> existente = usuarioRepository.findByEmail(correo.trim());
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                model.addAttribute("errorMessage", "Ya existe otro usuario con ese correo");
                model.addAttribute("usuario", usuario);
                return "usuario/edit";
            }

            // Actualizar datos básicos del usuario.
            usuario.setNombre(nombre.trim());
            usuario.setEmail(correo.trim().toLowerCase());
            usuario.setTelefono(telefono != null ? telefono.trim() : "");
            usuarioRepository.save(usuario);

            // Actualizar o crear dirección principal solo si vienen datos mínimos obligatorios.
            if (direccionLinea1 != null && !direccionLinea1.trim().isEmpty() &&
                direccionCiudad != null && !direccionCiudad.trim().isEmpty() &&
                direccionPais != null && !direccionPais.trim().isEmpty()) {
                
                // Busco la dirección principal del usuario.
                Direccion direccion = direccionRepository.findPrincipalByUsuarioId(usuario.getId().intValue());
                
                if (direccion == null) {
                    // Si no tiene dirección principal, creo una nueva.
                    direccion = new Direccion();
                    direccion.setUsuarioId(usuario.getId().intValue());
                    direccion.setEsPrincipal(true);
                }
                
                // Actualizo los campos de dirección con los datos del formulario.
                direccion.setLinea1(direccionLinea1.trim());
                direccion.setLinea2(direccionLinea2 != null ? direccionLinea2.trim() : null);
                direccion.setCiudad(direccionCiudad.trim());
                direccion.setProvincia(direccionProvincia != null ? direccionProvincia.trim() : null);
                direccion.setCodigoPostal(direccionCodigoPostal != null ? direccionCodigoPostal.trim() : null);
                direccion.setPais(direccionPais.trim());
                
                // Guardo la dirección en el repositorio.
                direccionRepository.save(direccion);
            }

            // Si todo salió bien, muestro mensaje de éxito.
            redirectAttributes.addFlashAttribute("successMessage", "✓ Usuario actualizado exitosamente");
            return "redirect:/web/usuarios";
        } catch (Exception e) {
            // Capturo cualquier error y redirijo con mensaje.
            redirectAttributes.addFlashAttribute("errorMessage", "⚠ Error: " + e.getMessage());
            return "redirect:/web/usuarios";
        }
    }

    @PostMapping("/{id}/delete")
    // Acción POST que elimina un usuario por su id.
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Primero verifico que el usuario exista.
            if (!usuarioRepository.existsById(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "⚠ Usuario no encontrado");
                return "redirect:/web/usuarios";
            }

            // Elimino el usuario de la base de datos.
            usuarioRepository.deleteById(id);
            // Mensaje de éxito después de eliminar.
            redirectAttributes.addFlashAttribute("successMessage", "✓ Usuario eliminado exitosamente");
            return "redirect:/web/usuarios";
        } catch (Exception e) {
            // Manejo de errores en la eliminación.
            redirectAttributes.addFlashAttribute("errorMessage", "⚠ Error: " + e.getMessage());
            return "redirect:/web/usuarios";
        }
    }

    // Método de ayuda para manejar errores en el formulario de creación.
    private String mostrarError(Model model, String mensaje, String nombre, String email, String telefono) {
        // Envío el mensaje de error y los valores del formulario para no perderlos.
        model.addAttribute("errorMessage", mensaje);
        model.addAttribute("nombre", nombre);
        model.addAttribute("email", email);
        model.addAttribute("telefono", telefono);
        // Vuelvo al formulario de creación.
        return "usuario/create";
    }
}
