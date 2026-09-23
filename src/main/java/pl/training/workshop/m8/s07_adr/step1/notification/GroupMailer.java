package pl.training.workshop.m8.s07_adr.step1.notification;

import java.util.ArrayList;
import java.util.List;

/** Krok 1 (bez zmian): powiadomienia - wołane już tylko przez warstwę aplikacji. */
public final class GroupMailer {
    private final List<String> sent = new ArrayList<>();

    public void groupDiscountGranted(String organizer, int tickets) {
        sent.add(organizer + ": rabat grupowy dla " + tickets + " biletow");
    }

    public List<String> sent() {
        return List.copyOf(sent);
    }
}
