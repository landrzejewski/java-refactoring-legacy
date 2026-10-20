import type { Money } from '../../../shared/Money.js';
import type { LocalDate } from '../../../shared/time.js';
import { ScreeningReport } from './ScreeningReport.js';

/** Start: okres dzienny + format CSV. */
export class DailyCsvReport extends ScreeningReport {
  protected override includes(day: LocalDate, saleDay: LocalDate): boolean {
    return saleDay.equals(day);
  }

  protected override label(day: LocalDate): string {
    return `dzien ${day.toString()}`;
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
