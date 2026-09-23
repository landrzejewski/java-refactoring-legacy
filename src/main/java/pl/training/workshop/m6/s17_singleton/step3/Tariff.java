package pl.training.workshop.m6.s17_singleton.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: Extract Interface - klient zależy od kontraktu cennika, nie od singletona. */
@FunctionalInterface
public interface Tariff {
    Money basePrice(String format);
}
