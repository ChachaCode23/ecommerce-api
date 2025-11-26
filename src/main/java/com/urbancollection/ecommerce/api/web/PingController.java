package com.urbancollection.ecommerce.api.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PingController
 *
 * Este controlador es para chequeos rápidos de que la API está viva.
 * No tiene lógica de negocio ni habla con la base de datos.
 *  Veo si el servidor está respondiendo.
 *  Tiene una ruta raíz que te diga que la API levantó bien.
 */
@RestController
public class PingController {

  // Controlador súper simple: solo expone un endpoint para verificar que la API responde.

  /**
   * GET /ping
   *
   * Esta ruta devuelve literalmente el string "pong".
   * Es el típico ping/pong de salud.
   *
   *
   * Si esto responde "pong", ya sabemos que:
   *   - el servidor Spring Boot está corriendo
   *   - llegó el request
   *   - salió la respuesta
   *
   * O sea, mínimo la API no está caída.
   */
  @GetMapping("/ping")
  public String ping() {
      // Respuesta directa sin pasar por servicios ni nada de lógica de negocio.
      return "pong";
  }


}
