package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.api.web.dto.EnvioRequest;
import com.urbancollection.ecommerce.application.service.IEnvioService;
import com.urbancollection.ecommerce.domain.entity.logistica.Envio;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.domain.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
// Aqui manejamos los envíos asociados a los pedidos.
public class EnvioController {

    // Servicio que contiene la lógica de negocio de los envíos.
    private final IEnvioService envioService;

    // Repositorio para buscar el pedido al que pertenece el envío.
    private final PedidoRepository pedidoRepository;

    // Constructor donde Spring inyecta el servicio de envíos y el repositorio de pedidos.
    public EnvioController(IEnvioService envioService, PedidoRepository pedidoRepository) {
        this.envioService = envioService;
        this.pedidoRepository = pedidoRepository;
    }

    // GET /api/envios
    // Devuelve la lista de todos los envíos registrados.
    @GetMapping
    public ResponseEntity<List<Envio>> listar() {
        List<Envio> envios = envioService.listar();
        return ResponseEntity.ok(envios);
    }

    // GET /api/envios/{id}
    // Busca un envío específico por su id.
    // Si no existe, responde 404.
    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtenerPorId(@PathVariable Long id) {
        return envioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/envios
    // Crea un nuevo envío para un pedido:
    // 1) Busca el pedido por id.
    // 2) Si no existe el pedido, devuelve 404.
    // 3) Si existe, construye la entidad Envio y la guarda.
    @PostMapping
    public ResponseEntity<Envio> crear(@RequestBody EnvioRequest request) {
        Pedido pedido = pedidoRepository.findById(request.getPedidoId());
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        // Armo el objeto Envio a partir de los datos del request.
        Envio envio = new Envio();
        envio.setPedido(pedido);
        envio.setTracking(request.getTracking());
        envio.setEstado(request.getEstado());

        // Delego la creación al servicio de envíos.
        envioService.crear(envio);
        return ResponseEntity.status(HttpStatus.CREATED).body(envio);
    }

    // PUT /api/envios/{id}
    // Actualiza los datos de un envío existente:
    // 1) Verifica que el pedido del request exista.
    // 2) Construye un objeto Envio con los cambios.
    // 3) Llama al servicio para actualizar y luego devuelve el envío actualizado.
    @PutMapping("/{id}")
    public ResponseEntity<Envio> actualizar(@PathVariable Long id, @RequestBody EnvioRequest request) {
        Pedido pedido = pedidoRepository.findById(request.getPedidoId());
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }

        // Creo un objeto con los datos nuevos que se van a aplicar.
        Envio cambios = new Envio();
        cambios.setPedido(pedido);
        cambios.setTracking(request.getTracking());
        cambios.setEstado(request.getEstado());

        // Mando los cambios al servicio para que haga la actualización.
        envioService.actualizar(id, cambios);
        
        // Luego vuelvo a buscar el envío para devolver el resultado final.
        return envioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/envios/{id}
    // Elimina un envío por su id y responde 204 sin contenido.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        envioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
