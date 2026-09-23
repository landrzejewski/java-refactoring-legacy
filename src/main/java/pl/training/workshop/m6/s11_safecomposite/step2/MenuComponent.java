package pl.training.workshop.m6.s11_safecomposite.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: forma Java 25 - zamknięty zestaw węzłów, rekordy, brak mutacji po zbudowaniu. */
public sealed interface MenuComponent permits Product, Combo {
    String name();

    Money price();

    String describe();
}
