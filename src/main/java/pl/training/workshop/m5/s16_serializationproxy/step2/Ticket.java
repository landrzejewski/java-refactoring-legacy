package pl.training.workshop.m5.s16_serializationproxy.step2;

/**
 * Krok 2: baza nie jest już Serializable - formatem danych zarządza proxy serializacji w podklasie,
 * więc przyszłe ruchy w hierarchii nie zmienią postaci strumienia.
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
