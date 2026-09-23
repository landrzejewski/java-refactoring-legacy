package pl.training.workshop.m5.s04_extractsuperclass.step2;

import java.time.LocalDateTime;

/**
 * Krok 2: PrivateEvent extends HallBooking - zduplikowane pola i akcesory usunięte.
 * Konstruktor i fabryka rental(...) zachowane: konstruktory nie są dziedziczone, każdą sygnaturę pilnujemy sami.
 */
public final class PrivateEvent extends HallBooking {
    private final String client;

    public PrivateEvent(String client, String hall, LocalDateTime start, int minutes) {
        super(hall, start, minutes);
        this.client = client;
    }

    /** Fabryka: standardowy wynajem trwa 2 godziny. */
    public static PrivateEvent rental(String client, String hall, LocalDateTime start) {
        return new PrivateEvent(client, hall, start, 120);
    }

    public String client() {
        return client;
    }
}
