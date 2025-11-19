package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.api.web.dto.CuponRequest; 
import com.urbancollection.ecommerce.application.service.ICuponService; 
import com.urbancollection.ecommerce.domain.entity.catalogo.Cupon;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; 

@RestController // ¡Importante! Esto dice que NO devuelve una página HTML, sino datos (JSON o XML), es una API REST.
@RequestMapping("/api/cupones") // Todos los métodos aquí dentro responderán a rutas que empiezan con /api/cupones
public class CuponController {

    private final ICuponService cuponService; 

    // Aqui hacemos una  inyección de dependencias por constructor. Spring se encarga de darnos una implementación de ICuponService.
    public CuponController(ICuponService cuponService) {
        this.cuponService = cuponService;
    }

    // ==================================================================================
    // 1. OBTENER TODOS (GET /api/cupones)
    // ==================================================================================
    @GetMapping
    public ResponseEntity<List<Cupon>> listar() {
        // Llama al servicio para que nos dé la lista de cupones.
        List<Cupon> cupones = cuponService.listar();
        // Devuelve la lista con el código de estado 200 OK.
        return ResponseEntity.ok(cupones);
    }

    // ==================================================================================
    // 2. OBTENER POR ID (GET /api/cupones/{id})
    // ==================================================================================
    @GetMapping("/{id}") // {id} significa que esperamos un valor en la ruta (ej: /api/cupones/5).
    public ResponseEntity<Cupon> obtenerPorId(@PathVariable Long id) { // @PathVariable captura ese valor de la ruta.
        // Llama al servicio para buscar el cupón. Devuelve un Optional.
        return cuponService.buscarPorId(id)
                // Si el cupón se encuentra (.map(ResponseEntity::ok)), lo devuelve con 200 OK.
                .map(ResponseEntity::ok)
                // Si no se encuentra (.orElse(ResponseEntity.notFound().build())), devuelve 404 NOT FOUND.
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================================================================================
    // 3. CREAR NUEVO (POST /api/cupones)
    // ==================================================================================
    @PostMapping
    public ResponseEntity<Cupon> crear(@RequestBody CuponRequest request) { // @RequestBody lee el JSON que nos envían.
        // Crea un nuevo objeto Cupon.
        Cupon cupon = new Cupon();
        // Mapeo los campos del objeto de la petición 
        cupon.setCodigo(request.getCodigo());
        cupon.setActivo(request.isActivo());
        cupon.setFechaInicio(request.getFechaInicio());
        cupon.setFechaFin(request.getFechaFin());
        cupon.setMinimoCompra(request.getMinimoCompra());
        cupon.setTipo(request.getTipo());
        cupon.setValorDescuento(request.getValorDescuento());
        cupon.setTopeDescuento(request.getTopeDescuento());

        // Llama al servicio para guardarlo en la base de datos (se asume que crea el ID).
        cuponService.crear(cupon);
        // Devuelve el cupón creado con el código de estado 201 CREATED (¡es mejor que 200 OK para creaciones!).
        return ResponseEntity.status(HttpStatus.CREATED).body(cupon);
    }

    // ==================================================================================
    // 4. ACTUALIZAR (PUT /api/cupones/{id})
    // ==================================================================================
    @PutMapping("/{id}")
    public ResponseEntity<Cupon> actualizar(@PathVariable Long id, @RequestBody CuponRequest request) {
        // Crea un objeto temporal 'cambios' con los nuevos datos de la petición.
        Cupon cambios = new Cupon();
        cambios.setCodigo(request.getCodigo());
        cambios.setActivo(request.isActivo());
        cambios.setFechaInicio(request.getFechaInicio());
        cambios.setFechaFin(request.getFechaFin());
        cambios.setMinimoCompra(request.getMinimoCompra());
        cambios.setTipo(request.getTipo());
        cambios.setValorDescuento(request.getValorDescuento());
        cambios.setTopeDescuento(request.getTopeDescuento());

        // Llama al servicio para aplicar los cambios al cupón con ese ID.
        cuponService.actualizar(id, cambios);

        // Llamo otra vez al servicio para recuperar el cupón actualizado y devolverlo en la respuesta. 
        return cuponService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Devuelve 404 si el cupón desaparecio 
    }

    // ==================================================================================
    // 5. ELIMINAR (DELETE /api/cupones/{id})
    // ==================================================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        // Le dice al servicio que lo borre.
        cuponService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}