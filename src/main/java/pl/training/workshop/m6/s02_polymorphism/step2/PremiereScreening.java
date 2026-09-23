package pl.training.workshop.m6.s02_polymorphism.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: premiera - 30 minut spotkania z twórcami zamiast reklam. */
public final class PremiereScreening extends Screening {
    private final int runtime;

    PremiereScreening(String title, int runtime) {
        super(title);
        this.runtime = runtime;
    }

    @Override
    public String label() {
        return "Premiera: " + title();
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
