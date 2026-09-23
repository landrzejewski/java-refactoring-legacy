package pl.training.workshop.m3.s10_isp.step2;

import java.math.BigDecimal;

/** Rola z perspektywy raportu. */
public interface SalesFigures {
    BigDecimal dailyRevenue();

    int ticketsSold(String title);
}
