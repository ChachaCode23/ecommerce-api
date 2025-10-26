package com.urbancollection.ecommerce.infrastructure.idempotency;

import com.urbancollection.ecommerce.shared.idempotency.IdempotencyPort;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * InMemoryIdempotencyStore
 *
 * Esta clase es una memoria rápida para detectar si ya se procesó
 * una acción que no debe repetirse (por ejemplo, confirmar el mismo pago dos veces).
 *
 * Funciona guardando llaves (keys) temporalmente con una expiración (TTL).
 * Si la misma key vuelve dentro del TTL, ya la considero "vista".
 *
 */
public class InMemoryIdempotencyStore implements IdempotencyPort {

    // Entry guarda hasta qué momento (en ms) la key es válida.
    private static final class Entry {
        long expiresAt;
        Entry(long e){ this.expiresAt = e; }
    }

    // seen = mapa de key -> Entry (con su expiración)
    private final Map<String, Entry> seen = new ConcurrentHashMap<>();

    /**
     * wasSeen:
     * Verifica si esa key ya fue usada recientemente.
     * - true  -> ya se vio (no lo repitas)
     * - false -> no se ha visto o ya expiró
     *
     * También limpia las llaves que ya expiraron.
     */
    @Override
    public boolean wasSeen(String key) {
        if (key == null || key.isBlank()) return false;

        Entry e = seen.get(key);
        if (e == null) return false;

        // si ya venció, la borro y digo que no se ha visto
        if (System.currentTimeMillis() > e.expiresAt) {
            seen.remove(key);
            return false;
        }

        // sigue siendo válida → ya se había visto
        return true;
    }

    /**
     * remember:
     * Marca una key como "ya usada" por cierto tiempo (ttlMs).
     * Así si el cliente reintenta la misma operación rápido,
     * yo sé que es duplicado.
     *
     * También le pongo un TTL mínimo de 1 segundo para evitar 0.
     */
    @Override
    public void remember(String key, long ttlMs) {
        if (key == null || key.isBlank()) return;

        long exp = System.currentTimeMillis() + Math.max(ttlMs, 1000L);
        seen.put(key, new Entry(exp));
    }
}
