package pl.training.workshop.m5.s30_facade.step2;

import java.util.Objects;

import pl.training.workshop.m5.s30_facade.TicketOrder;
import pl.training.workshop.shared.Money;

/** Krok 2: bez zmian - sklep deleguje cały zakup do fasady; zna już tylko swoją opłatę rezerwacyjną. */
public final class OnlineShop {
    private static final Money BOOKING_FEE = Money.of("2.00");

    private final BookingFacade booking;

    public OnlineShop(BookingFacade booking) {
        this.booking = Objects.requireNonNull(booking, "booking");
    }

    public String buy(TicketOrder order) {
        return booking.book(order, BOOKING_FEE);
    }
}
