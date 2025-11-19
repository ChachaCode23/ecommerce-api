package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.api.web.dto.PedidoMapper;
import com.urbancollection.ecommerce.application.service.IPedidoService;
import com.urbancollection.ecommerce.application.service.IProductoService;
import com.urbancollection.ecommerce.application.service.IUsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HomeWebControllerTest {

    @Mock
    private IPedidoService pedidoService;

    @Mock
    private IProductoService productoService;

    @Mock
    private IUsuarioService usuarioService;

    @Mock
    private PedidoMapper pedidoMapper;

    @InjectMocks
    private HomeWebController homeWebController;

    @Test
    void home_deberiaRetornarVistaHomeYAgregarAtributosAlModelo() throws Exception {
        // given
        Model model = new ExtendedModelMap();

        // Buscamos el método anotado con @GetMapping (el de la vista home)
        Method metodoHome = Arrays.stream(HomeWebController.class.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(GetMapping.class))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No se encontró ningún método @GetMapping en HomeWebController"));

        // when
        String viewName = (String) metodoHome.invoke(homeWebController, model);

        // then
        assertEquals("home", viewName);

        // Atributos que el controller siempre debe poner en el modelo
        assertTrue(model.containsAttribute("totalPedidos"));
        assertTrue(model.containsAttribute("totalProductos"));
        assertTrue(model.containsAttribute("totalUsuarios"));
        assertTrue(model.containsAttribute("ventasHoy"));
        assertTrue(model.containsAttribute("ultimosPedidos"));

        Object ultimosPedidos = model.getAttribute("ultimosPedidos");
        assertNotNull(ultimosPedidos);
        assertTrue(ultimosPedidos instanceof List);
    }
}
