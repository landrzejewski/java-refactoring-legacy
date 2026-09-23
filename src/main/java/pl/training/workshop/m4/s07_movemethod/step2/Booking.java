package pl.training.workshop.m4.s07_movemethod.step2;

/**
 * Rezerwacja jednego miejsca.
 *
 * @param seat numer miejsca; {@code Integer}, bo tak przychodzi ze starego API
 */
public record Booking(String id, Screening screening, Integer seat) {
}
