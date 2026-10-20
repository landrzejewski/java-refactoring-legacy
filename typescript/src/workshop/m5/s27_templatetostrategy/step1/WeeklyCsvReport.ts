import { DAYS_OF_WEEK, type LocalDate } from '../../../shared/time.js';
import { CsvFormat } from './CsvFormat.js';
import { ScreeningReport } from './ScreeningReport.js';

/** Krok 1: podklasa wybiera format przez konstruktor; zostały w niej tylko haki okresu. */
export class WeeklyCsvReport extends ScreeningReport {
  constructor() {
    super(new CsvFormat());
  }

  protected override includes(day: LocalDate, saleDay: LocalDate): boolean {
    const monday = day.plusDays(-DAYS_OF_WEEK.indexOf(day.dayOfWeek));
    return !saleDay.isBefore(monday) && !saleDay.isAfter(monday.plusDays(6));
  }

  protected override label(day: LocalDate): string {
    const monday = day.plusDays(-DAYS_OF_WEEK.indexOf(day.dayOfWeek));
    return `tydzien ${monday.toString()}..${monday.plusDays(6).toString()}`;
  }
}
