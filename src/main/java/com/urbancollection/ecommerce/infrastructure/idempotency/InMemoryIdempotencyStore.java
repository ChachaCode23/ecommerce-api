package com.urbancollection.ecommerce.infrastructure.idempotency;

import com.urbancollection.ecommerce.shared.idempotency.IdempotencyPort;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// TODO(PENDING): usar Redis o DB con expiración real en producción.
public class InMemoryIdempotencyStore implements IdempotencyPort {
    private static final class Entry { long expiresAt; Entry(long e){ this.expiresAt = e; } }
    private final Map<String, Entry> seen = new ConcurrentHashMap<>();

    @Override
    public boolean wasSeen(String key) {
        if (key == null || key.isBlank()) return false;
        Entry e = seen.get(key);
        if (e == null) return false;
        if (System.currentTimeMillis() > e.expiresAt) {
            seen.remove(key);
            return false;
        }
        return true;
    }

    @Override
    public void remember(String key, long ttlMs) {
        if (key == null || key.isBlank()) return;
        long exp = System.currentTimeMillis() + Math.max(ttlMs, 1000L);
        seen.put(key, new Entry(exp));
    }
}
