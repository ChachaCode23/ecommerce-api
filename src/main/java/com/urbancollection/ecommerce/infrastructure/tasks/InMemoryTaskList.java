package com.urbancollection.ecommerce.infrastructure.tasks;

import com.urbancollection.ecommerce.shared.tasks.TaskListPort;
import java.util.concurrent.ConcurrentLinkedQueue;

// PENDING: implementar consumidor/worker real. Ahora solo loguea.
public class InMemoryTaskList implements TaskListPort {
    private final ConcurrentLinkedQueue<String> q = new ConcurrentLinkedQueue<>();
    @Override
    public void enqueue(String type, String payloadJson) {
        q.add(type + "::" + payloadJson);
        System.out.println("[MOCK-TASK] queued type=" + type + " payload=" + payloadJson);
    }
}
