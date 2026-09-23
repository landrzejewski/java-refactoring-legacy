package pl.training.workshop.m6.s02_polymorphism.step3;

import pl.training.workshop.shared.Money;

/** Krok 3: maraton jako rekord - pole films ma jedno znaczenie. */
public record MarathonScreening(String title, int films) implements Screening {
    @Override
    public String label() {
        return "Maraton: " + title + " (" + films + " filmy)";
    }

    @Override
    public int durationMinutes() {
        return films * 120 + (films - 1) * 15;
    }

    @Override
    public Money price() {
        return Money.of("20.00").times(films);
    }
}
