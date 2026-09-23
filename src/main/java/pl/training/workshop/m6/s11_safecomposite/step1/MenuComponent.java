package pl.training.workshop.m6.s11_safecomposite.step1;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: Safe Composite - Push Members Down: add() i children() tylko w Combo.
 * Wspólny typ ma wyłącznie operacje sensowne dla liścia i węzła.
 */
public abstract class MenuComponent {
    public abstract String name();

    public abstract Money price();

    public String describe() {
        return name() + " " + price();
    }
}
