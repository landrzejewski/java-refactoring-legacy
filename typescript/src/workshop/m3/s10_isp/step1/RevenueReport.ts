import type { SalesFigures } from './SalesFigures.js';

/** Krok 1: raport zależy tylko od roli SalesFigures. */
export class RevenueReport {
  constructor(private readonly backOffice: SalesFigures) {}

  summary(title: string): string {
    return `${title}: ${this.backOffice.ticketsSold(title)} biletow, dzien: `
      + this.backOffice.dailyRevenue().toFixed(2);
  }
}
