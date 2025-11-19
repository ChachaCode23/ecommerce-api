package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import com.urbancollection.ecommerce.domain.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private static final String BASE_URL = "/api/usuarios"; // cambia esto si tu controller usa otra ruta

    private MockMvc mockMvc;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioController usuarioController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
        objectMapper = new ObjectMapper();
    }

    // =========================
    // GET /api/usuarios
    // =========================

    @Test
    void listar_deberiaResponderExitosamente() throws Exception {
        Usuario u1 = new Usuario();
        Usuario u2 = new Usuario();
        when(usuarioRepository.findAll()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().is2xxSuccessful());
    }

    // =========================
    // POST /api/usuarios
    // =========================

    @Test
    void crear_deberiaResponderExitosamente() throws Exception {
        // request JSON
        UsuarioController.CrearUsuarioRequest request =
                new UsuarioController.CrearUsuarioRequest();
        request.setNombre("Juan Pérez");
        request.setCorreo("juan@example.com");
        request.setContrasena("secreta");
        request.setRol("CLIENTE");

        // lo que devuelve el repo
        Usuario guardado = new Usuario();
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(guardado);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is2xxSuccessful());
    }
}
