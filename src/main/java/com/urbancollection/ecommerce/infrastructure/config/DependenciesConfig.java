package com.urbancollection.ecommerce.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.urbancollection.ecommerce.application.service.CuponService;
import com.urbancollection.ecommerce.application.service.ICuponService;
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
public class DependenciesConfig {

    // ===================== STOCK SERVICE =====================

    @Bean
    public StockService stockService(ProductoRepository productoRepository) {
        return new StockServiceImpl(productoRepository);
    }

    // ===================== PEDIDO SERVICE =====================

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

    @Bean
    public IProductoService productoService(ProductoRepository productoRepository) {
        return new ProductoService(productoRepository);
    }

    // ===================== USUARIO SERVICE =====================

    @Bean
    public IUsuarioService usuarioService(UsuarioRepository usuarioRepository) {
        return new UsuarioService(usuarioRepository);
    }

    // ===================== CUPON SERVICE =====================

    @Bean
    public ICuponService cuponService(CuponRepository cuponRepository) {
        return new CuponService(cuponRepository);
    }
}