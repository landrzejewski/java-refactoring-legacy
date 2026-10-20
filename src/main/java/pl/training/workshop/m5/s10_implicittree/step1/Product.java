package pl.training.workshop.m5.s10_implicittree.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: liść - produkt baru z ceną. */
public record Product(String name, Money price) implements MenuItem {
    @Override
    public void render(int depth, StringBuilder text) {
        text.append("  ".repeat(depth)).append(name).append(' ').append(price).append('\n');
    }
}
