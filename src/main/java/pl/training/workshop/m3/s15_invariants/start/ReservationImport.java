package pl.training.workshop.m3.s15_invariants.start;

import java.math.BigDecimal;

/** Start: import z pliku partnera "email;miejsca;kwota" - tworzy model z pominięciem walidacji. */
public final class ReservationImport {
    public String importLine(String line) {
        String[] columns = line.split(";");
        Reservation reservation = new Reservation();
        reservation.setEmail(columns[0]);
        reservation.setSeats(Integer.parseInt(columns[1]));
        reservation.setTotal(new BigDecimal(columns[2]));
        return "zaimportowano: " + reservation.getEmail() + ", miejsc " + reservation.getSeats()
                + ", kwota " + reservation.getTotal();
    }
}
