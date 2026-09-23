package pl.training.workshop.m6.s11_safecomposite.start;

import pl.training.workshop.shared.Money;

/** Start: liść dziedziczy add(), który zawsze rzuca wyjątek. */
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
