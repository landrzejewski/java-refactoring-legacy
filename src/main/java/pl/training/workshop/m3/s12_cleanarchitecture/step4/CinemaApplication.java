package pl.training.workshop.m3.s12_cleanarchitecture.step4;

import pl.training.workshop.m3.s12_cleanarchitecture.Outbox;
import pl.training.workshop.m3.s12_cleanarchitecture.RowStore;
import pl.training.workshop.m3.s12_cleanarchitecture.step4.adapter.OutboxBookingNotifier;
import pl.training.workshop.m3.s12_cleanarchitecture.step4.adapter.ReservationController;
import pl.training.workshop.m3.s12_cleanarchitecture.step4.adapter.RowStoreReservationStore;
import pl.training.workshop.m3.s12_cleanarchitecture.step4.app.BookSeats;

/**
 * Krok 4 (rozwiązanie): composition root - jedyne miejsce, które zna wszystkie konkrety
 * i składa graf ręcznymi konstruktorami. Nie zawiera reguł biznesowych.
 * Podmiana adaptera (np. inna baza) = zmiana tylko tutaj.
 */
public final class CinemaApplication {
    private CinemaApplication() {
    }

    public static ReservationController reservationController(RowStore db, Outbox outbox) {
        BookSeats bookSeats = new BookSeats(
                new RowStoreReservationStore(db), new OutboxBookingNotifier(outbox));
        return new ReservationController(bookSeats);
    }
}
