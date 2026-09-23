package pl.training.workshop.m6.s02_polymorphism.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: zwykły seans jako rekord. */
public record RegularScreening(String title, int runtime) implements Screening {
    @Override
    public String label() {
        return title;
    }

    @Override
    public int durationMinutes() {
        return 20 + runtime;
    }

    @Override
    public Money price() {
        return Money.of("25.00");
    }
}
