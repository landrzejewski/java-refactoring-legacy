package pl.training.workshop.m8.s12_expandcontract.step2;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import pl.training.workshop.m8.s12_expandcontract.Booking;
import pl.training.workshop.shared.Money;

/** Krok 2 (bez zmian): nowy, wersjonowany format payload. */
public final class BookingPayloadFormat {
    private static final String VERSION = "v2";

    private BookingPayloadFormat() {
    }

    public static String write(Booking booking) {
        return VERSION + "|id=" + booking.id() + "|email=" + booking.email()
                + "|seats=" + String.join(" ", booking.seats()) + "|total=" + booking.total();
    }

    public static Booking read(String payload) {
        String[] parts = payload.split("\\|");
        if (!parts[0].equals(VERSION)) {
            throw new IllegalArgumentException("Nieznana wersja formatu: " + parts[0]);
        }
        Map<String, String> fields = new HashMap<>();
        for (int i = 1; i < parts.length; i++) {
            String[] field = parts[i].split("=", 2);
            fields.put(field[0], field[1]);
        }
        return new Booking(fields.get("id"), fields.get("email"),
                Arrays.asList(fields.get("seats").split(" ")),
                new Money(new BigDecimal(fields.get("total"))));
    }
}
