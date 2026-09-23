package pl.training.workshop.m3.s12_cleanarchitecture.step2;

/** Krok 2: port wyjściowy - "daj znać, że rezerwacja powstała". */
public interface BookingNotifier {
    void reservationCreated(String id, NewReservation reservation);
}
