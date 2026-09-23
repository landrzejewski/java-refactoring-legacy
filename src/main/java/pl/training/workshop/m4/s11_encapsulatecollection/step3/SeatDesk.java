package pl.training.workshop.m4.s11_encapsulatecollection.step3;

import pl.training.workshop.m4.s11_encapsulatecollection.Seat;

/** Od kroku 1: klient przepisany na operacje właściciela - nie dotyka już listy. */
public final class SeatDesk {
    public void select(Booking booking, Seat seat) {
        booking.addSeat(seat);
    }

    public void release(Booking booking, Seat seat) {
        booking.removeSeat(seat);
    }
}
