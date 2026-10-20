package pl.training.workshop.m5.s27_templatetostrategy.step2;

/** Krok 2: podklasa to już tylko para strategii - kandydat do usunięcia (Inline Class). */
public final class DailyCsvReport extends ScreeningReport {
    public DailyCsvReport() {
        super(new DailyPeriod(), new CsvFormat());
    }
}
