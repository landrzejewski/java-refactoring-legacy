package pl.training.workshop.m3.s12_cleanarchitecture.step3.adapter;

import pl.training.workshop.m3.s12_cleanarchitecture.Outbox;
import pl.training.workshop.m3.s12_cleanarchitecture.step3.app.BookingNotifier;
import pl.training.workshop.m3.s12_cleanarchitecture.step3.app.NewReservation;

/** Krok 2: adapter wyjściowy - zna temat i format komunikatu. */
public final class OutboxBookingNotifier implements BookingNotifier {
    private final Outbox outbox;

    public OutboxBookingNotifier(Outbox outbox) {
        this.outbox = outbox;
    }

    @Override
    public void reservationCreated(String id, NewReservation reservation) {
        outbox.publish("reservation-created", id + ";" + reservation.email() + ";" + reservation.total());
    }
}
