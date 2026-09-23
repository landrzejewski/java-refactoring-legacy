package pl.training.workshop.m3.s02_similarity.start;

import java.math.BigDecimal;
import java.util.List;

/** Sprzedaż online: suma biletów plus opłata rezerwacyjna za każdy bilet. */
public final class OnlineCheckout {
    public BigDecimal total(List<BigDecimal> ticketPrices) {
        BigDecimal tickets = ticketPrices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return tickets.add(ServiceFee.of(ServiceFee.Kind.ONLINE_BOOKING, ticketPrices.size()));
    }
}
