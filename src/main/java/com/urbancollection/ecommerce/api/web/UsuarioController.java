package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.application.service.IUsuarioService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


// Controlador REST para manejar usuarios en la API 
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    // Servicio de usuarios de la capa de aplicación.
    // Aquí va la lógica de negocio, el controlador solo recibe y responde HTTP.
    private final IUsuarioService usuarioService; 

    // Constructor donde Spring inyecta la implementación de IUsuarioService.
    public UsuarioController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // ================== GET /api/usuarios ==================
    // Endpoint para listar todos los usuarios registrados.
    @GetMapping
    public ResponseEntity<List<Usuario>> listar() {
        // Le pedimos al servicio la lista de usuarios.
        List<Usuario> usuarios = usuarioService.listar();
        // Devolvemos la lista con código 200 OK.
        return ResponseEntity.ok(usuarios);
    }

    // ================== POST /api/usuarios ==================
    // Endpoint para crear un nuevo usuario a partir de un JSON en el body.
    @PostMapping
    public ResponseEntity<Usuario> crear(@RequestBody CrearUsuarioRequest request) {
        // Creamos una entidad Usuario usando los datos que vienen en el request (DTO).
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setContrasena(request.getContrasena());
        usuario.setRol(request.getRol());

        // Llamamos al servicio para que intente crear el usuario y aplique las validaciones de negocio.
        OperationResult result = usuarioService.crear(usuario);
        
        // Si la operación no fue exitosa, devolvemos 400 Bad Request.
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().build();
        }

        // Si todo salió bien, devolvemos 201 Created con el usuario creado en el body.
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    // ================== DTO ==================
    // Clase interna que funciona como DTO para recibir el JSON de creación de usuario.
    public static class CrearUsuarioRequest {
        // Campos que esperamos en el JSON del cliente.
        private String nombre;
        private String correo;
        private String contrasena;
        private String rol;

        // Getters y setters para que Spring pueda mapear el JSON a este objeto.

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getCorreo() {
            return correo;
        }

        public void setCorreo(String correo) {
            this.correo = correo;
        }

        public String getContrasena() {
            return contrasena;
        }

        public void setContrasena(String contrasena) {
            this.contrasena = contrasena;
        }

        public String getRol() {
            return rol;
        }

        public void setRol(String rol) {
            this.rol = rol;
        }
    }
}
