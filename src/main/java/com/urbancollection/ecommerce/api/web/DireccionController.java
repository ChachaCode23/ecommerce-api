package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.domain.entity.logistica.Direccion;
import com.urbancollection.ecommerce.domain.repository.DireccionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/direcciones")
// Aqui se  maneja las direcciones de envío del usuario.
public class DireccionController {

    // Repositorio para acceder a la tabla de direcciones en la base de datos.
    private final DireccionRepository direccionRepository;

    // Constructor donde Spring inyecta el repositorio.
    public DireccionController(DireccionRepository direccionRepository) {
        this.direccionRepository = direccionRepository;
    }

    @GetMapping
    // Devuelve la lista completa de direcciones registradas.
    public ResponseEntity<List<Direccion>> listar() {
        return ResponseEntity.ok(direccionRepository.findAll());
    }

    @PostMapping
    // Crea una nueva dirección a partir de los datos que vienen en el cuerpo de la petición.
    public ResponseEntity<Direccion> crear(@RequestBody CrearDireccionRequest request) {
        // Aquí mapeo el DTO simple a la entidad Direccion.
        Direccion direccion = new Direccion();
        direccion.setCalle(request.getCalle());
        direccion.setCiudad(request.getCiudad());
        direccion.setProvincia(request.getProvincia());
        direccion.setCodigoPostal(request.getCodigoPostal());
        
        // Guardo la dirección en la base de datos y devuelvo la versión guardada.
        Direccion guardada = direccionRepository.save(direccion);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

    // Clase interna que uso como DTO para recibir la información de la dirección desde el cliente.
    public static class CrearDireccionRequest {
        private String calle;
        private String ciudad;
        private String provincia;
        private String codigoPostal;

        public String getCalle() {
            return calle;
        }

        public void setCalle(String calle) {
            this.calle = calle;
        }

        public String getCiudad() {
            return ciudad;
        }

        public void setCiudad(String ciudad) {
            this.ciudad = ciudad;
        }

        public String getProvincia() {
            return provincia;
        }

        public void setProvincia(String provincia) {
            this.provincia = provincia;
        }

        public String getCodigoPostal() {
            return codigoPostal;
        }

        public void setCodigoPostal(String codigoPostal) {
            this.codigoPostal = codigoPostal;
        }
    }
}
