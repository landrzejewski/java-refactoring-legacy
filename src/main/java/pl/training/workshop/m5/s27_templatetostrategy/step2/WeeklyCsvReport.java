package pl.training.workshop.m5.s27_templatetostrategy.step2;

/** Krok 2: podklasa to już tylko para strategii - kandydat do usunięcia (Inline Class). */
public final class WeeklyCsvReport extends ScreeningReport {
    public WeeklyCsvReport() {
        super(new WeeklyPeriod(), new CsvFormat());
    }
}
