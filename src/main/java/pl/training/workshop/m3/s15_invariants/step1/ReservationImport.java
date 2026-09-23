package pl.training.workshop.m3.s15_invariants.step1;

import java.math.BigDecimal;

/** Krok 1: import tworzy rekord - nadal bez walidacji. */
public final class ReservationImport {
    public String importLine(String line) {
        String[] columns = line.split(";");
        Reservation reservation =
                new Reservation(columns[0], Integer.parseInt(columns[1]), new BigDecimal(columns[2]));
        return "zaimportowano: " + reservation.email() + ", miejsc " + reservation.seats()
                + ", kwota " + reservation.total();
    }
}
