package pl.training.workshop.m5.s15_compatibility.step3;

import pl.training.workshop.shared.Money;
import pl.training.workshop.m5.s15_compatibility.Column;

/**
 * Krok 3: bez zmian.
 */
public abstract class Ticket {
    private final String title;
    private final Money basePrice;

    protected Ticket(String title, Money basePrice) {
        this.title = title;
        this.basePrice = basePrice;
    }

    public String title() {
        return title;
    }

    public Money basePrice() {
        return basePrice;
    }

    @Column("cena")
    public final Money price() {
        return basePrice.minus(basePrice.percent(discountPercent()));
    }

    protected abstract int discountPercent();
}
