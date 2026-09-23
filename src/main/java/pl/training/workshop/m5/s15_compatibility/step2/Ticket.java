package pl.training.workshop.m5.s15_compatibility.step2;

import pl.training.workshop.shared.Money;
import pl.training.workshop.m5.s15_compatibility.Column;

/**
 * Krok 2: Pull Members Up - price() (z adnotacją @Column) w bazie, różnica w haku discountPercent().
 * Typ deklarujący metody zmienił się na Ticket: stare wywołania StudentTicket.price() dalej się linkują
 * (JVM szuka w nadklasach), ale getDeclaredMethods() na podklasie już jej nie widzi.
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
