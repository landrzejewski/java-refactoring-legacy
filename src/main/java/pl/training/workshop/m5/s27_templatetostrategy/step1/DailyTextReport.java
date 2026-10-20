package pl.training.workshop.m5.s27_templatetostrategy.step1;

import java.time.LocalDate;

/** Krok 1: podklasa wybiera format przez konstruktor; zostały w niej tylko haki okresu. */
public final class DailyTextReport extends ScreeningReport {
    public DailyTextReport() {
        super(new TextFormat());
    }

    @Override
    protected boolean includes(LocalDate day, LocalDate saleDay) {
        return saleDay.equals(day);
    }

    @Override
    protected String label(LocalDate day) {
        return "dzien " + day;
    }
}
