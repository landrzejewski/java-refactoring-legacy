import type { TicketSales } from './TicketSales.js';

/** Kasa zależy tylko od roli TicketSales. */
export class CashDesk {
  constructor(private readonly backOffice: TicketSales) {}

  sell(title: string, seat: number): string {
    return `bilet ${this.backOffice.sellTicket(title, seat)}: ${title}, miejsce ${seat}`;
  }

  refund(ticketId: string): string {
    return this.backOffice.refundTicket(ticketId);
  }
}
