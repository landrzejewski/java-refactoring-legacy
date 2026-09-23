package pl.training.workshop.m7.s10_booleanparameter.step4;

import java.math.BigDecimal;

/**
 * Krok 4 (rozwiązanie): wszyscy klienci zmigrowani, kompilator nie zgłasza już ostrzeżeń
 * o przestarzałym API, więc book(..., boolean, boolean) usunięte (Safe Delete).
 * Uwaga: w bibliotece publicznej usunięcie łamie zgodność binarną - tu to decyzja
 * o końcu okresu przejściowego.
 */
public final class TicketService {
    private static final BigDecimal GLASSES = new BigDecimal("3.00");
    private static final BigDecimal FEE = new BigDecimal("2.00");

    private enum Channel { ONLINE, BOX_OFFICE }

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
