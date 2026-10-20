package pl.training.workshop.m2.s09_cleanarchitecture;

import java.util.ArrayList;
import java.util.List;

/** Świat zewnętrzny sceny (stabilny): kanał komunikatów (np. broker) - temat i treść. */
public final class Outbox {
    private final List<String> messages = new ArrayList<>();

    public void publish(String topic, String payload) {
        messages.add(topic + ":" + payload);
    }

    public List<String> messages() {
        return List.copyOf(messages);
    }
}
