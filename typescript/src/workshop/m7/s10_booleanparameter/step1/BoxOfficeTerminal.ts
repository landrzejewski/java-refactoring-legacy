import { requireNonNull } from '../../../../shared/requireNonNull.js';
import type { TicketService } from './TicketService.js';

/**
 * Klient 2 (jeszcze niezmigrowany): edytor przekreśla wywołanie przestarzałego book().
 * customerHasGlasses to dana z formularza, nie flaga sterująca -
 * nie każdy boolean jest zapachem.
 */
export class BoxOfficeTerminal {
  private readonly tickets: TicketService;

  constructor(tickets: TicketService) {
    this.tickets = requireNonNull(tickets, 'tickets');
  }

  sell(title: string, format: string, seats: number, customerHasGlasses: boolean): string {
    return this.tickets.book(title, format, seats, false, customerHasGlasses);
  }
}
