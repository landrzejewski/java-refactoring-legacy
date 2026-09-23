package pl.training.workshop.m5.s04_extractsuperclass.step3;

import java.time.LocalDateTime;

/**
 * Krok 3 (rozwiązanie): warunek kolizji wyciągnięty (Extract Method) i przeniesiony do nadklasy
 * jako {@code overlaps}; {@code name()} to abstrakcyjny punkt rozszerzenia dla opisu konfliktu.
 */
public abstract class HallBooking {
    private final String hall;
    private final LocalDateTime start;
    private final int minutes;

    protected HallBooking(String hall, LocalDateTime start, int minutes) {
        this.hall = hall;
        this.start = start;
        this.minutes = minutes;
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

    public abstract String name();

    public final boolean overlaps(HallBooking other) {
        return hall.equals(other.hall) && start.isBefore(other.end()) && other.start.isBefore(end());
    }
}
