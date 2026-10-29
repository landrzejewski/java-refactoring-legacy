package pl.training.workshop.exercises.e03_notifications.step3;

import pl.training.workshop.exercises.e03_notifications.Customer;

/** Krok 3: kanal push. */
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
