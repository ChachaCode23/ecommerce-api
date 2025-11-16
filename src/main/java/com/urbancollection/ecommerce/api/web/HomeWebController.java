package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.api.web.dto.PedidoMapper;
import com.urbancollection.ecommerce.api.web.dto.PedidoResponse;
import com.urbancollection.ecommerce.application.service.IPedidoService;
import com.urbancollection.ecommerce.application.service.IProductoService;
import com.urbancollection.ecommerce.application.service.IUsuarioService;
import com.urbancollection.ecommerce.domain.entity.ventas.Pedido;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeWebController {

    private final IPedidoService pedidoService;
    private final IProductoService productoService;
    private final IUsuarioService usuarioService;

    public HomeWebController(IPedidoService pedidoService,
                             IProductoService productoService,
                             IUsuarioService usuarioService) {
        this.pedidoService = pedidoService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping({"/", "/web", "/web/home"})
    public String home(Model model) {
        try {
            // Obtener estadísticas
            List<Pedido> todosPedidos = pedidoService.listarTodos();
            int totalPedidos = todosPedidos.size();

            // listar() devuelve List<ProductoDTO>, así que contamos con .size()
            int totalProductos = productoService.listar().size();
            
            // listar() devuelve List<Usuario>
            int totalUsuarios = usuarioService.listar().size();

            // Calcular ventas del día (simplificado - todos los pedidos por ahora)
            BigDecimal ventasHoy = todosPedidos.stream()
                    .map(Pedido::getTotal)
                    .filter(total -> total != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Obtener últimos 5 pedidos
            List<PedidoResponse> ultimosPedidos = todosPedidos.stream()
                    .limit(5)
                    .map(PedidoMapper::toResponse)
                    .collect(Collectors.toList());

            // Agregar atributos al modelo
            model.addAttribute("totalPedidos", totalPedidos);
            model.addAttribute("totalProductos", totalProductos);
            model.addAttribute("totalUsuarios", totalUsuarios);
            model.addAttribute("ventasHoy", ventasHoy);
            model.addAttribute("ultimosPedidos", ultimosPedidos);

            return "home";

        } catch (Exception e) {
            model.addAttribute("errorMessage", "⚠ Error al cargar el dashboard: " + e.getMessage());
            model.addAttribute("totalPedidos", 0);
            model.addAttribute("totalProductos", 0);
            model.addAttribute("totalUsuarios", 0);
            model.addAttribute("ventasHoy", BigDecimal.ZERO);
            model.addAttribute("ultimosPedidos", new ArrayList<>());
            return "home";
        }
    }
}