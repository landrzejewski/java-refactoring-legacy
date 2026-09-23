package pl.training.workshop.m3.s12_cleanarchitecture.step2;

import pl.training.workshop.m3.s12_cleanarchitecture.Outbox;
import pl.training.workshop.m3.s12_cleanarchitecture.RowStore;

/**
 * Punkt startowy aplikacji. Na razie tylko tworzy kontroler - graf obiektów składa
 * sam kontroler. W kroku 4 ta klasa stanie się prawdziwym composition root.
 * Test woła tylko tę metodę, więc refaktoryzacja na żywo go nie psuje.
 */
public final class CinemaApplication {
    private CinemaApplication() {
    }

    public static ReservationController reservationController(RowStore db, Outbox outbox) {
        return new ReservationController(db, outbox);
    }
}
