import type { Money } from '../../../shared/Money.js';
import type { ReportFormat } from './ReportFormat.js';

/** Krok 1: format CSV w jednym miejscu (był skopiowany w DailyCsvReport i WeeklyCsvReport). */
export class CsvFormat implements ReportFormat {
  header(label: string): string {
    return `# raport ${label}\nfilm;bilety;przychod\n`;
  }

  row(title: string, tickets: number, revenue: Money): string {
    const cell = title.includes(';') ? `"${title}"` : title;
    return `${cell};${tickets};${revenue.toString()}\n`;
  }

  footer(tickets: number, revenue: Money): string {
    return `SUMA;${tickets};${revenue.toString()}\n`;
  }
}
