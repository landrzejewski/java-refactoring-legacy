package pl.training.workshop.m6.s01_strategy.step3;

import pl.training.workshop.m6.s01_strategy.PriceRequest;
import pl.training.workshop.shared.Money;

/** Krok 3: klient składa kontekst ze strategią wybraną w DiscountPrograms (Change Signature). */
public final class PriceBoard {
    public Money priceFor(PriceRequest request) {
        return new TicketPricer(DiscountPrograms.forName(request.program()))
                .price(request.base(), request.ticketType());
    }
}
