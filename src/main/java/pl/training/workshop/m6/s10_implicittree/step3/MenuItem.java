package pl.training.workshop.m6.s10_implicittree.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: jawny Composite - produkt (liść) albo zestaw (węzeł). */
public sealed interface MenuItem permits Product, Combo {
    String name();

    Money price();

    void render(int depth, StringBuilder text);
}
