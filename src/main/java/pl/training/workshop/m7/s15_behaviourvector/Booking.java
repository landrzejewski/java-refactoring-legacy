package pl.training.workshop.m7.s15_behaviourvector;

import pl.training.workshop.shared.Money;

/** Stabilny kontrakt sceny: rezerwacja - mutowalny stan, który też należy do wektora zachowania. */
public final class Booking {
    private final String id;
    private final String email;
    private final Money amount;
    private BookingStatus status;

    public Booking(String id, String email, Money amount, BookingStatus status) {
        this.id = id;
        this.email = email;
        this.amount = amount;
        this.status = status;
    }

    public String id() {
        return id;
    }

    public String email() {
        return email;
    }

    public Money amount() {
        return amount;
    }

    public BookingStatus status() {
        return status;
    }

    public void markPaid() {
        status = BookingStatus.PAID;
    }
}
