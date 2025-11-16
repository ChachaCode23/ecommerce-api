package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.domain.entity.logistica.Direccion;
import com.urbancollection.ecommerce.domain.repository.DireccionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionController {

    private final DireccionRepository direccionRepository;

    public DireccionController(DireccionRepository direccionRepository) {
        this.direccionRepository = direccionRepository;
    }

    @GetMapping
    public ResponseEntity<List<Direccion>> listar() {
        return ResponseEntity.ok(direccionRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Direccion> crear(@RequestBody CrearDireccionRequest request) {
        Direccion direccion = new Direccion();
        direccion.setCalle(request.getCalle());
        direccion.setCiudad(request.getCiudad());
        direccion.setProvincia(request.getProvincia());
        direccion.setCodigoPostal(request.getCodigoPostal());
        
        Direccion guardada = direccionRepository.save(direccion);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardada);
    }

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