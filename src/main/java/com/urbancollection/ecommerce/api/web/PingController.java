package com.urbancollection.ecommerce.api.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//Expone endpoints muy simples para verificar que la API esta viva.

@RestController
public class PingController {

  //Devuelve un texto fijo "pong" para confirmar respuesta del servidor.

  @GetMapping("/ping")
  public String ping() { return "pong"; }
  
  //Ruta raiz de la API. Responde con un texto simple de estado.
  
  @GetMapping("/")
  public String root() { return "API online"; }

}
