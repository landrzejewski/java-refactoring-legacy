package pl.training.workshop.m5.s30_facade.start;

import java.util.ArrayList;
import java.util.List;

/** Podsystem: wysyłka biletów mailem. */
public final class TicketMailer {
    private final List<String> sent = new ArrayList<>();

    public void send(String email, String text) {
        sent.add(email + ": " + text);
    }

    public List<String> sent() {
        return List.copyOf(sent);
    }
}
