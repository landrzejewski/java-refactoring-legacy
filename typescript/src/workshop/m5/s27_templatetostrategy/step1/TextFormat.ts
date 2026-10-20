import type { Money } from '../../../shared/Money.js';
import type { ReportFormat } from './ReportFormat.js';

/** Krok 1: format tekstowy w jednym miejscu (był skopiowany w DailyTextReport i WeeklyTextReport). */
export class TextFormat implements ReportFormat {
  header(label: string): string {
    return `RAPORT ${label.toUpperCase()}\n${'-'.repeat(37)}\n`;
  }

  row(title: string, tickets: number, revenue: Money): string {
    return `${title.padEnd(20)} ${String(tickets).padStart(5)} ${revenue.toString().padStart(10)}\n`;
  }

  footer(tickets: number, revenue: Money): string {
    return `${'-'.repeat(37)}\n${this.row('RAZEM', tickets, revenue)}`;
  }
}
