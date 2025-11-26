package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.application.service.IUsuarioService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IUsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController).build();
        new ObjectMapper();
    }

    @Test
    void listar_deberiaResponderExitosamente() throws Exception {
        Usuario u1 = new Usuario();
        u1.setId(1L);
        u1.setNombre("Juan");
        u1.setCorreo("juan@test.com");

        Usuario u2 = new Usuario();
        u2.setId(2L);
        u2.setNombre("Maria");
        u2.setCorreo("maria@test.com");

        when(usuarioService.listar()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[1].nombre").value("Maria"));
    }

    @Test
    void crear_deberiaResponderExitosamente() throws Exception {
        String requestBody = """
                {
                    "nombre": "Carlos",
                    "correo": "carlos@test.com",
                    "contrasena": "password123",
                    "rol": "CLIENTE"
                }
                """;

        when(usuarioService.crear(any(Usuario.class)))
                .thenReturn(OperationResult.success("Usuario creado"));

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Carlos"));
    }
}