package pl.training.workshop.m5.s06_extractinterface.step2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: klient przechodzi na rolę - jedna lista Priceable, jedno add(Priceable), jedna pętla.
 * Źródłowo zgodne (add(ticket) nadal się kompiluje), binarnie NIE: zmienił się deskryptor add(...).
 */
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
            BigDecimal rate = BigDecimal.valueOf(item.vatPercent());
            vat = vat.plus(new Money(item.price().amount().multiply(rate)
                    .divide(rate.add(BigDecimal.valueOf(100)), 2, RoundingMode.HALF_UP)));
        }
        return "Razem: " + total + ", VAT: " + vat;
    }
}
