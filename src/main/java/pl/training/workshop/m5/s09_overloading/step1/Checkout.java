package pl.training.workshop.m5.s09_overloading.step1;

import java.util.List;

import pl.training.workshop.shared.Money;

/** Krok 1: bez zmian - ten sam kod klienta liczy teraz poprawnie. */
public final class Checkout {
    private final PriceList priceList = new PriceList();

    public Money total(List<Ticket> tickets) {
        Money total = Money.ZERO;
        for (Ticket ticket : tickets) {
            total = total.plus(priceList.price(ticket));
        }
        return total;
    }

    public boolean alreadyInCart(List<Ticket> cart, Ticket ticket) {
        return cart.contains(ticket);
    }
}
