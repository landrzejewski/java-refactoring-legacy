package pl.training.workshop.m3.s09_lsp.step1;

/**
 * Krok 1: Extract Interface - rola "plan miejsc do odczytu".
 * Kontrakt: {@code freeSeats()} równa się liczbie miejsc 1..capacity, dla których {@code isFree}.
 */
public interface SeatMap {
    boolean isFree(int seat);

    int freeSeats();

    int capacity();
}
