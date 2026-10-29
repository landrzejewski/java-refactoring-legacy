package pl.training.workshop.exercises.e03_notifications.step2;

import pl.training.workshop.exercises.e03_notifications.Customer;

/** Krok 2: kanal e-mail - tytul i tresc osobno, bez limitu dlugosci. */
public final class EmailChannel implements Channel {
    private final Journal journal;

    public EmailChannel(Journal journal) {
        this.journal = journal;
    }

    @Override
    public void deliver(Customer customer, String subject, String body) {
        journal.email(customer.email(), subject, body);
    }
}
