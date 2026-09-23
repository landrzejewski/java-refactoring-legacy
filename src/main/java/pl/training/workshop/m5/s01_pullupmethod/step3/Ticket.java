package pl.training.workshop.m5.s01_pullupmethod.step3;

import java.util.Objects;

import pl.training.workshop.shared.Money;

/**
 * Krok 3 (rozwiązanie): Pull Members Up dla {@code label()} - trzy identyczne ciała stały się jedną metodą.
 * {@code final}, bo etykieta jest kontraktem wspólnym dla wszystkich biletów; wariantem jest tylko cena.
 */
public abstract class Ticket {
    private final String title;
    private final Money basePrice;

    protected Ticket(String title, Money basePrice) {
        this.title = Objects.requireNonNull(title, "title");
        this.basePrice = Objects.requireNonNull(basePrice, "basePrice");
    }

    public String title() {
        return title;
    }

    public Money basePrice() {
        return basePrice;
    }

    public abstract Money price();

    public final String label() {
        return title() + ": " + price();
    }
}
