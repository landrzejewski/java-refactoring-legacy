import type { Money } from '../../../shared/Money.js';
import { DAYS_OF_WEEK, type LocalDate } from '../../../shared/time.js';
import { ScreeningReport } from './ScreeningReport.js';

/** Start: okres tygodniowy skopiowany z WeeklyCsvReport + format tekstowy skopiowany z DailyTextReport. */
export class WeeklyTextReport extends ScreeningReport {
  protected override includes(day: LocalDate, saleDay: LocalDate): boolean {
    const monday = day.plusDays(-DAYS_OF_WEEK.indexOf(day.dayOfWeek));
    return !saleDay.isBefore(monday) && !saleDay.isAfter(monday.plusDays(6));
  }

  protected override label(day: LocalDate): string {
    const monday = day.plusDays(-DAYS_OF_WEEK.indexOf(day.dayOfWeek));
    return `tydzien ${monday.toString()}..${monday.plusDays(6).toString()}`;
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
