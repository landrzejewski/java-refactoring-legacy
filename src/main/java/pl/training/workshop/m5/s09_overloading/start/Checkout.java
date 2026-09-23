package pl.training.workshop.m5.s09_overloading.start;

import java.util.List;

import pl.training.workshop.shared.Money;

/** Start: klient po migracji na typ bazowy. Kompiluje się bez ostrzeżeń - i liczy źle. */
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
