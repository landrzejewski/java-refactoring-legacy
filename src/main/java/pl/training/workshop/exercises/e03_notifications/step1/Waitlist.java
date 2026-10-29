package pl.training.workshop.exercises.e03_notifications.step1;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.exercises.e03_notifications.Customer;

/** Krok 1: lista oczekujacych jako odbiorca zdarzen - ta sama logika, inna sygnatura. */
public final class Waitlist implements ScreeningListener {
    private final Journal journal;
    private final List<Customer> waiting;

    public Waitlist(Journal journal, List<Customer> waiting) {
        this.journal = journal;
        this.waiting = new ArrayList<>(waiting);
    }

    @Override
    public void screeningMoved(ScreeningMoved event) {
        journal.note("WAITLIST " + event.after().title() + ": " + waiting.size() + " oczekujacych zachowuje kolejnosc");
    }

    @Override
    public void screeningCancelled(ScreeningCancelled event) {
        journal.note("WAITLIST " + event.screening().title() + ": lista wyczyszczona (" + waiting.size() + ")");
        waiting.clear();
    }
}
