
// Esto es básicamente la "puerta de entrada" de mi aplicacion Spring Boot.
package com.urbancollection.ecommerce.api;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// esto levanta la aplicación completa de Spring Boot.
@SpringBootApplication

// @ComponentScan le digo a Spring en qué paquete tiene que buscar
// mis componentes (controllers, services, etc.).
// Yo le digo que busque en "com.urbancollection.ecommerce" completo,
// así él encuentra todas las clases que yo anoto con @Service, @Controller, etc.
@ComponentScan(basePackages = "com.urbancollection.ecommerce")

// @EntityScan le dice a Spring dónde están mis entidades JPA.
// O sea, mis clases que representan tablas (por ejemplo Usuario, Producto, Pedido...)
// para que las pueda mapear a la base de datos.
@EntityScan(basePackages = "com.urbancollection.ecommerce.domain.entity")

// @EnableJpaRepositories le dice dónde están mis repositorios JPA.
// Los repositorios son las interfaces que hablan directo con la base de datos
// (por ejemplo ProductoRepository, UsuarioRepository, etc.).
// Básicamente aquí habilito que Spring cree las implementaciones de esas interfaces.
@EnableJpaRepositories(basePackages = "com.urbancollection.ecommerce.persistence.jpa.spring")
public class EcommerceApiApplication {

    // Este es el main, el punto de arranque de la app.
    // Cuando yo corro el proyecto, SpringApplication.run(...) levanta el servidor,
    // registra todos los beans, conecta con la BD, expone los endpoints REST, etc.
    public static void main(String[] args) {
        SpringApplication.run(EcommerceApiApplication.class, args);
    }
}
