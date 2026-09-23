package pl.training.workshop.m3.s01_dryknowledge.start;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;

import pl.training.workshop.m3.s01_dryknowledge.Ticket;

/**
 * Start: reguła ceny biletu (cena formatu, zniżka typu, seans poranny) jest zapisana
 * DWA razy - w sprzedaży i w zwrocie. Tekst jest inny (switch kontra if i mnożniki),
 * więc detektor duplikatów w IDE nic nie znajdzie, ale wiedza jest ta sama:
 * zmiana zniżki studenckiej wymaga zgodnej edycji dwóch miejsc.
 */
public final class BoxOffice {
    public BigDecimal sell(Ticket ticket) {
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

    public BigDecimal refund(Ticket ticket, long hoursBeforeStart) {
        // ile kosztował bilet
        BigDecimal paid = new BigDecimal("25.00");
        if (ticket.format().equals("3D")) {
            paid = new BigDecimal("32.00");
        } else if (ticket.format().equals("IMAX")) {
            paid = new BigDecimal("40.00");
        }
        if (ticket.type().equals("STUDENT")) {
            paid = paid.multiply(new BigDecimal("0.75"));
        } else if (ticket.type().equals("SENIOR")) {
            paid = paid.multiply(new BigDecimal("0.70"));
        } else if (ticket.type().equals("CHILD")) {
            paid = paid.multiply(new BigDecimal("0.60"));
        }
        if (ticket.start().isBefore(LocalTime.NOON)) {
            paid = paid.subtract(new BigDecimal("5.00"));
        }
        // zwrot: >= 24h 100%, < 24h 50%, po starcie 0%; potrącenie 3.00
        int percent = hoursBeforeStart >= 24 ? 100 : hoursBeforeStart > 0 ? 50 : 0;
        BigDecimal refund = paid.multiply(BigDecimal.valueOf(percent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .subtract(new BigDecimal("3.00"));
        return refund.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }
}
