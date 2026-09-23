package pl.training.workshop.m4.s10_encapsulatefield.step3;

/**
 * Krok 3 (ŚWIADOMA ZMIANA ZACHOWANIA, osobny commit): operacje pilnują niezmiennika.
 * Niedozwolone przejście rzuca IllegalStateException zamiast być cicho ignorowane,
 * a gość może wejść tylko na rezerwację NEW albo PAID. To już nie refaktoryzacja.
 */
public final class Reservation {
    private String status = "NEW";

    public String status() {
        return status;
    }

    public void pay() {
        moveTo("PAID", "NEW");
    }

    public void checkIn() {
        moveTo("USED", "PAID");
    }

    public void cancel() {
        moveTo("CANCELLED", "NEW", "PAID");
    }

    public void expire() {
        moveTo("EXPIRED", "NEW");
    }

    public void admitGuestWithoutPayment() {
        moveTo("USED", "NEW", "PAID");
    }

    private void moveTo(String target, String... allowedFrom) {
        for (String allowed : allowedFrom) {
            if (status.equals(allowed)) {
                status = target;
                return;
            }
        }
        throw new IllegalStateException("Niedozwolone przejscie " + status + " -> " + target);
    }
}
