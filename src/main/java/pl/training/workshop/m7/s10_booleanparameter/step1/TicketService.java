package pl.training.workshop.m7.s10_booleanparameter.step1;

import java.math.BigDecimal;

/**
 * Krok 1: flaga online zastąpiona jawnymi metodami bookOnline / bookAtBoxOffice.
 * Stara metoda zostaje jako @Deprecated i deleguje - klienci migrują po jednym.
 */
public final class TicketService {
    private static final BigDecimal GLASSES = new BigDecimal("3.00");
    private static final BigDecimal FEE = new BigDecimal("2.00");

    private enum Channel { ONLINE, BOX_OFFICE }

    /** @deprecated użyj {@link #bookOnline} albo {@link #bookAtBoxOffice} */
    @Deprecated
    public String book(String title, String format, int seats, boolean online, boolean ownGlasses) {
        return online
                ? bookOnline(title, format, seats, ownGlasses)
                : bookAtBoxOffice(title, format, seats, ownGlasses);
    }

    public String bookOnline(String title, String format, int seats, boolean ownGlasses) {
        return book(title, format, seats, Channel.ONLINE, ownGlasses);
    }

    public String bookAtBoxOffice(String title, String format, int seats, boolean ownGlasses) {
        return book(title, format, seats, Channel.BOX_OFFICE, ownGlasses);
    }

    private String book(String title, String format, int seats, Channel channel, boolean ownGlasses) {
        BigDecimal base = switch (format) {
            case "IMAX" -> new BigDecimal("40.00");
            case "3D" -> new BigDecimal("32.00");
            default -> new BigDecimal("25.00");
        };
        BigDecimal count = BigDecimal.valueOf(seats);
        BigDecimal total = base.multiply(count);
        if (format.equals("3D") && !ownGlasses) {
            total = total.add(GLASSES.multiply(count));
        }
        if (channel == Channel.ONLINE) {
            total = total.add(FEE.multiply(count));
        }
        return title + " " + format + " x" + seats
                + (channel == Channel.ONLINE ? " online" : " kasa") + ": " + total;
    }
}
