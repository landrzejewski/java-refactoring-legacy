package pl.training.workshop.m4.s03_magicnumbers.step2;

import java.math.BigDecimal;
import java.math.RoundingMode;

import pl.training.workshop.m4.s03_magicnumbers.Order;
import pl.training.workshop.m4.s03_magicnumbers.Ticket;

/**
 * Krok 2: Replace Magic Numbers - progi i współczynniki reguł. Trzy "dziesiątki" dostają TRZY
 * różne stałe (VIP_FROM_ROW, GROUP_MIN_TICKETS, AMOUNT_PER_LOYALTY_POINT), bo zmieniają się
 * z różnych powodów. Nazwa opisuje rolę, nie wartość - żadnego TEN.
 */
public final class OrderPricer {
    private static final BigDecimal BASE_PRICE_2D = new BigDecimal("25.00");
    private static final BigDecimal BASE_PRICE_3D = new BigDecimal("32.00");
    private static final BigDecimal BASE_PRICE_IMAX = new BigDecimal("40.00");
    private static final BigDecimal MORNING_REDUCTION = new BigDecimal("5.00");
    private static final BigDecimal VIP_SURCHARGE = new BigDecimal("10.00");
    private static final BigDecimal ONLINE_FEE_PER_TICKET = new BigDecimal("2.00");
    private static final BigDecimal NO_FEE = new BigDecimal("0.00");
    private static final int MORNING_ENDS_AT_HOUR = 12;
    private static final int VIP_FROM_ROW = 10;
    private static final int GROUP_MIN_TICKETS = 10;
    private static final BigDecimal GROUP_PRICE_FACTOR = new BigDecimal("0.90");
    private static final BigDecimal AMOUNT_PER_LOYALTY_POINT = BigDecimal.TEN;

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
            if (o.start().getHour() < MORNING_ENDS_AT_HOUR) {
                p = p.subtract(MORNING_REDUCTION);
            }
            if (t.row() >= VIP_FROM_ROW) {
                p = p.add(VIP_SURCHARGE);
            }
            tickets = tickets.add(p);
        }
        if (o.tickets().size() >= GROUP_MIN_TICKETS) {
            tickets = tickets.multiply(GROUP_PRICE_FACTOR);
        }
        tickets = tickets.setScale(2, RoundingMode.HALF_UP);
        BigDecimal fee = o.online()
                ? ONLINE_FEE_PER_TICKET.multiply(BigDecimal.valueOf(o.tickets().size()))
                : NO_FEE;
        int points = tickets.divide(AMOUNT_PER_LOYALTY_POINT, 0, RoundingMode.DOWN).intValue();
        return "Bilety: " + tickets + ", oplata: " + fee + ", razem: " + tickets.add(fee)
                + ", punkty: " + points;
    }
}
