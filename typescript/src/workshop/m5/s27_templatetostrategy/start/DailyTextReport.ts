import type { Money } from '../../../shared/Money.js';
import type { LocalDate } from '../../../shared/time.js';
import { ScreeningReport } from './ScreeningReport.js';

/** Start: okres dzienny skopiowany z DailyCsvReport + format tekstowy z kolumnami. */
export class DailyTextReport extends ScreeningReport {
  protected override includes(day: LocalDate, saleDay: LocalDate): boolean {
    return saleDay.equals(day);
  }

  protected override label(day: LocalDate): string {
    return `dzien ${day.toString()}`;
  }

  protected override header(label: string): string {
    return `RAPORT ${label.toUpperCase()}\n${'-'.repeat(37)}\n`;
  }

  protected override row(title: string, tickets: number, revenue: Money): string {
    return `${title.padEnd(20)} ${String(tickets).padStart(5)} ${revenue.toString().padStart(10)}\n`;
  }

  protected override footer(tickets: number, revenue: Money): string {
    return `${'-'.repeat(37)}\n${this.row('RAZEM', tickets, revenue)}`;
  }
}
