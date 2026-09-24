import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { TicketService } from './TicketService.js';

/** Klient 1: aplikacja mobilna - zawsze online, okulary zawsze z kina. */
export class MobileApp {
  private readonly tickets: TicketService;

  constructor(tickets: TicketService) {
    this.tickets = requireNonNull(tickets, 'tickets');
  }

  buy(title: string, format: string, seats: number): string {
    return this.tickets.book(title, format, seats, true, false);
  }
}
