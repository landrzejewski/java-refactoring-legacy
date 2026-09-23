package pl.training.workshop.m8.s12_expandcontract.start;

import java.math.BigDecimal;
import java.util.Arrays;

import pl.training.workshop.m8.s12_expandcontract.Booking;
import pl.training.workshop.shared.Money;

/**
 * Start: stary format wiersza - B1;anna@kino.pl;A5,A10;84.00
 * (bez wersji, średnik w danych psuje wiersz).
 */
public final class CsvBookingFormat {
    private CsvBookingFormat() {
    }

    public static String write(Booking booking) {
        return booking.id() + ";" + booking.email() + ";" + String.join(",", booking.seats())
                + ";" + booking.total();
    }

    public static Booking read(String line) {
        String[] parts = line.split(";");
        return new Booking(parts[0], parts[1], Arrays.asList(parts[2].split(",")),
                new Money(new BigDecimal(parts[3])));
    }
}
