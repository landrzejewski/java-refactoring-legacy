package pl.training.workshop.m4.s10_encapsulatefield.step2;

/**
 * Krok 2: setter zastąpiony operacjami domenowymi (Move Method z BoxOffice + Remove Setting Method).
 * Warunki przepisane 1:1 - niedozwolone przejście nadal jest cicho ignorowane.
 * Wejście gościa ma teraz uczciwą nazwę zamiast anonimowego setStatus("USED").
 */
public final class Reservation {
    private String status = "NEW";

    public String status() {
        return status;
    }

    public void pay() {
        if (status.equals("NEW")) {
            status = "PAID";
        }
    }

    public void checkIn() {
        if (status.equals("PAID")) {
            status = "USED";
        }
    }

    public void cancel() {
        if (status.equals("NEW") || status.equals("PAID")) {
            status = "CANCELLED";
        }
    }

    public void expire() {
        if (status.equals("NEW")) {
            status = "EXPIRED";
        }
    }

    public void admitGuestWithoutPayment() {
        status = "USED";
    }
}
