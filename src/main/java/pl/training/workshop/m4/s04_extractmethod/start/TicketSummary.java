package pl.training.workshop.m4.s04_extractmethod.start;

import java.math.BigDecimal;
import java.math.RoundingMode;

import pl.training.workshop.m4.s04_extractmethod.Order;

public final class TicketSummary {
    public String describe(Order order) {
        // cena bazowa formatu
        BigDecimal base;
        if (order.format().equals("IMAX")) {
            base = new BigDecimal("40.00");
        } else if (order.format().equals("3D")) {
            base = new BigDecimal("32.00");
        } else {
            base = new BigDecimal("25.00");
        }
        if (order.start().getHour() < 12) {
            base = base.subtract(new BigDecimal("5.00"));
        }

        // suma i liczba miejsc VIP
        BigDecimal subtotal = BigDecimal.ZERO;
        int vipSeats = 0;
        for (int row : order.rows()) {
            BigDecimal price = base;
            if (row >= 10) {
                price = price.add(new BigDecimal("10.00"));
                vipSeats++;
            }
            subtotal = subtotal.add(price);
        }
        subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);

        // dokument
        StringBuilder text = new StringBuilder();
        text.append("BILETY: ").append(order.title()).append('\n');
        text.append("Format: ").append(order.format())
                .append(", start ").append(order.start()).append('\n');
        text.append("Miejsc: ").append(order.rows().size());
        if (vipSeats > 0) {
            text.append(" (w tym VIP: ").append(vipSeats).append(')');
        }
        text.append('\n');
        text.append("Razem: ").append(subtotal).append('\n');
        return text.toString();
    }
}
