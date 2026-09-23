package pl.training.workshop.m8.s07_adr.start.notification;

import java.util.ArrayList;
import java.util.List;

/** Start: powiadomienia o rabacie grupowym (poza cennikiem - ale cennik je woła). */
public final class GroupMailer {
    private final List<String> sent = new ArrayList<>();

    public void groupDiscountGranted(String organizer, int tickets) {
        sent.add(organizer + ": rabat grupowy dla " + tickets + " biletow");
    }

    public List<String> sent() {
        return List.copyOf(sent);
    }
}
