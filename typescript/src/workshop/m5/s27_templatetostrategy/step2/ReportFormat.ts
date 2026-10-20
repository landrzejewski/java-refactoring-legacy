import type { Money } from '../../../shared/Money.js';

/** Krok 2: Strategy (bez zmian) dla osi "format" - trzy haki formatu wyjęte z hierarchii. */
export interface ReportFormat {
  header(label: string): string;

  row(title: string, tickets: number, revenue: Money): string;

  footer(tickets: number, revenue: Money): string;
}
