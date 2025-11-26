package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.domain.entity.usuarios.Usuario;
import com.urbancollection.ecommerce.persistence.jpa.spring.UsuarioJpaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class UsuarioWebControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UsuarioJpaRepository usuarioRepository;

    @InjectMocks
    private UsuarioWebController usuarioWebController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usuarioWebController).build();
    }

    @Test
    void listar_cuandoTodoOk_deberiaMostrarListaUsuarios() throws Exception {
        when(usuarioRepository.findAll()).thenReturn(Collections.singletonList(new Usuario()));

        mockMvc.perform(get("/web/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuario/list"))
                .andExpect(model().attributeExists("usuarios"));
    }

    @Test
    void listar_cuandoRepositoryLanzaError_deberiaMostrarMensajeError() throws Exception {
        when(usuarioRepository.findAll()).thenThrow(new RuntimeException("fallo inesperado"));

        mockMvc.perform(get("/web/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuario/list"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void mostrarFormularioCrear_deberiaCargarValoresPorDefecto() throws Exception {
        mockMvc.perform(get("/web/usuarios/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuario/create"))
                .andExpect(model().attribute("nombre", ""))
                .andExpect(model().attribute("email", ""))
                .andExpect(model().attribute("telefono", ""));
    }
}