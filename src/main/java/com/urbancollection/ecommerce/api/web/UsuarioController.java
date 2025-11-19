package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import com.urbancollection.ecommerce.domain.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
// aqui expongo la API de usuarios.
public class UsuarioController {

    // Repositorio para acceder a la tabla de usuarios en la base de datos.
    private final UsuarioRepository usuarioRepository;

    // Constructor donde Spring inyecta el repositorio.
    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    // Devuelve la lista completa de usuarios registrados.
    public ResponseEntity<List<Usuario>> listar() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping
    // Crea un nuevo usuario a partir del JSON que llega en el cuerpo de la petición.
    public ResponseEntity<Usuario> crear(@RequestBody CrearUsuarioRequest request) {
        // Aquí mapeo el DTO simple a la entidad Usuario.
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setContrasena(request.getContrasena());
        usuario.setRol(request.getRol());

        // Guardo el usuario en la base de datos.
        Usuario guardado = usuarioRepository.save(usuario);

        // Respondo con 201 Created y el usuario que se acaba de guardar.
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    // Clase interna que uso como DTO para recibir los datos del usuario desde el cliente.
    public static class CrearUsuarioRequest {
        private String nombre;
        private String correo;
        private String contrasena;
        private String rol;

        // Getters y setters del DTO

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
