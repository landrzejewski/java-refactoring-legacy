package pl.training.workshop.m5.s20_decisionmap.step2;

import pl.training.workshop.shared.Money;

/** Krok 2 (ścieżka A): korekta ceny za dzień jako wymienny algorytm - Strategy. */
@FunctionalInterface
public interface DayPolicy {
    Money apply(Money base);
}
