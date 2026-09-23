package pl.training.workshop.m4.s10_encapsulatefield.start;

/** Kasa - klient pola status. guestEntry łamie niezmiennik z zewnątrz. */
public final class BoxOffice {
    public void pay(Reservation r) {
        if (r.status.equals("NEW")) {
            r.status = "PAID";
        }
    }

    public void checkIn(Reservation r) {
        if (r.status.equals("PAID")) {
            r.status = "USED";
        }
    }

    public void cancel(Reservation r) {
        if (r.status.equals("NEW") || r.status.equals("PAID")) {
            r.status = "CANCELLED";
        }
    }

    public void expire(Reservation r) {
        if (r.status.equals("NEW")) {
            r.status = "EXPIRED";
        }
    }

    /** Wejście gościa kierownika - "na skróty", bez płatności i bez sprawdzania statusu. */
    public void guestEntry(Reservation r) {
        r.status = "USED";
    }
}
