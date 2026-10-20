import type { LocalDate } from '../../../shared/time.js';
import { TextFormat } from './TextFormat.js';
import { ScreeningReport } from './ScreeningReport.js';

/** Krok 1: podklasa wybiera format przez konstruktor; zostały w niej tylko haki okresu. */
export class DailyTextReport extends ScreeningReport {
  constructor() {
    super(new TextFormat());
  }

  protected override includes(day: LocalDate, saleDay: LocalDate): boolean {
    return saleDay.equals(day);
  }

  protected override label(day: LocalDate): string {
    return `dzien ${day.toString()}`;
  }
}
