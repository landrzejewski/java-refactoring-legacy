import { DAYS_OF_WEEK, type LocalDate } from '../../../shared/time.js';
import type { ReportPeriod } from './ReportPeriod.js';

/** Krok 3: tydzień poniedziałek-niedziela w jednym miejscu (był skopiowany w obu Weekly*Report). */
export class WeeklyPeriod implements ReportPeriod {
  includes(day: LocalDate, saleDay: LocalDate): boolean {
    const monday = WeeklyPeriod.monday(day);
    return !saleDay.isBefore(monday) && !saleDay.isAfter(monday.plusDays(6));
  }

  label(day: LocalDate): string {
    const monday = WeeklyPeriod.monday(day);
    return `tydzien ${monday.toString()}..${monday.plusDays(6).toString()}`;
  }

  // Odpowiednik day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).
  private static monday(day: LocalDate): LocalDate {
    return day.plusDays(-DAYS_OF_WEEK.indexOf(day.dayOfWeek));
  }
}
