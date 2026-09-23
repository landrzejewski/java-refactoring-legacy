package pl.training.workshop.m3.s02_similarity.step2;

import java.math.BigDecimal;
import java.util.List;

/**
 * Krok 2: Simplify - warunek na stałej zawsze prawdziwy, martwa gałąź usunięta.
 * ServiceFee usunięty (Safe Delete), bo nikt go już nie używa.
 */
public final class OnlineCheckout {
    public BigDecimal total(List<BigDecimal> ticketPrices) {
        BigDecimal tickets = ticketPrices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return tickets.add(new BigDecimal("2.00").multiply(BigDecimal.valueOf(ticketPrices.size())));
    }
}
