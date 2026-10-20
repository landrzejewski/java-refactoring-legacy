package pl.training.workshop.m5.s30_facade.step3;

import java.util.ArrayList;
import java.util.List;

/** Krok 3: podsystem ukryty (package-private) - wysyłka biletów mailem. */
final class TicketMailer {
    private final List<String> sent = new ArrayList<>();

    void send(String email, String text) {
        sent.add(email + ": " + text);
    }

    List<String> sent() {
        return List.copyOf(sent);
    }
}
