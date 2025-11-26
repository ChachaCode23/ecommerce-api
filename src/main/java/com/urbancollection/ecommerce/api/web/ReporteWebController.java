package com.urbancollection.ecommerce.api.web;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import com.urbancollection.ecommerce.persistence.jpa.spring.PedidoJpaRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.ProductoJpaRepository;
import com.urbancollection.ecommerce.persistence.jpa.spring.UsuarioJpaRepository;

@Controller
@RequestMapping("/web/reportes")
// Controlador web encargado de mostrar los reportes generales en la vista.
public class ReporteWebController {

    // Repositorio para consultar los pedidos almacenados en la base de datos.
    private final PedidoJpaRepository pedidoRepository;
    // Repositorio para consultar la cantidad de productos.
    private final ProductoJpaRepository productoRepository;
    // Repositorio para consultar la cantidad de usuarios.
    private final UsuarioJpaRepository usuarioRepository;

    // Constructor donde Spring inyecta los repositorios necesarios.
    public ReporteWebController(PedidoJpaRepository pedidoRepository,
                                ProductoJpaRepository productoRepository,
                                UsuarioJpaRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    // Método GET que arma los datos de los reportes y los envía a la vista.
    public String mostrarReportes(Model model) {
        try {
            // Traigo todos los pedidos de la base de datos.
            List<Pedido> todosPedidos = pedidoRepository.findAll();
            
            // Total de pedidos es simplemente el tamaño de la lista.
            int totalPedidos = todosPedidos.size();
            // Cantidad total de productos registrados.
            long totalProductos = productoRepository.count();
            // Cantidad total de usuarios registrados.
            long totalUsuarios = usuarioRepository.count();
            
            // Sumo el total de cada pedido, ignorando los null.
            BigDecimal totalVentas = todosPedidos.stream()
                    .map(Pedido::getTotal)
                    .filter(total -> total != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Si hay pedidos, calculo el promedio de ventas, si no, el promedio es cero.
            BigDecimal promedioVenta = totalPedidos > 0 
                    ? totalVentas.divide(new BigDecimal(totalPedidos), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            
            // Cuento cuántos pedidos están en algún estado que contenga la palabra "PENDIENTE".
            long pedidosPendientes = todosPedidos.stream()
                    .filter(p -> p.getEstado() != null && p.getEstado().name().contains("PENDIENTE"))
                    .count();
            
            // Cuento cuántos pedidos están exactamente en estado PAGADO.
            long pedidosPagados = todosPedidos.stream()
                    .filter(p -> p.getEstado() != null && p.getEstado().name().equals("PAGADO"))
                    .count();
            
            // Cuento cuántos pedidos están exactamente en estado COMPLETADO.
            long pedidosCompletados = todosPedidos.stream()
                    .filter(p -> p.getEstado() != null && p.getEstado().name().equals("COMPLETADO"))
                    .count();
            
            // Mando todos los valores calculados al modelo para que la vista los muestre.
            model.addAttribute("totalPedidos", totalPedidos);
            model.addAttribute("totalProductos", totalProductos);
            model.addAttribute("totalUsuarios", totalUsuarios);
            model.addAttribute("totalVentas", totalVentas);
            model.addAttribute("promedioVenta", promedioVenta);
            model.addAttribute("pedidosPendientes", pedidosPendientes);
            model.addAttribute("pedidosPagados", pedidosPagados);
            model.addAttribute("pedidosCompletados", pedidosCompletados);
            
            // Devuelvo la vista principal de reportes.
            return "reporte/index";
            
        } catch (Exception e) {
            // Si ocurre algún error, envío el mensaje a la vista y muestro la misma página.
            model.addAttribute("errorMessage", "⚠ Error: " + e.getMessage());
            return "reporte/index";
        }
    }
}
