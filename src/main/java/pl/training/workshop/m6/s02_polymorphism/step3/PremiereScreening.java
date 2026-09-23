package pl.training.workshop.m6.s02_polymorphism.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: premiera jako rekord. */
public record PremiereScreening(String title, int runtime) implements Screening {
    @Override
    public String label() {
        return "Premiera: " + title;
    }

    @Override
    public int durationMinutes() {
        return 30 + runtime;
    }

    @Override
    public Money price() {
        return Money.of("35.00");
    }
}
