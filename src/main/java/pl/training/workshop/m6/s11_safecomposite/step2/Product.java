package pl.training.workshop.m6.s11_safecomposite.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: liść jako rekord. */
public record Product(String name, Money price) implements MenuComponent {
    public Product(String name, String price) {
        this(name, Money.of(price));
    }

    @Override
    public String describe() {
        return name + " " + price;
    }
}
