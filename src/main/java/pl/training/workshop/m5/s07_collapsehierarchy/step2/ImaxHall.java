package pl.training.workshop.m5.s07_collapsehierarchy.step2;

/**
 * Krok 2: już nieużywana (klienci tworzą sale przez Hall.imax). W bibliotece zostałaby tu jako
 * {@code @Deprecated(forRemoval = true)} typ zgodności na jedno wydanie.
 */
public class ImaxHall extends Hall {
    public ImaxHall(String name, int rows, int seatsPerRow) {
        super(name, rows, seatsPerRow, rows - 1);
    }
}
