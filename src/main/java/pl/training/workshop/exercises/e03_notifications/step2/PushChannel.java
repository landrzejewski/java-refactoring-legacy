package pl.training.workshop.exercises.e03_notifications.step2;

import pl.training.workshop.exercises.e03_notifications.Customer;

/** Krok 2: kanal push. */
public final class PushChannel implements Channel {
    private final Journal journal;

    public PushChannel(Journal journal) {
        this.journal = journal;
    }

    @Override
    public void deliver(Customer customer, String subject, String body) {
        journal.push(customer.pushToken(), subject, body);
    }
}
