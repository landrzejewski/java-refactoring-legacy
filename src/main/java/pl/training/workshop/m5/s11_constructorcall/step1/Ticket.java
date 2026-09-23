package pl.training.workshop.m5.s11_constructorcall.step1;

/** Krok 1: baza bez zmian - naprawa lokalna w podklasie. */
public class Ticket {
    private final String seat;
    private final String label;

    public Ticket(String seat) {
        this.seat = seat;
        this.label = describe();
    }

    protected String describe() {
        return "Miejsce " + seat;
    }

    public final String label() {
        return label;
    }
}
