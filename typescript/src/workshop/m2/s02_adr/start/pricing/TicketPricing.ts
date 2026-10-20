import type { GroupMailer } from '../notification/GroupMailer.js';

/**
 * Start: cennik narusza oba punkty ADR-0007 - sam wysyła powiadomienie (zależność
 * pricing -> notification) i liczy kwoty w number.
 */
export class TicketPricing {
  constructor(private readonly mailer: GroupMailer) {}

  total(organizer: string, tickets: number, unitPrice: number): number {
    let sum = unitPrice * tickets;
    if (tickets >= 10) {
      sum = sum - sum * 0.10;
      this.mailer.groupDiscountGranted(organizer, tickets);
    }
    return Math.round(sum * 100) / 100.0;
  }
}
