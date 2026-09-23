package pl.training.workshop.m5.s13_sealed.start;

import pl.training.workshop.shared.Money;

/**
 * Start: łańcuch instanceof zakończony cichym "return 0". Nowy typ biletu (np. dziecięcy)
 * skompiluje się bez słowa i dostanie 0% zniżki.
 */
public final class PriceCalculator {
    public int discountPercent(Ticket ticket) {
        if (ticket instanceof StudentTicket) {
            return 25;
        } else if (ticket instanceof SeniorTicket) {
            return 30;
        }
        return 0;
    }

    public Money price(Ticket ticket) {
        return ticket.basePrice().minus(ticket.basePrice().percent(discountPercent(ticket)));
    }
}
