package pl.training.workshop.m6.s11_safecomposite.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: liść nie ma już metody add() - nie da się jej wywołać przez pomyłkę. */
public final class Product extends MenuComponent {
    private final String name;
    private final Money price;

    public Product(String name, String price) {
        this.name = name;
        this.price = Money.of(price);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Money price() {
        return price;
    }
}
