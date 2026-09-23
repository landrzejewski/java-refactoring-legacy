package pl.training.workshop.m7.s08_designbycontract.step1;

/**
 * Krok 1: warunki wstępne (preconditions) przed pierwszą mutacją. To NIE jest refaktoryzacja:
 * dla niepoprawnych wejść zachowanie się zmienia (wyjątek zamiast cichego zepsucia stanu).
 * Dla poprawnych wejść - bez zmian, łącznie z "false" przy braku miejsc.
 */
public final class SeatPool {
    private final int capacity;
    private int remaining;

    public SeatPool(int capacity) {
        Contracts.require(capacity > 0, "capacity must be positive");
        this.capacity = capacity;
        this.remaining = capacity;
    }

    public boolean reserve(int seats) {
        Contracts.require(seats > 0, "seats must be positive");
        if (seats > remaining) {
            return false;
        }
        remaining = remaining - seats;
        return true;
    }

    public void release(int seats) {
        Contracts.require(seats > 0, "seats must be positive");
        Contracts.require(seats <= capacity - remaining, "cannot release more seats than reserved");
        remaining = remaining + seats;
    }

    public int remaining() {
        return remaining;
    }

    public int capacity() {
        return capacity;
    }
}
