package pl.training.workshop.m5.s09_overloading.step2;

import java.util.Objects;

import pl.training.workshop.shared.Money;

/**
 * Krok 2 (rozwiązanie): equals(Object) z @Override i hashCode (Generate equals() and hashCode()).
 * Adnotacja @Override zamienia cichy błąd przeciążenia w błąd kompilacji. Porównujemy getClass(),
 * bo bilet studencki i normalny na ten sam film to różne pozycje.
 */
public class Ticket {
    private final String title;
    private final Money basePrice;

    public Ticket(String title, Money basePrice) {
        this.title = title;
        this.basePrice = basePrice;
    }

    public String title() {
        return title;
    }

    public Money basePrice() {
        return basePrice;
    }

    public int discountPercent() {
        return 0;
    }

    @Override
    public boolean equals(Object other) {
        return other != null && getClass() == other.getClass()
                && title.equals(((Ticket) other).title) && basePrice.equals(((Ticket) other).basePrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, basePrice);
    }
}
