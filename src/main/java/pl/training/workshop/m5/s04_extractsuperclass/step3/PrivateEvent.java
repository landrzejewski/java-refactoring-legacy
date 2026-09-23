package pl.training.workshop.m5.s04_extractsuperclass.step3;

import java.time.LocalDateTime;

/** Krok 3: PrivateEvent podaje tylko swoją nazwę do opisu konfliktu. */
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

    @Override
    public String name() {
        return "Wynajem: " + client;
    }
}
