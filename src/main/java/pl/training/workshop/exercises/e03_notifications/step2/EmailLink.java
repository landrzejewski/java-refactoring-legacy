package pl.training.workshop.exercises.e03_notifications.step2;

import java.util.Objects;

import pl.training.workshop.exercises.e03_notifications.Customer;

/** Krok 2: ogniwo e-mail - obsluguje klienta z adresem. */
public final class EmailLink implements Channel {
    private final EmailChannel email;
    private final Channel next;

    public EmailLink(EmailChannel email, Channel next) {
        this.email = Objects.requireNonNull(email, "email");
        this.next = Objects.requireNonNull(next, "next");
    }

    @Override
    public void deliver(Customer customer, String subject, String body) {
        if (customer.email() != null && !customer.email().isBlank()) {
            email.deliver(customer, subject, body);
        } else {
            next.deliver(customer, subject, body);
        }
    }
}
