package pl.training.workshop.m8.s02_stranglerfig.step4;

import pl.training.workshop.m8.s02_stranglerfig.BookingLedger;
import pl.training.workshop.shared.Money;

/** Krok 4 (bez zmian): nowy moduł rezerwacji - Money, nazwane reguły, wspólna baza z legacy. */
public final class BookingModule {
    private static final Money ONLINE_FEE = Money.of("2.00");
    private static final int GROUP_SIZE = 10;

    private final BookingLedger ledger;

    public BookingModule(BookingLedger ledger) {
        this.ledger = ledger;
    }

    public String book(String email, String title, int format, int tickets, boolean web) {
        if (tickets <= 0) {
            return "ERROR: no seats";
        }
        Money value = basePrice(format).times(tickets);
        if (tickets >= GROUP_SIZE) {
            value = value.minus(value.percent(10));
        }
        Money fees = web ? ONLINE_FEE.times(tickets) : Money.ZERO;
        String id = ledger.nextId();
        ledger.add(new BookingLedger.Booking(id, email, title, tickets, value, fees));
        return id;
    }

    private static Money basePrice(int format) {
        return switch (format) {
            case 1 -> Money.of("25.00");
            case 2 -> Money.of("32.00");
            default -> Money.of("40.00");
        };
    }
}
