package com.urbancollection.ecommerce.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.urbancollection.ecommerce.application.service.PedidoService;
import com.urbancollection.ecommerce.application.service.ProductoService;

import com.urbancollection.ecommerce.domain.repository.*;
import com.urbancollection.ecommerce.persistence.repositories.*;

/**
 * AppConfig
 * ------------------------------------------------
 * Esta clase configura los "beans" que usara Spring.
 * Aquí estoy registrando implementaciones en memoria de los repositorios,
 * y también exponiendo los servicios como beans para que Spring los inyecte donde se necesiten.
 */
@Configuration
public class AppConfig {

    // ===== Repos in-memory =====
    // Cada método @Bean devuelve una implementación en memoria del repositorio correspondiente.
    // Así, el resto de la aplicación (services) puede trabajar como si fuera una BD real.
    @Bean public ProductoRepository productoRepository() { return new ProductoRepositoryImpl(); }
    @Bean public UsuarioRepository usuarioRepository() { return new UsuarioRepositoryImpl(); }
    @Bean public DireccionRepository direccionRepository() { return new DireccionRepositoryImpl(); }
    @Bean public PedidoRepository pedidoRepository() { return new PedidoRepositoryImpl(); }
    @Bean public ItemPedidoRepository itemPedidoRepository() { return new ItemPedidoRepositoryImpl(); }
    @Bean public CuponRepository cuponRepository() { return new CuponRepositoryImpl(); }
    @Bean public TransaccionPagoRepository transaccionPagoRepository() { return new TransaccionPagoRepositoryImpl(); }
    @Bean public EnvioRepository envioRepository() { return new EnvioRepositoryImpl(); }

    // ===== Servicios =====
    // Exponemos los servicios como beans para que Spring pueda inyectarlos en los controladores o donde se pidan.
    @Bean
    public ProductoService productoService(ProductoRepository productoRepository) {
        // Inyección por parámetro: Spring pasa el bean ProductoRepository configurado arriba.
        return new ProductoService(productoRepository);
    }

    @Bean
    public PedidoService pedidoService(
            UsuarioRepository u, DireccionRepository d, ProductoRepository pr,
            PedidoRepository pe, ItemPedidoRepository ip,
            CuponRepository c, TransaccionPagoRepository tx, EnvioRepository env) {
        // Igual que arriba, Spring resuelve e inyecta cada repositorio.
        return new PedidoService(u, d, pr, pe, ip, c, tx, env);
    }

    // ===== Seed de datos (carga inicial) =====
    // CommandLineRunner se ejecuta al iniciar la aplicación.
    // Aquí se insertan datos de ejemplo para probar rápidamente (usuario, dirección, producto).
    // Útil en entorno de desarrollo para tener algo que consultar sin crear todo desde cero.
    @org.springframework.context.annotation.Bean
    public org.springframework.boot.CommandLineRunner seedData(
            com.urbancollection.ecommerce.domain.repository.UsuarioRepository uRepo,
            com.urbancollection.ecommerce.domain.repository.DireccionRepository dRepo,
            com.urbancollection.ecommerce.domain.repository.ProductoRepository pRepo
    ) {
        return args -> {
            // Usuario de ejemplo
            var u = new com.urbancollection.ecommerce.domain.entity.usuarios.Usuario();
            u.setNombre("Juan Perez");
            u.setCorreo("juan@example.com");
            u.setContrasena("secreto123");
            u.setRol("CLIENTE");
            uRepo.save(u); // Guardamos en el repo en memoria

            // Dirección de ejemplo
            var d = new com.urbancollection.ecommerce.domain.entity.logistica.Direccion();
            d.setCalle("Av. Principal 123");
            d.setCiudad("Santo Domingo");
            d.setProvincia("DN");
            d.setCodigoPostal("10101");
            dRepo.save(d);

            // Producto de ejemplo
            var p = new com.urbancollection.ecommerce.domain.entity.catalogo.Producto();
            p.setNombre("Gorra Negra");
            p.setDescripcion("Gorra negra 47 con logo");
            p.setPrecio(new java.math.BigDecimal("950.00"));
            p.setStock(10);
            pRepo.save(p);

            // Mensaje de confirmación en consola para verificar que se cargaron los datos
            System.out.println("[SEED] UsuarioId=" + u.getId() + " DireccionId=" + d.getId() + " ProductoId=" + p.getId());
        };
    }
}
