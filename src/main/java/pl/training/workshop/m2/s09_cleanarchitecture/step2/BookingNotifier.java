package pl.training.workshop.m2.s09_cleanarchitecture.step2;

/** Krok 2: port wyjściowy - "daj znać, że rezerwacja powstała". */
public interface BookingNotifier {
    void reservationCreated(String id, NewReservation reservation);
}
