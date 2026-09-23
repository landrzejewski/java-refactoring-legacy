package pl.training.workshop.m5.s04_extractsuperclass.step1;

import java.time.LocalDateTime;

/** Krok 1: bez zmian - dołączamy klasy pojedynczo, PrivateEvent w następnym kroku. */
public final class PrivateEvent {
    private final String client;
    private final String hall;
    private final LocalDateTime start;
    private final int minutes;

    public PrivateEvent(String client, String hall, LocalDateTime start, int minutes) {
        this.client = client;
        this.hall = hall;
        this.start = start;
        this.minutes = minutes;
    }

    /** Fabryka: standardowy wynajem trwa 2 godziny. */
    public static PrivateEvent rental(String client, String hall, LocalDateTime start) {
        return new PrivateEvent(client, hall, start, 120);
    }

    public String client() {
        return client;
    }

    public String hall() {
        return hall;
    }

    public LocalDateTime start() {
        return start;
    }

    public LocalDateTime end() {
        return start.plusMinutes(minutes);
    }
}
