package pl.training.workshop.m4.s03_magicnumbers.start;

import java.math.BigDecimal;
import java.math.RoundingMode;

import pl.training.workshop.m4.s03_magicnumbers.Order;
import pl.training.workshop.m4.s03_magicnumbers.Ticket;

/**
 * Start: podsumowanie zamówienia pełne magicznych liczb. Cztery różne "dziesiątki":
 * rząd VIP, dopłata VIP, próg grupy i kwota za punkt lojalnościowy. To cztery różne decyzje.
 */
public final class OrderPricer {
    public String summary(Order o) {
        BigDecimal tickets = BigDecimal.ZERO;
        for (Ticket t : o.tickets()) {
            BigDecimal p = o.format() == 3 ? new BigDecimal("40.00")
                    : o.format() == 2 ? new BigDecimal("32.00") : new BigDecimal("25.00");
            if (t.type().equals("S")) {
                p = p.multiply(BigDecimal.ONE.subtract(new BigDecimal("0.25")));
            } else if (t.type().equals("E")) {
                p = p.multiply(BigDecimal.ONE.subtract(new BigDecimal("0.30")));
            } else if (t.type().equals("C")) {
                p = p.multiply(BigDecimal.ONE.subtract(new BigDecimal("0.40")));
            }
            if (o.start().getHour() < 12) {
                p = p.subtract(new BigDecimal("5.00"));
            }
            if (t.row() >= 10) {
                p = p.add(new BigDecimal("10.00"));
            }
            tickets = tickets.add(p);
        }
        if (o.tickets().size() >= 10) {
            tickets = tickets.multiply(new BigDecimal("0.90"));
        }
        tickets = tickets.setScale(2, RoundingMode.HALF_UP);
        BigDecimal fee = o.online()
                ? new BigDecimal("2.00").multiply(BigDecimal.valueOf(o.tickets().size()))
                : new BigDecimal("0.00");
        int points = tickets.divide(BigDecimal.TEN, 0, RoundingMode.DOWN).intValue();
        return "Bilety: " + tickets + ", oplata: " + fee + ", razem: " + tickets.add(fee)
                + ", punkty: " + points;
    }
}
