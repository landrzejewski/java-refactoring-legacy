package pl.training.workshop.m3.s01_dryknowledge.step3;

import java.math.BigDecimal;
import java.math.RoundingMode;

import pl.training.workshop.m3.s01_dryknowledge.Ticket;

/**
 * Krok 3: Substitute Algorithm - zwrot korzysta z {@code ticketPrice},
 * kopia {@code paidFor} usunięta (Safe Delete). Test równoważności potwierdza,
 * że obie kopie reguły dawały te same kwoty dla wszystkich przypadków.
 */
public final class BoxOffice {
    public BigDecimal sell(Ticket ticket) {
        return ticketPrice(ticket);
    }

    public BigDecimal refund(Ticket ticket, long hoursBeforeStart) {
        BigDecimal paid = ticketPrice(ticket);
        // zwrot: >= 24h 100%, < 24h 50%, po starcie 0%; potrącenie 3.00
        int percent = hoursBeforeStart >= 24 ? 100 : hoursBeforeStart > 0 ? 50 : 0;
        BigDecimal refund = paid.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .subtract(new BigDecimal("3.00"));
        return refund.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal ticketPrice(Ticket ticket) {
        BigDecimal base = switch (ticket.format()) {
            case "IMAX" -> new BigDecimal("40.00");
            case "3D" -> new BigDecimal("32.00");
            default -> new BigDecimal("25.00");
        };
        int discount = switch (ticket.type()) {
            case "STUDENT" -> 25;
            case "SENIOR" -> 30;
            case "CHILD" -> 40;
            default -> 0;
        };
        BigDecimal price = base.subtract(base.multiply(BigDecimal.valueOf(discount))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        if (ticket.start().getHour() < 12) {
            price = price.subtract(new BigDecimal("5.00"));
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }
}
