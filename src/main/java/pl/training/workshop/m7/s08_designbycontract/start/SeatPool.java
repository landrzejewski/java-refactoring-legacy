package pl.training.workshop.m7.s08_designbycontract.start;

/**
 * Start: pula miejsc seansu bez żadnych kontraktów. reserve(-2) po cichu "dodaje" miejsca,
 * a release() pozwala przekroczyć pojemność sali. Stan może stać się niemożliwy.
 */
public final class SeatPool {
    private final int capacity;
    private int remaining;

    public SeatPool(int capacity) {
        this.capacity = capacity;
        this.remaining = capacity;
    }

    /** Zwraca false, gdy wolnych miejsc jest za mało - to poprawna, udokumentowana odpowiedź. */
    public boolean reserve(int seats) {
        if (seats > remaining) {
            return false;
        }
        remaining = remaining - seats;
        return true;
    }

    public void release(int seats) {
        remaining = remaining + seats;
    }

    public int remaining() {
        return remaining;
    }

    public int capacity() {
        return capacity;
    }
}
