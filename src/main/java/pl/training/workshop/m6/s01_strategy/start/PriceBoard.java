package pl.training.workshop.m6.s01_strategy.start;

import pl.training.workshop.m6.s01_strategy.PriceRequest;
import pl.training.workshop.shared.Money;

/** Start: klient cennika - kasa wycenia bilet wg programu z konfiguracji. */
public final class PriceBoard {
    private final TicketPricer pricer = new TicketPricer();

    public Money priceFor(PriceRequest request) {
        return pricer.price(request.base(), request.ticketType(), request.program());
    }
}
