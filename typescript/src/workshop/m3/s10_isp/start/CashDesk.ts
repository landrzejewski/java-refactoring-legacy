import type { CinemaAdminService } from './CinemaAdminService.js';

/** Klient: kasa. Używa sellTicket i refundTicket, a zależy od całego zaplecza. */
export class CashDesk {
  constructor(private readonly backOffice: CinemaAdminService) {}

  sell(title: string, seat: number): string {
    return `bilet ${this.backOffice.sellTicket(title, seat)}: ${title}, miejsce ${seat}`;
  }

  refund(ticketId: string): string {
    return this.backOffice.refundTicket(ticketId);
  }
}
