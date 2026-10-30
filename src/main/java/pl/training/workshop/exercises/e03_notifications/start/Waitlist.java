package pl.training.workshop.exercises.e03_notifications.start;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.exercises.e03_notifications.Customer;

/** Start: lista oczekujacych na bilety - po przeniesieniu zachowuje kolejnosc, po odwolaniu jest czyszczona. */
public final class Waitlist implements ScreeningListener {
    private final Journal journal;
    private final List<Customer> waiting;

    public Waitlist(Journal journal, List<Customer> waiting) {
        this.journal = journal;
        this.waiting = new ArrayList<>(waiting);
    }

    @Override
    public void onMoved(ScreeningMoved event) {
        journal.note("WAITLIST " + event.after().title() + ": " + waiting.size() + " oczekujacych zachowuje kolejnosc");
    }

    @Override
    public void onCanceled(ScreeningCancelled event) {
        journal.note("WAITLIST " + event.screening().title() + ": lista wyczyszczona (" + waiting.size() + ")");
        waiting.clear();
    }

}
