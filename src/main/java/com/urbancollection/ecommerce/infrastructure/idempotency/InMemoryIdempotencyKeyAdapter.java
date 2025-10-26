package com.urbancollection.ecommerce.infrastructure.idempotency;

import com.urbancollection.ecommerce.shared.idempotency.IdempotencyKeyPort;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InMemoryIdempotencyKeyAdapter
 *
 * Esta clase es una implementación en memoria de control de idempotencia.
 * sirve para evitar que la misma operación (ej: confirmar pago)
 * se ejecute dos veces por error si el cliente repite la misma petición.
 *
 */
public class InMemoryIdempotencyKeyAdapter implements IdempotencyKeyPort {

    // Clase interna que guarda hasta cuándo es válida una llave.
    private static class Entry {
        final Instant expiresAt;
        Entry(Instant t){ this.expiresAt = t; }
    }

    // Mapa en memoria:
    // scope -> ( key -> Entry )
    // "scope" se usa para separar tipos de operación (por ejemplo "pagoPedido").
    private final Map<String, Map<String, Entry>> data = new ConcurrentHashMap<>();

    /**
     * tryUse:
     *
     * Intenta registrar una clave de idempotencia.
     * - scope: categoría (por ejemplo "confirmarPago").
     * - key:   la llave única que manda el cliente.
     * - ttlSeconds: cuánto tiempo debe considerarse "ocupada" esa key.
     *
     * Comportamiento:
     * - Si la key NO se ha usado todavía en ese scope → la guardo y devuelvo true.
     * - Si ya se había usado → devuelvo false (o sea, repetida).
     *
     * También limpio las llaves que ya expiraron antes de decidir.
     */
    @Override
    public boolean tryUse(String scope, String key, long ttlSeconds) {
        if (scope == null || key == null || ttlSeconds <= 0) return false;

        var scopeMap = data.computeIfAbsent(scope, s -> new ConcurrentHashMap<>());
        var now = Instant.now();

        // Limpieza rápida de entradas expiradas
        scopeMap.entrySet().removeIf(e -> e.getValue().expiresAt.isBefore(now));

        // putIfAbsent devuelve null si la key no existía (o sea primera vez)
        var prev = scopeMap.putIfAbsent(key, new Entry(now.plusSeconds(ttlSeconds)));
        return prev == null;
    }

    /**
     * wasSeen:
     * Chequea si ya vimos una key antes.
     * Este método todavía no está implementado.
     */
    @Override
    public boolean wasSeen(String key) {
        // TODO: implementar si se necesita ese lookup directo por key global
        return false;
    }

    /**
     * remember:
     * Guarda una key manualmente con TTL.
     * Este método también está pendiente.
     */
    @Override
    public void remember(String key, long ttlMs) {
        // TODO: implementación pendiente
    }
}
