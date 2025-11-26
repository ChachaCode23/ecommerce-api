package com.urbancollection.ecommerce.api.web;

import com.urbancollection.ecommerce.application.service.IDireccionService;
import com.urbancollection.ecommerce.domain.base.OperationResult;
import com.urbancollection.ecommerce.domain.entity.logistica.Direccion;
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
class DireccionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IDireccionService direccionService;

    @InjectMocks
    private DireccionController direccionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(direccionController).build();
        new ObjectMapper();
    }

    @Test
    void listar_deberiaRetornarListaDireccionesYStatus200() throws Exception {
        Direccion d1 = new Direccion();
        d1.setId(1L);
        d1.setCalle("Calle 1");
        d1.setCiudad("Santo Domingo");
        d1.setProvincia("Distrito Nacional");
        d1.setCodigoPostal("10101");

        Direccion d2 = new Direccion();
        d2.setId(2L);
        d2.setCalle("Calle 2");
        d2.setCiudad("Santiago");
        d2.setProvincia("Santiago");
        d2.setCodigoPostal("51000");

        when(direccionService.listar()).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/api/direcciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].calle").value("Calle 1"))
                .andExpect(jsonPath("$[1].calle").value("Calle 2"));
    }

    @Test
    void crear_deberiaGuardarYRetornarDireccionConStatus201() throws Exception {
        String requestBody = """
                {
                    "calle": "Av. Winston Churchill 1100",
                    "ciudad": "Santo Domingo",
                    "provincia": "Distrito Nacional",
                    "codigoPostal": "10107"
                }
                """;

        when(direccionService.crear(any(Direccion.class)))
                .thenReturn(OperationResult.success("Dirección creada"));

        mockMvc.perform(post("/api/direcciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.calle").value("Av. Winston Churchill 1100"));
    }
}