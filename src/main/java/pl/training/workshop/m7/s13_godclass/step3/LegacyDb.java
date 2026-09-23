package pl.training.workshop.m7.s13_godclass.step3;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/** "Baza danych" starego systemu - globalne, mutowalne mapy. Krok 3: rezerwacje jako typ Booking. */
public final class LegacyDb {
    public static final Map<String, Object[]> SCREENINGS = new LinkedHashMap<>();
    public static final Map<String, Booking> BOOKINGS = new LinkedHashMap<>();
    public static final Map<String, Integer> LOYALTY = new HashMap<>();
    public static int sequence = 1;

    private LegacyDb() {
    }

    public static void clear() {
        SCREENINGS.clear();
        BOOKINGS.clear();
        LOYALTY.clear();
        sequence = 1;
        LegacyMailer.SENT.clear();
        LegacyPaymentGateway.CHARGES.clear();
    }
}
