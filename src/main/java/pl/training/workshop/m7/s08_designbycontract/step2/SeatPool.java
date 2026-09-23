package pl.training.workshop.m7.s08_designbycontract.step2;

/**
 * Krok 2 (rozwiązanie): warunki końcowe (ensure) i niezmiennik 0 <= remaining <= capacity.
 * Niezmiennik sprawdzamy dla nowej wartości PRZED przypisaniem - naruszenie nie zmienia obiektu.
 * Dla poprawnych wejść zachowanie identyczne jak w kroku 1; kontrole chronią przyszłe zmiany.
 */
public final class SeatPool {
    private final int capacity;
    private int remaining;

    public SeatPool(int capacity) {
        Contracts.require(capacity > 0, "capacity must be positive");
        this.capacity = capacity;
        this.remaining = capacity;
        checkInvariant(remaining);
    }

    public boolean reserve(int seats) {
        Contracts.require(seats > 0, "seats must be positive");
        if (seats > remaining) {
            return false;
        }
        int previous = remaining;
        int next = previous - seats;
        checkInvariant(next);
        remaining = next;
        Contracts.ensure(remaining == previous - seats, "reserve must reduce remaining by seats");
        return true;
    }

    public void release(int seats) {
        Contracts.require(seats > 0, "seats must be positive");
        Contracts.require(seats <= capacity - remaining, "cannot release more seats than reserved");
        int previous = remaining;
        int next = previous + seats;
        checkInvariant(next);
        remaining = next;
        Contracts.ensure(remaining == previous + seats, "release must increase remaining by seats");
    }

    public int remaining() {
        return remaining;
    }

    public int capacity() {
        return capacity;
    }

    private void checkInvariant(int candidate) {
        Contracts.ensure(candidate >= 0 && candidate <= capacity,
                "remaining must stay within 0.." + capacity);
    }
}
