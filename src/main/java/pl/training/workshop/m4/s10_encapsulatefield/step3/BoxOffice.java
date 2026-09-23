package pl.training.workshop.m4.s10_encapsulatefield.step3;

/** Krok 3: kasa bez zmian - nowa reguła mieszka w Reservation. */
public final class BoxOffice {
    public void pay(Reservation r) {
        r.pay();
    }

    public void checkIn(Reservation r) {
        r.checkIn();
    }

    public void cancel(Reservation r) {
        r.cancel();
    }

    public void expire(Reservation r) {
        r.expire();
    }

    public void guestEntry(Reservation r) {
        r.admitGuestWithoutPayment();
    }
}
