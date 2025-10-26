package com.urbancollection.ecommerce.api.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PingController
 *
 * Este controlador es para chequeos rápidos de que la API está viva.
 * No tiene lógica de negocio ni habla con la base de datos.
 *
 * Sirve para dos cosas:
 *  - Ver si el servidor está respondiendo.
 *  - Tener una ruta raíz que te diga que la API levantó bien.
 *
 */
@RestController
public class PingController {

  /**
   * GET /ping
   *
   * Esta ruta devuelve literalmente el string "pong".
   * Es el típico ping/pong de salud.
   *
   * Ejemplo:
   *   GET http://localhost:8080/ping
   *   Respuesta: pong
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
      return "pong";
  }

  /**
   * GET /
   *
   * Esta es la ruta raíz de la API.
   * Devuelve un mensaje corto diciendo "API online".
   *
   * Ejemplo:
   *   GET http://localhost:8080/
   *   Respuesta: API online
   *
   * Esto ayuda:
   *   cuando abres el navegador sin ruta, ves que el backend sí levantó.
   *   también lo puedes usar como healthcheck súper básico en desarrollo.
   *

   */
  @GetMapping("/")
  public String root() {
      return "API online";
  }

}
