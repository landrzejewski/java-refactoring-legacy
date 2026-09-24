import type { Decimal } from 'decimal.js';

/** Krok 1: rola z perspektywy raportu. */
export interface SalesFigures {
  dailyRevenue(): Decimal;

  ticketsSold(title: string): number;
}
