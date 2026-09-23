package pl.training.workshop.m4.s11_encapsulatecollection.start;

import pl.training.workshop.m4.s11_encapsulatecollection.Seat;

/** Klient kolekcji: wybór i zwolnienie miejsca - bezpośrednio na liście właściciela. */
public final class SeatDesk {
    public void select(Booking booking, Seat seat) {
        booking.seats.add(seat);
    }

    public void release(Booking booking, Seat seat) {
        booking.seats.remove(seat);
    }
}
