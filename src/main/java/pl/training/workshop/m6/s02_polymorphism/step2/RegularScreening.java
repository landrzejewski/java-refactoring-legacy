package pl.training.workshop.m6.s02_polymorphism.step2;

import pl.training.workshop.shared.Money;

/** Krok 2: zwykły seans - 20 minut reklam przed filmem. */
public final class RegularScreening extends Screening {
    private final int runtime;

    RegularScreening(String title, int runtime) {
        super(title);
        this.runtime = runtime;
    }

    @Override
    public String label() {
        return title();
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
