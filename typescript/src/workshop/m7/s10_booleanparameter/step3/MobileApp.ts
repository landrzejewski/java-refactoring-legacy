import { requireNonNull } from '../../../../shared/requireNonNull.js';
import { Glasses } from './Glasses.js';
import type { TicketService } from './TicketService.js';

/**
 * Krok 3: klient zmigrowany - wywołanie mówi "online, okulary z kina"
 * bez zaglądania do sygnatury.
 */
export class MobileApp {
  private readonly tickets: TicketService;

  constructor(tickets: TicketService) {
    this.tickets = requireNonNull(tickets, 'tickets');
  }

  buy(title: string, format: string, seats: number): string {
    return this.tickets.bookOnline(title, format, seats, Glasses.RENTED);
  }
}
