package pl.training.workshop.m4.s03_magicnumbers.step1;

import java.math.BigDecimal;
import java.math.RoundingMode;

import pl.training.workshop.m4.s03_magicnumbers.Order;
import pl.training.workshop.m4.s03_magicnumbers.Ticket;

/**
 * Krok 1: Extract Constant dla kwot z cennika (ceny bazowe, poranek, VIP, opłata online).
 * {@code private static final BigDecimal} - jeden właściciel, niezmienny obiekt,
 * a przy okazji koniec z tworzeniem BigDecimal w każdym obrocie pętli.
 */
public final class OrderPricer {
    private static final BigDecimal BASE_PRICE_2D = new BigDecimal("25.00");
    private static final BigDecimal BASE_PRICE_3D = new BigDecimal("32.00");
    private static final BigDecimal BASE_PRICE_IMAX = new BigDecimal("40.00");
    private static final BigDecimal MORNING_REDUCTION = new BigDecimal("5.00");
    private static final BigDecimal VIP_SURCHARGE = new BigDecimal("10.00");
    private static final BigDecimal ONLINE_FEE_PER_TICKET = new BigDecimal("2.00");
    private static final BigDecimal NO_FEE = new BigDecimal("0.00");

    public String summary(Order o) {
        BigDecimal tickets = BigDecimal.ZERO;
        for (Ticket t : o.tickets()) {
            BigDecimal p = o.format() == 3 ? BASE_PRICE_IMAX
                    : o.format() == 2 ? BASE_PRICE_3D : BASE_PRICE_2D;
            if (t.type().equals("S")) {
                p = p.multiply(BigDecimal.ONE.subtract(new BigDecimal("0.25")));
            } else if (t.type().equals("E")) {
                p = p.multiply(BigDecimal.ONE.subtract(new BigDecimal("0.30")));
            } else if (t.type().equals("C")) {
                p = p.multiply(BigDecimal.ONE.subtract(new BigDecimal("0.40")));
            }
            if (o.start().getHour() < 12) {
                p = p.subtract(MORNING_REDUCTION);
            }
            if (t.row() >= 10) {
                p = p.add(VIP_SURCHARGE);
            }
            tickets = tickets.add(p);
        }
        if (o.tickets().size() >= 10) {
            tickets = tickets.multiply(new BigDecimal("0.90"));
        }
        tickets = tickets.setScale(2, RoundingMode.HALF_UP);
        BigDecimal fee = o.online()
                ? ONLINE_FEE_PER_TICKET.multiply(BigDecimal.valueOf(o.tickets().size()))
                : NO_FEE;
        int points = tickets.divide(BigDecimal.TEN, 0, RoundingMode.DOWN).intValue();
        return "Bilety: " + tickets + ", oplata: " + fee + ", razem: " + tickets.add(fee)
                + ", punkty: " + points;
    }
}
