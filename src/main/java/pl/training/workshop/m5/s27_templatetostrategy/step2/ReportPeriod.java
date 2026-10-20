package pl.training.workshop.m5.s27_templatetostrategy.step2;

import java.time.LocalDate;

/** Krok 2: Strategy dla osi "okres" - dwa haki okresu wyjęte z hierarchii. */
public interface ReportPeriod {
    boolean includes(LocalDate day, LocalDate saleDay);

    String label(LocalDate day);
}
