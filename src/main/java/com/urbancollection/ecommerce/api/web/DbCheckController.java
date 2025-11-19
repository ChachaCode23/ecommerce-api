package com.urbancollection.ecommerce.api.web;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Este controlador es básicamente un "chequeo rápido" de la base de datos.
 *
 * La idea de esto es  saber si la API está conectando bien
 * a la base de datos y que la tabla principal Producto existe y tiene datos.
 *
 * Este endpoint NO es para el usuario final, es más para desarrollo / pruebas.
 *
 * Nota importante:
 * En producción esto hay que bloquearlo o borrarlo, porque aquí estoy
 * exponiendo información interna de la base de datos (nombre del server, etc.).
 */
@RestController
public class DbCheckController {

    // JdbcTemplate es una clase que me da Spring para tirar SQL directo.
    // Aquí yo la guardo como dependencia del controlador.
    // Spring la inyecta en el constructor.
    private final JdbcTemplate jdbc;

    public DbCheckController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Endpoint GET /db/check
     *
     * Cuando llamo a /db/check, yo devuelvo un JSON con:
     *  - el nombre del servidor SQL
     *  - el nombre de la base de datos
     *  - el schema actual
     *  - cuántos productos hay en la tabla core.Producto
     *
     * hago esto
     * - Para confirmar rápido que la API sí está hablando con la BD correcta.
     * - Para confirmar que el schema "core" existe.
     * - Para confirmar que la tabla Producto está creada y accesible.
     *
     *
     * Errores que pueden pasar:
     * - Si no hay conexión a BD, JdbcTemplate va a tirar una excepción.
     * - Si la tabla core.Producto no existe, también va a fallar.
     *
     * De esa forma si este endpoint truena yo sé de una vez
     * si el problema es que la app no levantó BD,
     * que no migraron las tablas,
     * o que estoy apuntando a otra base.
     */
    @GetMapping("/db/check")
    public Map<String, Object> check() {

        // Pregunto el nombre de la base actual.
        String dbName = jdbc.queryForObject(
                "SELECT DB_NAME()",
                String.class
        );

        // Pregunto el schema actual (por ejemplo 'core').
        String schema = jdbc.queryForObject(
                "SELECT SCHEMA_NAME()",
                String.class
        );

        // Pregunto el nombre del servidor SQL al que estoy conectado.
        String server = jdbc.queryForObject(
                "SELECT @@SERVERNAME",
                String.class
        );

        // cantidad de  registros hay en la tabla core.Producto.
        // Esto me confirma dos cosas al mismo tiempo:
        // 1. Que la tabla existe.
        // 2. Que se está leyendo bien.
        Integer productos = jdbc.queryForObject(
                "SELECT COUNT(*) FROM core.Producto",
                Integer.class
        );

        // Devuelvo un JSON simple con toda la info.
        // Map.of(...) me deja armarlo rápido tipo clave -> valor.
        // Ejemplo de respuesta:
        // {
        //   "server": "MI-SQLSERVER",
        //   "database": "ECommerceDB",
        //   "schema": "core",
        //   "producto_count": 42
        // }
        return Map.of(
                "server", server,
                "database", dbName,
                "schema", schema,
                "producto_count", productos
        );
    }
}
