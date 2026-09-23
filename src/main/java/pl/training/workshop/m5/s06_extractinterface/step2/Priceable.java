package pl.training.workshop.m5.s06_extractinterface.step2;

import pl.training.workshop.shared.Money;

/**
 * Krok 2: bez zmian.
 */
public interface Priceable {
    Money price();

    int vatPercent();
}
