package pl.training.workshop.m5.s27_templatetostrategy.step3;

import java.time.LocalDate;

/** Krok 3: okres dzienny w jednym miejscu (był skopiowany w DailyCsvReport i DailyTextReport). */
public final class DailyPeriod implements ReportPeriod {
    @Override
    public boolean includes(LocalDate day, LocalDate saleDay) {
        return saleDay.equals(day);
    }

    @Override
    public String label(LocalDate day) {
        return "dzien " + day;
    }
}
