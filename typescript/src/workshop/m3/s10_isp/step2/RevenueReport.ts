import type { SalesFigures } from './SalesFigures.js';

/** Raport zależy tylko od roli SalesFigures. */
export class RevenueReport {
  constructor(private readonly backOffice: SalesFigures) {}

  summary(title: string): string {
    return `${title}: ${this.backOffice.ticketsSold(title)} biletow, dzien: `
      + this.backOffice.dailyRevenue().toFixed(2);
  }
}
