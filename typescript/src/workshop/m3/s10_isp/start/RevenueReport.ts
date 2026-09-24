import type { CinemaAdminService } from './CinemaAdminService.js';

/** Klient: raport. Używa dailyRevenue i ticketsSold. */
export class RevenueReport {
  constructor(private readonly backOffice: CinemaAdminService) {}

  summary(title: string): string {
    return `${title}: ${this.backOffice.ticketsSold(title)} biletow, dzien: `
      + this.backOffice.dailyRevenue().toFixed(2);
  }
}
