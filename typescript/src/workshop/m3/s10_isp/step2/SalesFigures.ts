import type { Decimal } from 'decimal.js';

/** Rola z perspektywy raportu. */
export interface SalesFigures {
  dailyRevenue(): Decimal;

  ticketsSold(title: string): number;
}
