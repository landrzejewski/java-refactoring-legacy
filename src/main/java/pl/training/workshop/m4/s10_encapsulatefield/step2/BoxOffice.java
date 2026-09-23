package pl.training.workshop.m4.s10_encapsulatefield.step2;

/** Krok 2: kasa tylko deleguje - reguły przejść mają jednego właściciela. */
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
