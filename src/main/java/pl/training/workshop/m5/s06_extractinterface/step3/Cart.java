package pl.training.workshop.m5.s06_extractinterface.step3;

import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.shared.Money;

/** Krok 3: koszyk pyta pozycję o kwotę VAT zamiast liczyć ją sam. */
public final class Cart {
    private final List<Priceable> items = new ArrayList<>();

    public void add(Priceable item) {
        items.add(item);
    }

    public String summary() {
        Money total = Money.ZERO;
        Money vat = Money.ZERO;
        for (Priceable item : items) {
            total = total.plus(item.price());
            vat = vat.plus(item.vatAmount());
        }
        return "Razem: " + total + ", VAT: " + vat;
    }
}
