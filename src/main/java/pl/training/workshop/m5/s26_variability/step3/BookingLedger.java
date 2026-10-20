package pl.training.workshop.m5.s26_variability.step3;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import pl.training.workshop.m5.s26_variability.Status;
import pl.training.workshop.shared.Money;

/**
 * Krok 3: odbiorca poleceń bez warunków po statusie - o tym, co wolno i ile pieniędzy się
 * przesuwa, decyduje stan rezerwacji (Booking). Odbiorca prowadzi tylko kasę i rejestr.
 */
public final class BookingLedger {
    private final Map<String, Booking> bookings = new LinkedHashMap<>();
    private Money till = Money.ZERO;

    void open(String id, Money amount) {
        if (bookings.containsKey(id)) {
            throw new IllegalArgumentException("booking exists: " + id);
        }
        bookings.put(id, new Booking(amount));
    }

    void remove(String id) {
        bookings.remove(id);
    }

    Status status(String id) {
        return booking(id).status();
    }

    Money pay(String id) {
        Money paid = booking(id).pay();
        till = till.plus(paid);
        return paid;
    }

    void use(String id) {
        booking(id).use();
    }

    Money cancel(String id) {
        Money refunded = booking(id).cancel();
        till = till.minus(refunded);
        return refunded;
    }

    void restore(String id, Status previous) {
        booking(id).restore(previous);
    }

    void deposit(Money amount) {
        till = till.plus(amount);
    }

    void withdraw(Money amount) {
        till = till.minus(amount);
    }

    String summary() {
        String list = bookings.isEmpty() ? "-" : bookings.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue().status())
                .collect(Collectors.joining(", "));
        return "Kasa: " + till + "; " + list;
    }

    private Booking booking(String id) {
        Booking booking = bookings.get(id);
        if (booking == null) {
            throw new IllegalArgumentException("no booking " + id);
        }
        return booking;
    }
}
