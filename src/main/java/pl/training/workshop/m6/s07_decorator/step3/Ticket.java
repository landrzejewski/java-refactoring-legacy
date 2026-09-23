package pl.training.workshop.m6.s07_decorator.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: czysty rdzeń - tylko to, co ma każdy bilet. Bez flag. */
public record Ticket(String title, String format, Money base) implements PricedTicket {
    @Override
    public Money price() {
        return base;
    }

    @Override
    public String description() {
        return title + " " + format;
    }
}
