package pl.training.workshop.m6.s02_polymorphism;

/**
 * Stabilny kontrakt sceny: wiersz z bazy repertuaru. Znaczenie {@code value} zależy od rodzaju:
 * REGULAR i PREMIERE - długość filmu w minutach, MARATHON - liczba filmów.
 */
public record ScreeningRow(String kind, String title, int value) {
}
