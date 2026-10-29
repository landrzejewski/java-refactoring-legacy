package pl.training.workshop.exercises.e02_hallmap.step1;

import pl.training.workshop.shared.Money;

/**
 * Krok 1: wspólny kontrakt każdego elementu sali - od pojedynczego miejsca po całą salę.
 * Klient pyta o pojemność i dopłaty bez wiedzy, ile poziomów kryje się w środku.
 */
public interface HallElement {
    int capacity();

    Money surcharges();
}
