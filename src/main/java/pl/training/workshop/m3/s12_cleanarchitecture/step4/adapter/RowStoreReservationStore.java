package pl.training.workshop.m3.s12_cleanarchitecture.step4.adapter;

import pl.training.workshop.m3.s12_cleanarchitecture.RowStore;
import pl.training.workshop.m3.s12_cleanarchitecture.step4.app.NewReservation;
import pl.training.workshop.m3.s12_cleanarchitecture.step4.app.ReservationStore;

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
