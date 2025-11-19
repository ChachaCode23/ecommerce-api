package com.urbancollection.ecommerce.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.urbancollection.ecommerce.application.service.CuponService;
import com.urbancollection.ecommerce.application.service.EnvioService;
import com.urbancollection.ecommerce.application.service.ICuponService;
import com.urbancollection.ecommerce.application.service.IEnvioService;
import com.urbancollection.ecommerce.application.service.IPedidoService;
import com.urbancollection.ecommerce.application.service.IProductoService;
import com.urbancollection.ecommerce.application.service.IUsuarioService;
import com.urbancollection.ecommerce.application.service.PedidoService;
import com.urbancollection.ecommerce.application.service.ProductoService;
import com.urbancollection.ecommerce.application.service.StockServiceImpl;
import com.urbancollection.ecommerce.application.service.UsuarioService;

import com.urbancollection.ecommerce.domain.repository.CuponRepository;
import com.urbancollection.ecommerce.domain.repository.DireccionRepository;
import com.urbancollection.ecommerce.domain.repository.EnvioRepository;
import com.urbancollection.ecommerce.domain.repository.ItemPedidoRepository;
import com.urbancollection.ecommerce.domain.repository.PedidoRepository;
import com.urbancollection.ecommerce.domain.repository.ProductoRepository;
import com.urbancollection.ecommerce.domain.repository.TransaccionPagoRepository;
import com.urbancollection.ecommerce.domain.repository.UsuarioRepository;

import com.urbancollection.ecommerce.domain.service.StockService;

@Configuration
// Clase de configuración donde registro los beans de los servicios de la aplicación.
// Aquí se definen las dependencias que Spring va a inyectar usando @Bean.
public class DependenciesConfig {

    // ===================== STOCK SERVICE =====================

    // Recibe el repositorio de productos para poder validar y descontar stock.
    @Bean
    public StockService stockService(ProductoRepository productoRepository) {
        return new StockServiceImpl(productoRepository);
    }

    // ===================== PEDIDO SERVICE =====================

    // Bean que expone el servicio de pedidos.
    // Aquí se inyectan todos los repositorios y servicios que PedidoService necesita
    // para crear pedidos, manejar items, cupones, pagos, envíos y stock.
    @Bean
    public IPedidoService pedidoService(UsuarioRepository usuarioRepository,
                                        DireccionRepository direccionRepository,
                                        ProductoRepository productoRepository,
                                        PedidoRepository pedidoRepository,
                                        ItemPedidoRepository itemPedidoRepository,
                                        CuponRepository cuponRepository,
                                        TransaccionPagoRepository transaccionPagoRepository,
                                        EnvioRepository envioRepository,
                                        StockService stockService) {

        return new PedidoService(
                usuarioRepository,
                direccionRepository,
                productoRepository,
                pedidoRepository,
                itemPedidoRepository,
                cuponRepository,
                transaccionPagoRepository,
                envioRepository,
                stockService
        );
    }

    // ===================== PRODUCTO SERVICE =====================

    
    // Solo necesita el repositorio de productos para hacer operaciones CRUD.
    @Bean
    public IProductoService productoService(ProductoRepository productoRepository) {
        return new ProductoService(productoRepository);
    }

    // ===================== USUARIO SERVICE =====================

   
    // Usa el repositorio de usuarios y direcciones para manejar datos de usuario
    // y su relación con direcciones.
    @Bean
    public IUsuarioService usuarioService(UsuarioRepository usuarioRepository, 
                                          DireccionRepository direccionRepository) {
        return new UsuarioService(usuarioRepository, direccionRepository);
    }

    // ===================== CUPON SERVICE =====================

    
    // Se apoya en el repositorio de cupones para crear, listar y actualizar.
    @Bean
    public ICuponService cuponService(CuponRepository cuponRepository) {
        return new CuponService(cuponRepository);
    }

    // ===================== ENVIO SERVICE =====================
  
    // Usa el repositorio de envíos y de pedidos para asociar envíos a un pedido.
    @Bean
    public IEnvioService envioService(EnvioRepository envioRepository, PedidoRepository pedidoRepository) {
        return new EnvioService(envioRepository, pedidoRepository);
    }
}
