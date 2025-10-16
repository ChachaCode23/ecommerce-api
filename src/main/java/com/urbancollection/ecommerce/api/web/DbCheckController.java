package com.urbancollection.ecommerce.api.web;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 *  aqui se expone un endpoint simple para verificar la conexion a la base de datos
 *    y server, database, schema, conteo de productos.
 *
 * Notas:
 * Usa JdbcTemplate para ejecutar consultas SQL directas.
 * Pensado para diagnostico/healthcheck en ambiente de desarrollo.
 * En produccion, proteger este endpoint o removerlo.
 */
@RestController
public class DbCheckController {

    // Dependencia de acceso a datos con JDBC inyectada por constructor.
    private final JdbcTemplate jdbc;

    public DbCheckController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * GET /db/check
     * Retorna informacion basica de la BD y un conteo de registros en core.Producto.
     *
     * Consultas usadas (SQL Server):
     *  - DB_NAME()        -> nombre de la base de datos actual
     *  - SCHEMA_NAME()    -> nombre del schema actual
     *  - @@SERVERNAME     -> nombre del servidor SQL
     *  - SELECT COUNT(*) FROM core.Producto -> conteo de filas en la tabla Producto (schema core)
     *
     * Posibles errores:
     *  - Si la tabla o el schema no existen, lanzara una excepcion al ejecutar COUNT(*).
     *  - Si no hay conexion, JdbcTemplate lanzara una DataAccessException.
     */
    @GetMapping("/db/check")
    public Map<String, Object> check() {
        String dbName     = jdbc.queryForObject("SELECT DB_NAME()", String.class);
        String schema     = jdbc.queryForObject("SELECT SCHEMA_NAME()", String.class);
        String server     = jdbc.queryForObject("SELECT @@SERVERNAME", String.class);
        Integer productos = jdbc.queryForObject("SELECT COUNT(*) FROM core.Producto", Integer.class);

        // Construimos una respuesta inmutable con la info recolectada.
        return Map.of(
                "server", server,
                "database", dbName,
                "schema", schema,
                "producto_count", productos
        );
    }
}
