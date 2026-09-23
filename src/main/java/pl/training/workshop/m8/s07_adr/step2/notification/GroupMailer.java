package pl.training.workshop.m8.s07_adr.step2.notification;

import java.util.ArrayList;
import java.util.List;

/** Krok 2 (bez zmian): powiadomienia - wołane tylko przez warstwę aplikacji. */
public final class GroupMailer {
    private final List<String> sent = new ArrayList<>();

    public void groupDiscountGranted(String organizer, int tickets) {
        sent.add(organizer + ": rabat grupowy dla " + tickets + " biletow");
    }

    public List<String> sent() {
        return List.copyOf(sent);
    }
}
