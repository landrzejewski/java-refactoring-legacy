package pl.training.workshop.m5.s12_bridgemethods.step1;

import pl.training.workshop.shared.Money;
import pl.training.workshop.m5.s12_bridgemethods.StandardTicket;

/** Krok 1: implementuje PriceRule - w pliku .class są teraz DWIE metody apply (jedna to bridge). */
public final class StandardRule implements PriceRule<StandardTicket> {
    @Override
    public Money apply(StandardTicket ticket) {
        return ticket.basePrice();
    }
}
