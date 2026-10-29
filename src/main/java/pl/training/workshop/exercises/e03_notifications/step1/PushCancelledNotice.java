package pl.training.workshop.exercises.e03_notifications.step1;

import pl.training.workshop.exercises.e03_notifications.Customer;
import pl.training.workshop.exercises.e03_notifications.Screening;

/** Krok 1: powiadomienie push o odwolaniu seansu - szosta klasa z tej samej siatki kanal x rodzaj. */
public final class PushCancelledNotice {
    private final Journal journal;
    private final Screening screening;
    private final String reason;

    public PushCancelledNotice(Journal journal, Screening screening, String reason) {
        this.journal = journal;
        this.screening = screening;
        this.reason = reason;
    }

    public void send(Customer customer) {
        String subject = "Odwolany seans: " + screening.title();
        String body = "Seans " + screening.title() + " z " + screening.start() + " odwolany (" + reason
                + "). Zwrot w ciagu 7 dni.";
        journal.push(customer.pushToken(), subject, body);
    }
}
