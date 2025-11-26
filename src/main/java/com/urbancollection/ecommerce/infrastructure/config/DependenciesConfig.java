package com.urbancollection.ecommerce.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.urbancollection.ecommerce.application.service.CuponService;
import com.urbancollection.ecommerce.application.service.DireccionService;
import com.urbancollection.ecommerce.application.service.EnvioService;
import com.urbancollection.ecommerce.application.service.ICuponService;
import com.urbancollection.ecommerce.application.service.IDireccionService;
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
import com.urbancollection.ecommerce.domain.repository.PedidoRepository;
import com.urbancollection.ecommerce.domain.repository.ProductoRepository;
import com.urbancollection.ecommerce.domain.repository.TransaccionPagoRepository;
import com.urbancollection.ecommerce.domain.repository.UsuarioRepository;
import com.urbancollection.ecommerce.domain.service.StockService;
import com.urbancollection.ecommerce.infrastructure.client.ICuponApiClient;
import com.urbancollection.ecommerce.infrastructure.client.IEnvioApiClient;
import com.urbancollection.ecommerce.infrastructure.client.IPedidoApiClient;
import com.urbancollection.ecommerce.infrastructure.client.IProductoApiClient;
import com.urbancollection.ecommerce.infrastructure.client.IUsuarioApiClient;
import com.urbancollection.ecommerce.infrastructure.client.Impl.CuponApiClient;
import com.urbancollection.ecommerce.infrastructure.client.Impl.EnvioApiClient;
import com.urbancollection.ecommerce.infrastructure.client.Impl.PedidoApiClient;
import com.urbancollection.ecommerce.infrastructure.client.Impl.ProductoApiClient;
import com.urbancollection.ecommerce.infrastructure.client.Impl.UsuarioApiClient;

/**
 * Clase de configuración donde registro los beans de los servicios de la aplicación.
 * Aquí se definen las dependencias que Spring va a inyectar usando @Bean.
 * 
 */
@Configuration
public class DependenciesConfig {

    @Value("${api.base.url:http://localhost:8081}")
    private String apiBaseUrl;

    // =====================  REST TEMPLATE =====================

    /**
     * Bean de RestTemplate para realizar peticiones HTTP.
     * Usado por todos los ApiClients para consumir la API REST.
     * Se mantiene para tests y uso futuro.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // =====================  API CLIENTS (PARA TESTS Y MICROSERVICIOS FUTUROS) =====================

    /**
     * Bean del cliente API de Productos.
     * Se mantiene para tests unitarios y preparación de microservicios.
     */
    @Bean
    public IProductoApiClient productoApiClient(RestTemplate restTemplate) {
        return new ProductoApiClient(restTemplate, apiBaseUrl);
    }

    /**
     * Bean del cliente API de Pedidos.
     * Se mantiene para tests unitarios y preparación de microservicios.
     */
    @Bean
    public IPedidoApiClient pedidoApiClient(RestTemplate restTemplate) {
        return new PedidoApiClient(restTemplate, apiBaseUrl);
    }

    /**
     * Bean del cliente API de Envíos.
     * Se mantiene para tests unitarios y preparación de microservicios.
     */
    @Bean
    public IEnvioApiClient envioApiClient(RestTemplate restTemplate) {
        return new EnvioApiClient(restTemplate, apiBaseUrl);
    }

    /**
     * Bean del cliente API de Cupones.
     * Se mantiene para tests unitarios y preparación de microservicios.
     */
    @Bean
    public ICuponApiClient cuponApiClient(RestTemplate restTemplate) {
        return new CuponApiClient(restTemplate, apiBaseUrl);
    }

    /**
     * Bean del cliente API de Usuarios.
     * Se mantiene para tests unitarios y preparación de microservicios.
     */
    @Bean
    public IUsuarioApiClient usuarioApiClient(RestTemplate restTemplate) {
        return new UsuarioApiClient(restTemplate, apiBaseUrl);
    }

    // ===================== STOCK SERVICE =====================

    /**
     * StockService usa ProductoRepository directamente.
     */
    @Bean
    public StockService stockService(ProductoRepository productoRepository) {
        return new StockServiceImpl(productoRepository);
    }

    // ===================== PEDIDO SERVICE =====================

    /**
     * Usa ApiClients en lugar de Repositories
     * Preparado para arquitectura de microservicios
     */
    @Bean
    public IPedidoService pedidoService(IUsuarioApiClient usuarioApiClient,
                                        DireccionRepository direccionRepository,
                                        IProductoApiClient productoApiClient,
                                        IPedidoApiClient pedidoApiClient,
                                        ICuponApiClient cuponApiClient,
                                        TransaccionPagoRepository transaccionPagoRepository,
                                        StockService stockService) {

        return new PedidoService(
                usuarioApiClient,
                direccionRepository,
                productoApiClient,
                pedidoApiClient,
                cuponApiClient,
                transaccionPagoRepository,
                stockService
        );
    }

    // ===================== PRODUCTO SERVICE =====================

    /**
     *  recibe ProductoRepository en lugar de IProductoApiClient
     */
    @Bean
    public IProductoService productoService(ProductoRepository productoRepository) {
        return new ProductoService(productoRepository);
    }

    // ===================== USUARIO SERVICE =====================

    /**
     *  recibe UsuarioRepository en lugar de IUsuarioApiClient
     *  Mantiene DireccionRepository
     */
    @Bean
    public IUsuarioService usuarioService(UsuarioRepository usuarioRepository, 
                                          DireccionRepository direccionRepository) {
        return new UsuarioService(usuarioRepository, direccionRepository);
    }

    // =====================  CUPON SERVICE =====================

    /**
     * recibe CuponRepository en lugar de ICuponApiClient
     */
    @Bean
    public ICuponService cuponService(CuponRepository cuponRepository) {
        return new CuponService(cuponRepository);
    }

    // =====================  ENVIO SERVICE =====================

    /**
     * Ahora recibe EnvioRepository y PedidoRepository
     */
    @Bean
    public IEnvioService envioService(EnvioRepository envioRepository, PedidoRepository pedidoRepository) {
        return new EnvioService(envioRepository, pedidoRepository);
    }

    // ===================== DIRECCION SERVICE =====================

    /**
     * Service para gestionar direcciones
     */
    @Bean
    public IDireccionService direccionService(DireccionRepository direccionRepository) {
        return new DireccionService(direccionRepository);
    }
}