package pl.training.workshop.m5.s12_bridgemethods.step2;

import pl.training.workshop.shared.Money;
import pl.training.workshop.m5.s12_bridgemethods.StandardTicket;

/** Krok 2: reguła mówi wprost, jaki bilet obsługuje. */
public final class StandardRule implements PriceRule<StandardTicket> {
    @Override
    public Class<StandardTicket> ticketType() {
        return StandardTicket.class;
    }

    @Override
    public Money apply(StandardTicket ticket) {
        return ticket.basePrice();
    }
}
