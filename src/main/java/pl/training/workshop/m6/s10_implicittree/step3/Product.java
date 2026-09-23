package pl.training.workshop.m6.s10_implicittree.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: liść - produkt baru z ceną. */
public record Product(String name, Money price) implements MenuItem {
    @Override
    public void render(int depth, StringBuilder text) {
        text.append("  ".repeat(depth)).append(name).append(' ').append(price).append('\n');
    }
}
