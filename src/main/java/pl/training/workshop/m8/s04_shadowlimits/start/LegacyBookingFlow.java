package pl.training.workshop.m8.s04_shadowlimits.start;

import java.math.BigDecimal;

import pl.training.workshop.m8.s04_shadowlimits.BookingRequest;
import pl.training.workshop.m8.s04_shadowlimits.Infrastructure;
import pl.training.workshop.shared.Money;

/** Start: stara ścieżka rezerwacji (double) - autorytatywna, wykonuje prawdziwe efekty. */
public final class LegacyBookingFlow {
    private final Infrastructure infra;

    public LegacyBookingFlow(Infrastructure infra) {
        this.infra = infra;
    }

    public String book(BookingRequest r) {
        double total = 25.00 * r.tickets() + 2.00 * r.tickets();
        Money amount = new Money(BigDecimal.valueOf(total));
        infra.charge(r.card(), amount);
        infra.save(r.title() + ";" + r.email() + ";" + r.tickets() + ";" + amount);
        infra.sendMail(r.email(), "Bilety " + r.title() + " x" + r.tickets() + ", zaplacono " + amount);
        return "OK " + amount;
    }
}
