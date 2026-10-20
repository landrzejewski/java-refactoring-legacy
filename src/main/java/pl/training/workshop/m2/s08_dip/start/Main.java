package pl.training.workshop.m2.s08_dip.start;

import pl.training.workshop.m2.s08_dip.start.app.ConfirmReservation;

/**
 * Composition root wariantu: jedyne miejsce, które składa graf obiektów.
 * Test woła tylko tę metodę, więc refaktoryzacja konstruktorów na żywo go nie psuje
 * (Introduce Parameter w IntelliJ sam przeniesie tu tworzenie zależności).
 */
public final class Main {
    private Main() {
    }

    public static ConfirmReservation confirmReservation() {
        return new ConfirmReservation();
    }
}
