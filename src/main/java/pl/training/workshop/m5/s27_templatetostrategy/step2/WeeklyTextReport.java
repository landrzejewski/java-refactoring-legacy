package pl.training.workshop.m5.s27_templatetostrategy.step2;

/** Krok 2: podklasa to już tylko para strategii - kandydat do usunięcia (Inline Class). */
public final class WeeklyTextReport extends ScreeningReport {
    public WeeklyTextReport() {
        super(new WeeklyPeriod(), new TextFormat());
    }
}
