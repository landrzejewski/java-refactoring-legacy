package pl.training.workshop.m6.s01_strategy.step1;

import pl.training.workshop.m6.s01_strategy.PriceRequest;
import pl.training.workshop.shared.Money;

/** Krok 1: klient bez zmian. */
public final class PriceBoard {
    private final TicketPricer pricer = new TicketPricer();

    public Money priceFor(PriceRequest request) {
        return pricer.price(request.base(), request.ticketType(), request.program());
    }
}
