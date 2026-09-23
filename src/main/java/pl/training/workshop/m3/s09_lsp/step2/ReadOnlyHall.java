package pl.training.workshop.m3.s09_lsp.step2;

import java.util.Set;

/**
 * Krok 2 (rozwiązanie): Replace Inheritance with Delegation. Sala archiwalna
 * implementuje tylko rolę, której kontrakt spełnia - {@link SeatMap} - i deleguje
 * do prywatnej kopii Hall. Metody reserve po prostu nie ma: kompilator nie pozwoli
 * przekazać sali archiwalnej do kasy.
 */
public final class ReadOnlyHall implements SeatMap {
    private final Hall snapshot;

    public ReadOnlyHall(int capacity, Set<Integer> taken) {
        this.snapshot = new Hall(capacity);
        taken.forEach(snapshot::reserve);
    }

    @Override
    public boolean isFree(int seat) {
        return snapshot.isFree(seat);
    }

    @Override
    public int freeSeats() {
        return snapshot.freeSeats();
    }

    @Override
    public int capacity() {
        return snapshot.capacity();
    }
}
