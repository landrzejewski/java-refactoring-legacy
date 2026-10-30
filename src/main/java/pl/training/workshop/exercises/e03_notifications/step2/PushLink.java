package pl.training.workshop.exercises.e03_notifications.step2;

import java.util.Objects;

import pl.training.workshop.exercises.e03_notifications.Customer;

/**
 * Krok 2: pierwsze ogniwo dostarczania. Obsluguje klienta z waznym tokenem push, resztę oddaje nastepnikowi.
 * Ogniwo nie wie, co jest dalej - kolejnosc sklada korzen kompozycji.
 */
public final class PushLink implements Channel {
    private final PushChannel push;
    private final Channel next;

    public PushLink(PushChannel push, Channel next) {
        this.push = Objects.requireNonNull(push, "push");
        this.next = Objects.requireNonNull(next, "next");
    }

    @Override
    public void deliver(Customer customer, String subject, String body) {
        String token = customer.pushToken();
        if (token != null && !token.isBlank() && !token.startsWith("expired-")) {
            push.deliver(customer, subject, body);
        } else {
            next.deliver(customer, subject, body);
        }
    }
}
