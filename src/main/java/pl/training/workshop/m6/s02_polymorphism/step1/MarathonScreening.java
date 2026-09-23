package pl.training.workshop.m6.s02_polymorphism.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: pierwsza podklasa - maraton ma własne, nazwane dane (films zamiast value). */
public final class MarathonScreening extends Screening {
    private final int films;

    MarathonScreening(String title, int films) {
        super(Kind.MARATHON, title, films);
        this.films = films;
    }

    @Override
    public String label() {
        return "Maraton: " + title() + " (" + films + " filmy)";
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
