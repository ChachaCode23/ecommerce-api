package com.urbancollection.ecommerce.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * @OpenAPIDefinition:
 *  - Anotacion de Swagger/OpenAPI para documentar la API.
 *  - Define metadatos visibles en la UI de Swagger (título, versión, descripción).
 */
@OpenAPIDefinition(
    info = @Info(
        title = "Urban Collection Store - E-commerce API", // Título  en Swagger UI
        version = "v1.0",                                   // Versión de la API
        description = "API E-commerce: Productos y Pedidos"  // Breve descripción del alcance
    )
)
/**
 * @SpringBootApplication:
 *  - Anotacion “principal” de Spring Boot que combina:
 *      * @Configuration (configuracion de beans)
 *      * @EnableAutoConfiguration (auto-config de Spring Boot)
 *      * @ComponentScan (escaneo de componentes en el paquete base)
 *  - Marca esta clase como punto de arranque de la app.
 */
@SpringBootApplication
public class EcommerceApiApplication {

    /**
     * Metodo main:
     *  - Punto de entrada de la aplicacion Java.
     *  - SpringApplication.run(...) arranca el contexto de Spring,
     *    levanta el servidor embebido (por defecto Tomcat) y expone la API.
     *
     * @param args argumentos de linea de comandos (opcionalmente se pueden usar para perfiles, etc.)
     */
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApiApplication.class, args);
    }
}
