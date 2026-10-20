package pl.training.workshop.m2.s09_cleanarchitecture.step2;

import pl.training.workshop.m2.s09_cleanarchitecture.RowStore;

/** Krok 2: adapter wyjściowy - mapuje rekord na kolumny tabeli. */
public final class RowStoreReservationStore implements ReservationStore {
    private final RowStore db;

    public RowStoreReservationStore(RowStore db) {
        this.db = db;
    }

    @Override
    public String save(NewReservation reservation) {
        return db.insert(new Object[] {
                reservation.email(), reservation.format(), reservation.seats(), reservation.total()});
    }
}
