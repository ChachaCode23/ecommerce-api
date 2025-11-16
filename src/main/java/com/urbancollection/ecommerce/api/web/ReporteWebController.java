package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.application.service.IPedidoService;
import com.urbancollection.ecommerce.application.service.IProductoService;
import com.urbancollection.ecommerce.application.service.IUsuarioService;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/web/reportes")
public class ReporteWebController {

    private final IPedidoService pedidoService;
    private final IProductoService productoService;
    private final IUsuarioService usuarioService;

    public ReporteWebController(IPedidoService pedidoService,
                                IProductoService productoService,
                                IUsuarioService usuarioService) {
        this.pedidoService = pedidoService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String mostrarReportes(Model model) {
        try {
            // Obtener datos para reportes
            List<Pedido> todosPedidos = pedidoService.listarTodos();
            
            // Calcular estadísticas
            int totalPedidos = todosPedidos.size();
            int totalProductos = productoService.listar().size();
            int totalUsuarios = usuarioService.listar().size();
            
            // Calcular total de ventas
            BigDecimal totalVentas = todosPedidos.stream()
                    .map(Pedido::getTotal)
                    .filter(total -> total != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Calcular promedio de venta
            BigDecimal promedioVenta = totalPedidos > 0 
                    ? totalVentas.divide(new BigDecimal(totalPedidos), 2, BigDecimal.ROUND_HALF_UP)
                    : BigDecimal.ZERO;
            
            // Contar pedidos por estado
            long pedidosPendientes = todosPedidos.stream()
                    .filter(p -> p.getEstado() != null && p.getEstado().name().contains("PENDIENTE"))
                    .count();
            
            long pedidosPagados = todosPedidos.stream()
                    .filter(p -> p.getEstado() != null && p.getEstado().name().equals("PAGADO"))
                    .count();
            
            long pedidosCompletados = todosPedidos.stream()
                    .filter(p -> p.getEstado() != null && p.getEstado().name().equals("COMPLETADO"))
                    .count();
            
            // Agregar al modelo
            model.addAttribute("totalPedidos", totalPedidos);
            model.addAttribute("totalProductos", totalProductos);
            model.addAttribute("totalUsuarios", totalUsuarios);
            model.addAttribute("totalVentas", totalVentas);
            model.addAttribute("promedioVenta", promedioVenta);
            model.addAttribute("pedidosPendientes", pedidosPendientes);
            model.addAttribute("pedidosPagados", pedidosPagados);
            model.addAttribute("pedidosCompletados", pedidosCompletados);
            
            return "reporte/index";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "⚠ Error al generar reportes: " + e.getMessage());
            return "reporte/index";
        }
    }
}