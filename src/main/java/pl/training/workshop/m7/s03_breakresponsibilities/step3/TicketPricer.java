package pl.training.workshop.m7.s03_breakresponsibilities.step3;

import java.math.BigDecimal;

import pl.training.workshop.m7.s03_breakresponsibilities.BookingRequest;

/** Krok 2: Extract Class - cennik ma jednego właściciela i zwraca wynik zamiast dwóch zmiennych. */
final class TicketPricer {
    Pricing price(BookingRequest request) {
        BigDecimal base = switch (request.format()) {
            case "IMAX" -> new BigDecimal("40.00");
            case "3D" -> new BigDecimal("32.00");
            default -> new BigDecimal("25.00");
        };
        BigDecimal total = BigDecimal.ZERO;
        int vipSeats = 0;
        for (String seat : request.seats()) {
            total = total.add(base);
            if (Integer.parseInt(seat.substring(1)) >= 10) {
                total = total.add(new BigDecimal("10.00"));
                vipSeats++;
            }
        }
        return new Pricing(total, vipSeats);
    }
}
