package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.application.service.IDireccionService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.logistica.Direccion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/direcciones")
public class DireccionController {

    // Servicio de direcciones de la capa de aplicación.
    // Aquí va la lógica de negocio, el controlador solo "orquesta" la llamada.
    private final IDireccionService direccionService;

    // Constructor donde Spring inyecta el servicio de direcciones.
    public DireccionController(IDireccionService direccionService) {
        this.direccionService = direccionService;
    }

    // ================== GET /api/direcciones ==================
    // Endpoint para listar todas las direcciones.
    @GetMapping
    public ResponseEntity<List<Direccion>> listar() {
        // Le pedimos al servicio la lista de direcciones.
        List<Direccion> direcciones = direccionService.listar();
        // Devolvemos la lista con estado 200 OK.
        return ResponseEntity.ok(direcciones);
    }

    // ================== POST /api/direcciones ==================
    // Endpoint para crear una nueva dirección.
    @PostMapping
    public ResponseEntity<Direccion> crear(@RequestBody CrearDireccionRequest request) {
        // Creamos una entidad Direccion a partir de los datos que vienen en el body (DTO).
        Direccion direccion = new Direccion();
        direccion.setCalle(request.getCalle());
        direccion.setCiudad(request.getCiudad());
        direccion.setProvincia(request.getProvincia());
        direccion.setCodigoPostal(request.getCodigoPostal());
        
        // Llamamos al servicio para que ejecute la lógica de creación.
        OperationResult result = direccionService.crear(direccion);
        
        // Si la operación falla según la lógica de negocio, devolvemos 400 Bad Request.
        if (!result.isSuccess()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Si todo sale bien, devolvemos 201 Created con la dirección creada en el body.
        return ResponseEntity.status(HttpStatus.CREATED).body(direccion);
    }

    // ================== DTO ==================
    // Esta clase interna funciona como DTO de entrada para el endpoint POST.
    // Representa el JSON que el cliente envía en la petición.
    public static class CrearDireccionRequest {
        // Campos simples que vienen del cliente.
        private String calle;
        private String ciudad;
        private String provincia;
        private String codigoPostal;

        // Getters y setters para que Spring pueda mapear el JSON a este objeto.

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
