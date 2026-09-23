package pl.training.workshop.m5.s02_pullupfield.step3;

/**
 * Krok 3 (rozwiązanie): Pull Members Up dla pola {@code seat} i akcesora {@code seat()}.
 * Pole w bazie jest private final i ustawiane przez super(...) - nie surowe protected.
 */
public abstract class Ticket {
    private final String seat;

    protected Ticket(String seat) {
        this.seat = seat;
    }

    public String seat() {
        return seat;
    }

    public abstract String describe();
}
