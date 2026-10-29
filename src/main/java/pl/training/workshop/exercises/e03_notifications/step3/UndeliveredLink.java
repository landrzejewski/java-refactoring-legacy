package pl.training.workshop.exercises.e03_notifications.step3;

import pl.training.workshop.exercises.e03_notifications.Customer;

/**
 * Krok 3: jawny koniec lancucha. Zamiast "wypadniecia" z ostatniego else wpis o niedostarczeniu
 * jest ogniwem - mozna go podmienic (np. na zgloszenie do kasy) bez edycji pozostalych.
 */
public final class UndeliveredLink implements Channel {
    private final Journal journal;

    public UndeliveredLink(Journal journal) {
        this.journal = journal;
    }

    @Override
    public void deliver(Customer customer, String subject, String body) {
        journal.note("UNDELIVERED " + customer.name() + " (brak kanalu)");
    }
}
