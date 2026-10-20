package pl.training.workshop.m5.s27_templatetostrategy.step1;

import pl.training.workshop.shared.Money;

/** Krok 1: Strategy dla osi "format" - trzy haki formatu wyjęte z hierarchii. */
public interface ReportFormat {
    String header(String label);

    String row(String title, int tickets, Money revenue);

    String footer(int tickets, Money revenue);
}
