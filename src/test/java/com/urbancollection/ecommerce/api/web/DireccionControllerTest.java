package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.domain.entity.logistica.Direccion;
import com.urbancollection.ecommerce.domain.repository.DireccionRepository;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DireccionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DireccionRepository direccionRepository;

    @InjectMocks
    private DireccionController direccionController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(direccionController).build();
        objectMapper = new ObjectMapper();
    }

    // =========================
    // GET /api/direcciones
    // =========================
    @Test
    void listar_deberiaRetornarListaDireccionesYStatus200() throws Exception {
        Direccion d1 = new Direccion();
        d1.setCalle("Calle 1");
        d1.setCiudad("Santo Domingo");
        d1.setProvincia("DN");
        d1.setCodigoPostal("10101");

        Direccion d2 = new Direccion();
        d2.setCalle("Calle 2");
        d2.setCiudad("Santiago");
        d2.setProvincia("Santiago");
        d2.setCodigoPostal("51000");

        List<Direccion> direcciones = Arrays.asList(d1, d2);
        when(direccionRepository.findAll()).thenReturn(direcciones);

        mockMvc.perform(get("/api/direcciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].calle").value("Calle 1"))
                .andExpect(jsonPath("$[0].ciudad").value("Santo Domingo"))
                .andExpect(jsonPath("$[1].calle").value("Calle 2"));
    }

    // =========================
    // POST /api/direcciones
    // =========================
    @Test
    void crear_deberiaGuardarYRetornarDireccionConStatus201() throws Exception {
        // Request que recibe el controller
        DireccionController.CrearDireccionRequest request =
                new DireccionController.CrearDireccionRequest();
        request.setCalle("Av. México");
        request.setCiudad("Santo Domingo");
        request.setProvincia("DN");
        request.setCodigoPostal("10102");

        // Lo que devuelve el repositorio al guardar
        Direccion guardada = new Direccion();
        guardada.setCalle("Av. México");
        guardada.setCiudad("Santo Domingo");
        guardada.setProvincia("DN");
        guardada.setCodigoPostal("10102");

        when(direccionRepository.save(any(Direccion.class))).thenReturn(guardada);

        mockMvc.perform(post("/api/direcciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.calle").value("Av. México"))
                .andExpect(jsonPath("$.ciudad").value("Santo Domingo"))
                .andExpect(jsonPath("$.provincia").value("DN"))
                .andExpect(jsonPath("$.codigoPostal").value("10102"));
    }
}
