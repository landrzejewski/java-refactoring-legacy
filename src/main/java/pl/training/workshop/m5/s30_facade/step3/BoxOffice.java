package pl.training.workshop.m5.s30_facade.step3;

import java.util.Objects;

import pl.training.workshop.m5.s30_facade.TicketOrder;
import pl.training.workshop.shared.Money;

/**
 * Krok 3: bez zmian - kasa korzysta z tej samej fasady co sklep, bez opłaty rezerwacyjnej.
 * Skopiowana orkiestracja zniknęła razem z jej błędem (brak zwolnienia miejsc po odmowie).
 */
public final class BoxOffice {
    private final BookingFacade booking;

    public BoxOffice(BookingFacade booking) {
        this.booking = Objects.requireNonNull(booking, "booking");
    }

    public String sell(TicketOrder order) {
        return booking.book(order, Money.ZERO);
    }
}
