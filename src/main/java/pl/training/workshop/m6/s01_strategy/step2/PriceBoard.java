package pl.training.workshop.m6.s01_strategy.step2;

import pl.training.workshop.m6.s01_strategy.PriceRequest;
import pl.training.workshop.shared.Money;

/** Krok 2: klient bez zmian. */
public final class PriceBoard {
    private final TicketPricer pricer = new TicketPricer();

    public Money priceFor(PriceRequest request) {
        return pricer.price(request.base(), request.ticketType(), request.program());
    }
}
