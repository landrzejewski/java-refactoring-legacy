package pl.training.workshop.m5.s06_extractinterface.step1;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: Extract Interface z perspektywy klienta (Cart) - rola "coś, co ma cenę brutto i stawkę VAT".
 * Nie kopiujemy title(), seat() ani name(): koszyk ich nie używa.
 */
public interface Priceable {
    Money price();

    int vatPercent();
}
