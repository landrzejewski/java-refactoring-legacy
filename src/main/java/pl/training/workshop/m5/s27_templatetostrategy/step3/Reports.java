package pl.training.workshop.m5.s27_templatetostrategy.step3;

import java.util.Map;

/**
 * Stabilny punkt wejścia sceny. Krok 3: osie wybierane niezależnie - dwie mapy zamiast
 * zagnieżdżonego switch. Kolejność walidacji (najpierw okres, potem format) jak w start.
 */
public final class Reports {
    private static final Map<String, ReportPeriod> PERIODS = Map.of(
            "DAILY", new DailyPeriod(),
            "WEEKLY", new WeeklyPeriod());
    private static final Map<String, ReportFormat> FORMATS = Map.of(
            "CSV", new CsvFormat(),
            "TEXT", new TextFormat());

    private Reports() {
    }

    public static ScreeningReport of(String period, String format) {
        ReportPeriod chosenPeriod = PERIODS.get(period);
        if (chosenPeriod == null) {
            throw new IllegalArgumentException("unknown period: " + period);
        }
        ReportFormat chosenFormat = FORMATS.get(format);
        if (chosenFormat == null) {
            throw new IllegalArgumentException("unknown format: " + format);
        }
        return new ScreeningReport(chosenPeriod, chosenFormat);
    }
}
