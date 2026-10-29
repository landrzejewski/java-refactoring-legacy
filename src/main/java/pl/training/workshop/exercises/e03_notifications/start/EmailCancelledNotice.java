package pl.training.workshop.exercises.e03_notifications.start;

import pl.training.workshop.exercises.e03_notifications.Customer;
import pl.training.workshop.exercises.e03_notifications.Screening;

/** Start: e-mail o odwolaniu seansu. Nowy rodzaj wiadomosci (np. zmiana sali) to kolejne trzy klasy. */
public final class EmailCancelledNotice {
    private final Journal journal;
    private final Screening screening;
    private final String reason;

    public EmailCancelledNotice(Journal journal, Screening screening, String reason) {
        this.journal = journal;
        this.screening = screening;
        this.reason = reason;
    }

    public void send(Customer customer) {
        String subject = "Odwolany seans: " + screening.title();
        String body = "Seans " + screening.title() + " z " + screening.start() + " odwolany (" + reason
                + "). Zwrot w ciagu 7 dni.";
        journal.email(customer.email(), subject, body);
    }
}
