import type { Money } from '../../../shared/Money.js';
import { DAYS_OF_WEEK, type LocalDate } from '../../../shared/time.js';
import { ScreeningReport } from './ScreeningReport.js';

/** Start: okres tygodniowy (poniedziałek-niedziela) + format CSV skopiowany z DailyCsvReport. */
export class WeeklyCsvReport extends ScreeningReport {
  protected override includes(day: LocalDate, saleDay: LocalDate): boolean {
    const monday = day.plusDays(-DAYS_OF_WEEK.indexOf(day.dayOfWeek));
    return !saleDay.isBefore(monday) && !saleDay.isAfter(monday.plusDays(6));
  }

  protected override label(day: LocalDate): string {
    const monday = day.plusDays(-DAYS_OF_WEEK.indexOf(day.dayOfWeek));
    return `tydzien ${monday.toString()}..${monday.plusDays(6).toString()}`;
  }

  protected override header(label: string): string {
    return `# raport ${label}\nfilm;bilety;przychod\n`;
  }

  protected override row(title: string, tickets: number, revenue: Money): string {
    const cell = title.includes(';') ? `"${title}"` : title;
    return `${cell};${tickets};${revenue.toString()}\n`;
  }

  protected override footer(tickets: number, revenue: Money): string {
    return `SUMA;${tickets};${revenue.toString()}\n`;
  }
}
