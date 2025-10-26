package com.urbancollection.ecommerce.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.urbancollection.ecommerce")
@EntityScan(basePackages = "com.urbancollection.ecommerce.domain.entity")
@EnableJpaRepositories(basePackages = "com.urbancollection.ecommerce.persistence.jpa.spring")
public class EcommerceApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApiApplication.class, args);
    }
}
