import type { LocalDate } from '../../../shared/time.js';
import type { ReportPeriod } from './ReportPeriod.js';

/** Krok 3: okres dzienny w jednym miejscu (był skopiowany w DailyCsvReport i DailyTextReport). */
export class DailyPeriod implements ReportPeriod {
  includes(day: LocalDate, saleDay: LocalDate): boolean {
    return saleDay.equals(day);
  }

  label(day: LocalDate): string {
    return `dzien ${day.toString()}`;
  }
}
