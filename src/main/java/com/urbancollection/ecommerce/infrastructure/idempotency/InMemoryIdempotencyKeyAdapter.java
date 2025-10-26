package com.urbancollection.ecommerce.infrastructure.idempotency;

import com.urbancollection.ecommerce.shared.idempotency.IdempotencyKeyPort;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryIdempotencyKeyAdapter implements IdempotencyKeyPort {
    private static class Entry { final Instant expiresAt; Entry(Instant t){ this.expiresAt = t; } }
    // scope -> key -> entry
    private final Map<String, Map<String, Entry>> data = new ConcurrentHashMap<>();

    @Override
    public boolean tryUse(String scope, String key, long ttlSeconds) {
        if (scope == null || key == null || ttlSeconds <= 0) return false;
        var scopeMap = data.computeIfAbsent(scope, s -> new ConcurrentHashMap<>());
        var now = Instant.now();

        // limpia expirados rápido (best-effort)
        scopeMap.entrySet().removeIf(e -> e.getValue().expiresAt.isBefore(now));

        // putIfAbsent retorna null si no existía → OK (primera vez)
        var prev = scopeMap.putIfAbsent(key, new Entry(now.plusSeconds(ttlSeconds)));
        return prev == null;
    }

	@Override
	public boolean wasSeen(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void remember(String key, long ttlMs) {
		// TODO Auto-generated method stub
		
	}
}
