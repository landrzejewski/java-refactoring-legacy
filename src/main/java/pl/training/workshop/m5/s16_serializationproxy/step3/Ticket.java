package pl.training.workshop.m5.s16_serializationproxy.step3;

/**
 * Krok 3: bez zmian.
 */
public abstract class Ticket {
    private final String title;
    private final String seat;

    protected Ticket(String title, String seat) {
        this.title = title;
        this.seat = seat;
    }

    public String title() {
        return title;
    }

    public String seat() {
        return seat;
    }
}
