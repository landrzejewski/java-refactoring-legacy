package pl.training.workshop.m7.s03_breakresponsibilities;

import java.util.ArrayList;
import java.util.List;

/** Stabilny kontrakt sceny: skrzynka nadawcza - efekt uboczny, który test obserwuje. */
public final class Outbox {
    private final List<String> sent = new ArrayList<>();

    public void send(String to, String text) {
        sent.add(to + ": " + text);
    }

    public List<String> sent() {
        return List.copyOf(sent);
    }
}
