package pl.training.workshop.m7.s10_booleanparameter.step2;

import java.math.BigDecimal;

/**
 * Krok 2: druga flaga (ownGlasses) w nowych metodach zamieniona na enum Glasses.
 * Nie dokładamy metody na każdą kombinację (4 metody) - kanał to metoda, okulary to wartość.
 * Stara metoda @Deprecated tłumaczy boolean na Glasses.
 */
public final class TicketService {
    private static final BigDecimal GLASSES = new BigDecimal("3.00");
    private static final BigDecimal FEE = new BigDecimal("2.00");

    private enum Channel { ONLINE, BOX_OFFICE }

    /** @deprecated użyj {@link #bookOnline} albo {@link #bookAtBoxOffice} */
    @Deprecated
    public String book(String title, String format, int seats, boolean online, boolean ownGlasses) {
        Glasses glasses = ownGlasses ? Glasses.OWN : Glasses.RENTED;
        return online
                ? bookOnline(title, format, seats, glasses)
                : bookAtBoxOffice(title, format, seats, glasses);
    }

    public String bookOnline(String title, String format, int seats, Glasses glasses) {
        return book(title, format, seats, Channel.ONLINE, glasses);
    }

    public String bookAtBoxOffice(String title, String format, int seats, Glasses glasses) {
        return book(title, format, seats, Channel.BOX_OFFICE, glasses);
    }

    private String book(String title, String format, int seats, Channel channel, Glasses glasses) {
        BigDecimal base = switch (format) {
            case "IMAX" -> new BigDecimal("40.00");
            case "3D" -> new BigDecimal("32.00");
            default -> new BigDecimal("25.00");
        };
        BigDecimal count = BigDecimal.valueOf(seats);
        BigDecimal total = base.multiply(count);
        if (format.equals("3D") && glasses == Glasses.RENTED) {
            total = total.add(GLASSES.multiply(count));
        }
        if (channel == Channel.ONLINE) {
            total = total.add(FEE.multiply(count));
        }
        return title + " " + format + " x" + seats
                + (channel == Channel.ONLINE ? " online" : " kasa") + ": " + total;
    }
}
