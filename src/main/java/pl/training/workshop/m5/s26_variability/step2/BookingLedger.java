package pl.training.workshop.m5.s26_variability.step2;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import pl.training.workshop.m5.s26_variability.Status;
import pl.training.workshop.shared.Money;

/**
 * Krok 2: odbiorca poleceń - rezerwacje i kasa wydzielone z pulpitu (Extract Class).
 * Cykl życia nadal jest warunkami po statusie w każdej metodzie - to zapach na krok 3.
 */
public final class BookingLedger {
    private final Map<String, Status> statuses = new LinkedHashMap<>();
    private final Map<String, Money> amounts = new LinkedHashMap<>();
    private Money till = Money.ZERO;

    void open(String id, Money amount) {
        if (statuses.containsKey(id)) {
            throw new IllegalArgumentException("booking exists: " + id);
        }
        statuses.put(id, Status.NEW);
        amounts.put(id, amount);
    }

    void remove(String id) {
        statuses.remove(id);
        amounts.remove(id);
    }

    Status status(String id) {
        Status status = statuses.get(id);
        if (status == null) {
            throw new IllegalArgumentException("no booking " + id);
        }
        return status;
    }

    Money pay(String id) {
        Status status = status(id);
        if (status != Status.NEW) {
            throw invalid("pay", status);
        }
        till = till.plus(amounts.get(id));
        statuses.put(id, Status.PAID);
        return amounts.get(id);
    }

    void use(String id) {
        Status status = status(id);
        if (status != Status.PAID) {
            throw invalid("use", status);
        }
        statuses.put(id, Status.USED);
    }

    Money cancel(String id) {
        Status status = status(id);
        if (status == Status.NEW) {
            statuses.put(id, Status.CANCELLED);
            return Money.ZERO;
        } else if (status == Status.PAID) {
            till = till.minus(amounts.get(id));
            statuses.put(id, Status.CANCELLED);
            return amounts.get(id);
        }
        throw invalid("cancel", status);
    }

    void restore(String id, Status previous) {
        statuses.put(id, previous);
    }

    void deposit(Money amount) {
        till = till.plus(amount);
    }

    void withdraw(Money amount) {
        till = till.minus(amount);
    }

    String summary() {
        String bookings = statuses.isEmpty() ? "-" : statuses.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", "));
        return "Kasa: " + till + "; " + bookings;
    }

    private static IllegalStateException invalid(String action, Status status) {
        return new IllegalStateException("cannot " + action + " in " + status);
    }
}
