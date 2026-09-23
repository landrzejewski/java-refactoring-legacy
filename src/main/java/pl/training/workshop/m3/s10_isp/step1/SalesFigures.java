package pl.training.workshop.m3.s10_isp.step1;

import java.math.BigDecimal;

/** Krok 1: rola z perspektywy raportu. */
public interface SalesFigures {
    BigDecimal dailyRevenue();

    int ticketsSold(String title);
}
