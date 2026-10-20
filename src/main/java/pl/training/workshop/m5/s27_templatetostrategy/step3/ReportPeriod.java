package pl.training.workshop.m5.s27_templatetostrategy.step3;

import java.time.LocalDate;

/** Krok 3: Strategy (bez zmian od kroku 2) dla osi "okres" - dwa haki okresu wyjęte z hierarchii. */
public interface ReportPeriod {
    boolean includes(LocalDate day, LocalDate saleDay);

    String label(LocalDate day);
}
