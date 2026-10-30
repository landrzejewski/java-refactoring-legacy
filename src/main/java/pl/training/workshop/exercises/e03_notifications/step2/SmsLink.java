package pl.training.workshop.exercises.e03_notifications.step2;

import java.util.Objects;

import pl.training.workshop.exercises.e03_notifications.Customer;

/** Krok 2: ogniwo SMS - obsluguje klienta z numerem telefonu. */
public final class SmsLink implements Channel {
    private final SmsChannel sms;
    private final Channel next;

    public SmsLink(SmsChannel sms, Channel next) {
        this.sms = Objects.requireNonNull(sms, "sms");
        this.next = Objects.requireNonNull(next, "next");
    }

    @Override
    public void deliver(Customer customer, String subject, String body) {
        if (customer.phone() != null && !customer.phone().isBlank()) {
            sms.deliver(customer, subject, body);
        } else {
            next.deliver(customer, subject, body);
        }
    }
}
