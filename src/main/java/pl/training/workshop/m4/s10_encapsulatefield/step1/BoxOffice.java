package pl.training.workshop.m4.s10_encapsulatefield.step1;

/** Krok 1: klienci przepisani przez IDE na getStatus/setStatus. */
public final class BoxOffice {
    public void pay(Reservation r) {
        if (r.getStatus().equals("NEW")) {
            r.setStatus("PAID");
        }
    }

    public void checkIn(Reservation r) {
        if (r.getStatus().equals("PAID")) {
            r.setStatus("USED");
        }
    }

    public void cancel(Reservation r) {
        if (r.getStatus().equals("NEW") || r.getStatus().equals("PAID")) {
            r.setStatus("CANCELLED");
        }
    }

    public void expire(Reservation r) {
        if (r.getStatus().equals("NEW")) {
            r.setStatus("EXPIRED");
        }
    }

    /** Wejście gościa kierownika - "na skróty", bez płatności i bez sprawdzania statusu. */
    public void guestEntry(Reservation r) {
        r.setStatus("USED");
    }
}
